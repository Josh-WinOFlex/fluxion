package com.winoflex.fluxion.update.manager

import android.net.Uri

/**
 * Represents the status of a download.
 */
sealed class DownloadStatus {
    object Pending : DownloadStatus()
    data class Downloading(val progress: Int) : DownloadStatus()
    data class Completed(val uri: Uri, val localPath: String?) : DownloadStatus()
    data class Failed(val reason: Int, val message: String?) : DownloadStatus()
}
