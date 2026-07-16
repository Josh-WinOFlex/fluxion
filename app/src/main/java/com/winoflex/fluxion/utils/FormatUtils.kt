package com.winoflex.fluxion.utils

import java.util.Locale

object FormatUtils {
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(Locale.US, "%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun formatSpeed(bytesPerSecond: Long): String {
        return "${formatFileSize(bytesPerSecond)}/s"
    }

    fun formatDuration(seconds: Long): String {
        return when {
            seconds < 60 -> "${seconds}s remaining"
            seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s remaining"
            else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m remaining"
        }
    }
}
