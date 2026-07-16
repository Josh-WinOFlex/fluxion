package com.winoflex.fluxion.update.manager

import android.content.Context
import com.winoflex.fluxion.update.repository.UpdateRepositoryProvider

/**
 * Provider for the UpdateManager singleton.
 */
object UpdateManagerProvider {
    @Volatile
    private var instance: UpdateManager? = null

    /**
     * Returns the singleton instance of UpdateManager.
     */
    fun getInstance(context: Context): UpdateManager {
        return instance ?: synchronized(this) {
            instance ?: UpdateManager(
                context.applicationContext,
                UpdateRepositoryProvider.repository,
                DownloadManagerWrapper(context.applicationContext)
            ).also { instance = it }
        }
    }
}
