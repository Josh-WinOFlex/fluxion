package com.winoflex.fluxion.update.repository

import com.winoflex.fluxion.update.model.UpdateInfo

interface UpdateRepository {
    suspend fun getLatestUpdateInfo(): Result<UpdateInfo>
}
