package com.winoflex.fluxion.update.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateInfo(
    @Json(name = "version") val version: String,
    @Json(name = "versionCode") val versionCode: Int,
    @Json(name = "downloadUrl") val downloadUrl: String,
    @Json(name = "releaseNotes") val releaseNotes: String,
    @Json(name = "fileSize") val fileSize: Long,
    @Json(name = "checksum") val checksum: String? = null,
    @Json(name = "isCritical") val isCritical: Boolean = false,
    @Json(name = "minSdkVersion") val minSdkVersion: Int = 21
)
