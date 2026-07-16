package com.winoflex.fluxion.update.network

import com.winoflex.fluxion.update.model.UpdateInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface UpdateApiService {
    @GET
    suspend fun getUpdateInfo(@Url url: String): Response<UpdateInfo>
}
