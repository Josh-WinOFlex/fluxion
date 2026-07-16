package com.winoflex.fluxion.update.repository

import com.winoflex.fluxion.update.UpdateConfig
import com.winoflex.fluxion.update.network.UpdateApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object UpdateRepositoryProvider {
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(UpdateConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UpdateApiService::class.java)
    }

    val repository: UpdateRepository by lazy {
        GitHubUpdateRepository(apiService, UpdateConfig.UPDATE_JSON_URL)
    }
}
