package com.winoflex.fluxion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winoflex.fluxion.ui.theme.FluxionTheme
import com.winoflex.fluxion.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutProjectScreen(onBack: () -> Unit) {
    val developers = listOf(
        Developer("Joshua Samuel", "WinOFlex Developer"),
        Developer("Mohamed Farhaan", "Developer"),
        Developer("Akilarasan", "Developer"),
        Developer("Dharneish", "Developer"),
        Developer("Praneesh", "Developer")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Project") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Project Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fluxion: AIoT-Driven Smart Prepaid Energy Meter Billing System with Real-Time Monitoring. A comprehensive solution for modern energy management.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Our Team",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(developers) { developer ->
                DeveloperItem(developer)
            }
        }
    }
}

@Composable
fun DeveloperItem(developer: Developer) {
    Surface(
        shape = Shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Rounded.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = developer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = developer.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class Developer(val name: String, val role: String)

@Preview(showBackground = true)
@Composable
fun AboutProjectScreenPreview() {
    FluxionTheme {
        AboutProjectScreen(onBack = {})
    }
}
