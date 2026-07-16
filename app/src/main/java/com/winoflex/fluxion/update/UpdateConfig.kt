package com.winoflex.fluxion.update

object UpdateConfig {
    const val GITHUB_OWNER = "winoflex"
    const val GITHUB_REPO = "fluxion-mobile"
    const val BASE_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/"
    const val UPDATE_JSON_URL = "https://raw.githubusercontent.com/$GITHUB_OWNER/$GITHUB_REPO/main/update.json"
}
