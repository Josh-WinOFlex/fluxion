package com.winoflex.fluxion.update.installer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Helper class to handle the installation of APK files.
 */
object PackageInstallerHelper {

    /**
     * Starts the installation of an APK file.
     *
     * @param context The context to use to start the activity.
     * @param apkFile The APK file to install.
     */
    fun installPackage(context: Context, apkFile: File) {
        val authority = context.packageName + ".fileprovider"
        val apkUri = FileProvider.getUriForFile(context, authority, apkFile)
        installPackage(context, apkUri)
    }

    /**
     * Starts the installation of an APK from its Uri.
     *
     * @param context The context to use to start the activity.
     * @param apkUri The Uri of the APK to install.
     */
    fun installPackage(context: Context, apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle cases where no activity can handle the intent
        }
    }
}
