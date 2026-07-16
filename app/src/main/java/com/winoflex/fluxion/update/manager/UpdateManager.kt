package com.winoflex.fluxion.update.manager

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import com.winoflex.fluxion.update.installer.PackageInstallerHelper
import com.winoflex.fluxion.update.model.UpdateInfo
import com.winoflex.fluxion.update.model.UpdateStatus
import com.winoflex.fluxion.update.repository.UpdateRepository
import com.winoflex.fluxion.update.utils.SecurityUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

/**
 * Manages the update process: checking, downloading, verifying, and installing.
 */
class UpdateManager(
    private val context: Context,
    private val repository: UpdateRepository,
    private val downloadManagerWrapper: DownloadManagerWrapper
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var downloadJob: Job? = null

    private val _status = MutableStateFlow(UpdateStatus.IDLE)
    val status: StateFlow<UpdateStatus> = _status.asStateFlow()

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private var downloadedFile: File? = null

    /**
     * Checks for updates using the repository.
     */
    fun checkForUpdates() {
        if (_status.value == UpdateStatus.CHECKING) return

        scope.launch {
            _status.value = UpdateStatus.CHECKING
            val result = repository.getLatestUpdateInfo()
            result.onSuccess { info ->
                if (isUpdateAvailable(info)) {
                    _updateInfo.value = info
                    _status.value = UpdateStatus.UPDATE_AVAILABLE
                    Log.d("UpdateManager", "Update available: ${info.version} (${info.versionCode})")
                } else {
                    _status.value = UpdateStatus.IDLE
                    Log.d("UpdateManager", "No update available")
                }
            }.onFailure { e ->
                _status.value = UpdateStatus.ERROR
                Log.e("UpdateManager", "Failed to check for updates", e)
            }
        }
    }

    /**
     * Determines if an update is available based on version code and name.
     */
    private fun isUpdateAvailable(info: UpdateInfo): Boolean {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            val currentVersionName = packageInfo.versionName ?: ""

            // Primary check: versionCode
            if (info.versionCode > currentVersionCode) return true

            // Secondary check: versionName (if versionCode is same, check if name is "higher")
            if (info.versionCode == currentVersionCode.toInt()) {
                return isVersionNameHigher(info.version, currentVersionName)
            }

            false
        } catch (e: Exception) {
            Log.e("UpdateManager", "Error getting package info", e)
            false
        }
    }

    /**
     * Compares version names in semver-like format.
     */
    private fun isVersionNameHigher(newVersion: String, currentVersion: String): Boolean {
        val newParts = newVersion.split('.').mapNotNull { it.trim().toIntOrNull() }
        val currentParts = currentVersion.split('.').mapNotNull { it.trim().toIntOrNull() }
        
        val length = maxOf(newParts.size, currentParts.size)
        for (i in 0 until length) {
            val newVal = newParts.getOrElse(i) { 0 }
            val currVal = currentParts.getOrElse(i) { 0 }
            if (newVal > currVal) return true
            if (newVal < currVal) return false
        }
        return false
    }

    /**
     * Starts downloading the update.
     */
    fun startDownload() {
        val info = _updateInfo.value ?: return
        if (_status.value == UpdateStatus.DOWNLOADING) return

        _status.value = UpdateStatus.DOWNLOADING
        val fileName = "fluxion_update_${info.versionCode}.apk"
        downloadManagerWrapper.downloadApk(info.downloadUrl, fileName)

        downloadJob?.cancel()
        downloadJob = scope.launch {
            downloadManagerWrapper.getDownloadProgress().collect { downloadStatus ->
                when (downloadStatus) {
                    is DownloadStatus.Downloading -> {
                        _progress.value = downloadStatus.progress
                    }
                    is DownloadStatus.Completed -> {
                        _progress.value = 100
                        verifyAndPrepareInstallation(downloadStatus.localPath)
                        this.cancel() // Stop collecting once completed
                    }
                    is DownloadStatus.Failed -> {
                        Log.e("UpdateManager", "Download failed: ${downloadStatus.message}")
                        _status.value = UpdateStatus.ERROR
                        this.cancel()
                    }
                    DownloadStatus.Pending -> {
                        _progress.value = 0
                    }
                }
            }
        }
    }

    /**
     * Verifies the downloaded file and sets status to READY_TO_INSTALL.
     */
    private fun verifyAndPrepareInstallation(localPath: String?) {
        val info = _updateInfo.value ?: return
        if (localPath == null) {
            _status.value = UpdateStatus.ERROR
            return
        }

        // localPath might be a URI string or a file path. 
        // DownloadManagerWrapper usually returns a path if available.
        val file = if (localPath.startsWith("content://") || localPath.startsWith("file://")) {
            // Need to handle URI if it's not a direct path, but SecurityUtils needs File
            // In most cases with setDestinationInExternalFilesDir, it's a file path
            File(localPath.removePrefix("file://"))
        } else {
            File(localPath)
        }

        downloadedFile = file

        if (!info.checksum.isNullOrBlank()) {
            _status.value = UpdateStatus.VERIFYING
            scope.launch(Dispatchers.IO) {
                val isValid = SecurityUtils.verifySha256(file, info.checksum)
                withContext(Dispatchers.Main) {
                    if (isValid) {
                        _status.value = UpdateStatus.READY_TO_INSTALL
                    } else {
                        Log.e("UpdateManager", "Checksum verification failed")
                        _status.value = UpdateStatus.ERROR
                    }
                }
            }
        } else {
            _status.value = UpdateStatus.READY_TO_INSTALL
        }
    }

    /**
     * Pauses the download (if supported by the underlying mechanism).
     */
    fun pauseDownload() {
        // DownloadManager doesn't natively support pause/resume easily via ID.
        _status.value = UpdateStatus.PAUSED
    }

    /**
     * Resumes the download (if supported by the underlying mechanism).
     */
    fun resumeDownload() {
        _status.value = UpdateStatus.DOWNLOADING
    }

    /**
     * Cancels the current download.
     */
    fun cancelDownload() {
        downloadJob?.cancel()
        downloadManagerWrapper.cancelDownload()
        _status.value = UpdateStatus.IDLE
        _progress.value = 0
        downloadedFile = null
    }

    /**
     * Installs the downloaded update.
     */
    fun installUpdate() {
        val file = downloadedFile
        if (file == null || !file.exists()) {
            _status.value = UpdateStatus.ERROR
            return
        }
        _status.value = UpdateStatus.INSTALLING
        PackageInstallerHelper.installPackage(context, file)
    }
}
