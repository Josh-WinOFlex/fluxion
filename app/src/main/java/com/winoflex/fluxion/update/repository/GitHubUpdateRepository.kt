package com.winoflex.fluxion.update.repository

import com.winoflex.fluxion.update.model.UpdateInfo
import com.winoflex.fluxion.update.network.UpdateApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubUpdateRepository(
    private val apiService: UpdateApiService,
    private val updateUrl: String
) : UpdateRepository {

    override suspend fun getLatestUpdateInfo(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUpdateInfo(updateUrl)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
