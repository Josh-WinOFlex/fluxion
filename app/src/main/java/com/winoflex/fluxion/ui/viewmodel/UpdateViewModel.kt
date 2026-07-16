package com.winoflex.fluxion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winoflex.fluxion.update.manager.UpdateManager
import com.winoflex.fluxion.update.model.UpdateInfo
import com.winoflex.fluxion.update.model.UpdateStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UpdateUiState(
    val status: UpdateStatus = UpdateStatus.IDLE,
    val updateInfo: UpdateInfo? = null,
    val progress: Int = 0,
    val isMandatory: Boolean = false
)

class UpdateViewModel(
    private val updateManager: UpdateManager
) : ViewModel() {

    val uiState: StateFlow<UpdateUiState> = combine(
        updateManager.status,
        updateManager.updateInfo,
        updateManager.progress
    ) { status, info, progress ->
        UpdateUiState(
            status = status,
            updateInfo = info,
            progress = progress,
            isMandatory = info?.isCritical ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UpdateUiState(
            status = updateManager.status.value,
            updateInfo = updateManager.updateInfo.value,
            progress = updateManager.progress.value,
            isMandatory = updateManager.isUpdateMandatory
        )
    )

    fun checkForUpdates() = updateManager.checkForUpdates()
    fun startDownload() = updateManager.startDownload()
    fun pauseDownload() = updateManager.pauseDownload()
    fun resumeDownload() = updateManager.resumeDownload()
    fun cancelDownload() = updateManager.cancelDownload()
    fun installUpdate() = updateManager.installUpdate()
}
