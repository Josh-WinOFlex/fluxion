package com.winoflex.fluxion.network

import retrofit2.http.GET

interface FluxionApiService {
    @GET("placeholder")
    suspend fun getPlaceholderData(): String
}
