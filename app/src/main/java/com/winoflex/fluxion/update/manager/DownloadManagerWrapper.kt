package com.winoflex.fluxion.update.manager

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import java.io.File

/**
 * Wrapper for android.app.DownloadManager to manage APK downloads.
 */
class DownloadManagerWrapper(private val context: Context) {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var currentDownloadId: Long = -1L

    /**
     * Downloads an APK from a URL.
     *
     * @param url The URL of the APK.
     * @param fileName The name to save the APK as.
     * @return The download ID.
     */
    fun downloadApk(url: String, fileName: String): Long {
        // Clear previous download if any (optional)
        // if (currentDownloadId != -1L) cancelDownload()

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Handled by UpdateNotificationHelper
            setTitle("Fluxion Update")
            setDescription("Downloading $fileName")
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            setMimeType("application/vnd.android.package-archive")
        }

        currentDownloadId = downloadManager.enqueue(request)
        return currentDownloadId
    }

    /**
     * Cancels the current download.
     */
    fun cancelDownload() {
        if (currentDownloadId != -1L) {
            downloadManager.remove(currentDownloadId)
            currentDownloadId = -1L
        }
    }

    /**
     * Returns a Flow that emits the status of the current download.
     */
    fun getDownloadProgress(): Flow<DownloadStatus> = callbackFlow {
        if (currentDownloadId == -1L) {
            close()
            return@callbackFlow
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == currentDownloadId) {
                    // Trigger an immediate poll when download completes
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Polling loop for progress
        while (isActive) {
            val query = DownloadManager.Query().setFilterById(currentDownloadId)
            val cursor: Cursor? = downloadManager.query(query)

            if (cursor != null && cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1

                when (status) {
                    DownloadManager.STATUS_PENDING -> trySend(DownloadStatus.Pending)
                    DownloadManager.STATUS_RUNNING -> {
                        val totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        
                        val totalSize = if (totalIndex != -1) cursor.getLong(totalIndex) else 0L
                        val downloadedSize = if (downloadedIndex != -1) cursor.getLong(downloadedIndex) else 0L
                        
                        if (totalSize > 0) {
                            val progress = ((downloadedSize * 100) / totalSize).toInt()
                            trySend(DownloadStatus.Downloading(progress))
                        } else {
                            trySend(DownloadStatus.Downloading(-1)) // Indeterminate
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val uriString = if (uriIndex != -1) cursor.getString(uriIndex) else null
                        val uri = uriString?.let { Uri.parse(it) } ?: Uri.EMPTY
                        
                        // Also try to get local file path if possible
                        val localPathIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
                        val localPath = if (localPathIndex != -1) cursor.getString(localPathIndex) else null
                        
                        trySend(DownloadStatus.Completed(uri, localPath))
                        cursor.close()
                        break // Stop polling
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        val reason = if (reasonIndex != -1) cursor.getInt(reasonIndex) else -1
                        trySend(DownloadStatus.Failed(reason, "Download failed with reason: $reason"))
                        cursor.close()
                        break // Stop polling
                    }
                }
                cursor.close()
            } else {
                // If cursor is null or empty, the download might have been removed
                trySend(DownloadStatus.Failed(-1, "Download not found"))
                cursor?.close()
                break
            }

            delay(500) // Poll every 500ms
        }

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore if not registered
            }
        }
    }
}
