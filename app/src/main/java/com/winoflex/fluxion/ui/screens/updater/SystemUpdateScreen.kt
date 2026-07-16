package com.winoflex.fluxion.ui.screens.updater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winoflex.fluxion.ui.theme.FluxionTheme
import com.winoflex.fluxion.ui.viewmodel.UpdateUiState
import com.winoflex.fluxion.ui.viewmodel.UpdateViewModel
import com.winoflex.fluxion.update.model.UpdateStatus
import com.winoflex.fluxion.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemUpdateScreen(
    viewModel: UpdateViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Update", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        SystemUpdateContent(
            uiState = uiState,
            onCheck = { viewModel.checkForUpdates() },
            onDownload = { viewModel.startDownload() },
            onInstall = { viewModel.installUpdate() },
            onRetry = { viewModel.checkForUpdates() },
            onCancel = { viewModel.cancelDownload() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
fun SystemUpdateContent(
    uiState: UpdateUiState,
    onCheck: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Status Header
        UpdateHeader(uiState)

        // Progress Section
        AnimatedVisibility(
            visible = uiState.status == UpdateStatus.DOWNLOADING || 
                      uiState.status == UpdateStatus.VERIFYING || 
                      uiState.status == UpdateStatus.INSTALLING
        ) {
            UpdateProgress(uiState)
        }

        // Release Notes
        uiState.updateInfo?.let { info ->
            ReleaseNotes(info.releaseNotes)
        }

        // Mandatory Warning
        if (uiState.isMandatory) {
            MandatoryWarning()
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        UpdateActions(
            uiState = uiState,
            onCheck = onCheck,
            onDownload = onDownload,
            onInstall = onInstall,
            onRetry = onRetry,
            onCancel = onCancel
        )
    }
}

@Composable
fun UpdateHeader(uiState: UpdateUiState) {
    val (icon, title, subtitle) = when (uiState.status) {
        UpdateStatus.IDLE -> Triple(Icons.Rounded.CheckCircle, "You're up to date", "Last checked: Just now")
        UpdateStatus.CHECKING -> Triple(Icons.Rounded.Refresh, "Checking for updates...", "This won't take long")
        UpdateStatus.UPDATE_AVAILABLE -> Triple(Icons.Rounded.SystemUpdate, "Update available", "Version ${uiState.updateInfo?.version}")
        UpdateStatus.DOWNLOADING -> Triple(Icons.Rounded.SystemUpdate, "Downloading...", "Stay connected to Wi-Fi for faster download")
        UpdateStatus.PAUSED -> Triple(Icons.Rounded.Info, "Download paused", "Waiting to resume")
        UpdateStatus.VERIFYING -> Triple(Icons.Rounded.Info, "Verifying update...", "Ensuring everything is correct")
        UpdateStatus.READY_TO_INSTALL -> Triple(Icons.Rounded.CheckCircle, "Ready to install", "Installation will restart Fluxion")
        UpdateStatus.INSTALLING -> Triple(Icons.Rounded.Refresh, "Installing update...", "Fluxion is being updated")
        UpdateStatus.COMPLETED -> Triple(Icons.Rounded.CheckCircle, "Update completed", "Your system is now current")
        UpdateStatus.ERROR -> Triple(Icons.Rounded.Error, "Something went wrong", "We couldn't complete the update")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = if (uiState.status == UpdateStatus.ERROR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Column {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun UpdateProgress(uiState: UpdateUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinearProgressIndicator(
            progress = { if (uiState.progress >= 0) uiState.progress / 100f else 0f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        val details = uiState.downloadDetails
        if (details != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${FormatUtils.formatFileSize(details.downloadedBytes)} / ${FormatUtils.formatFileSize(details.totalBytes)}",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "${FormatUtils.formatSpeed(details.speedBytesPerSecond)} • ${FormatUtils.formatDuration(details.etaSeconds)}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        } else {
            Text(
                text = if (uiState.progress >= 0) "${uiState.progress}% completed" else "Preparing...",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ReleaseNotes(notes: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Release Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = notes,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MandatoryWarning() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Error, contentDescription = null)
            Text(
                "An update is required before using Fluxion Mobile.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UpdateActions(
    uiState: UpdateUiState,
    onCheck: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
    ) {
        when (uiState.status) {
            UpdateStatus.IDLE -> {
                Button(onClick = onCheck) { Text("Check for updates") }
            }
            UpdateStatus.UPDATE_AVAILABLE -> {
                TextButton(onClick = onCheck) { Text("Check again") }
                Button(onClick = onDownload) { Text("Download") }
            }
            UpdateStatus.DOWNLOADING, UpdateStatus.PAUSED -> {
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
            UpdateStatus.READY_TO_INSTALL -> {
                Button(onClick = onInstall) { Text("Install now") }
            }
            UpdateStatus.ERROR -> {
                Button(onClick = onRetry) { Text("Retry") }
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SystemUpdateScreenPreview() {
    FluxionTheme {
        SystemUpdateContent(
            uiState = UpdateUiState(
                status = UpdateStatus.UPDATE_AVAILABLE,
                updateInfo = com.winoflex.fluxion.update.model.UpdateInfo(
                    version = "1.2.0",
                    versionCode = 12,
                    downloadUrl = "",
                    releaseNotes = "• Improved performance\n• Fixed minor bugs\n• New dashboard features",
                    fileSize = 1024 * 1024 * 15,
                    isCritical = true
                ),
                progress = 45,
                isMandatory = true
            ),
            onCheck = {},
            onDownload = {},
            onInstall = {},
            onRetry = {},
            onCancel = {}
        )
    }
}
