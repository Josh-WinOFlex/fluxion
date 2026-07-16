package com.winoflex.fluxion.repository

import com.winoflex.fluxion.network.FluxionApiService

class FluxionRepository(
    private val apiService: FluxionApiService
) {
    suspend fun getData(): String {
        return "Placeholder data"
    }
}
