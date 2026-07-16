package com.winoflex.fluxion.update.model

data class DownloadDetails(
    val downloadedBytes: Long,
    val totalBytes: Long,
    val speedBytesPerSecond: Long = 0,
    val etaSeconds: Long = 0
)
