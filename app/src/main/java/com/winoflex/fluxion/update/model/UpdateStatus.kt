package com.winoflex.fluxion.update.model

enum class UpdateStatus {
    IDLE,
    CHECKING,
    UPDATE_AVAILABLE,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    READY_TO_INSTALL,
    INSTALLING,
    COMPLETED,
    ERROR
}
