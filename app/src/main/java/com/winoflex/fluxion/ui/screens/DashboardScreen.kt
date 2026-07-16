package com.winoflex.fluxion.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winoflex.fluxion.ui.components.FluxionButton
import com.winoflex.fluxion.ui.components.FluxionCard
import com.winoflex.fluxion.ui.theme.FluxionTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DashboardScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            MeterStatusHeader()
        }

        item {
            MainMetricsGrid()
        }

        item {
            Text(
                text = "Live Power Usage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LiveUsageChartCard()
        }

        item {
            AIInsightsSection()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* TODO */ }) {
                    Text("View All", color = MaterialTheme.colorScheme.primary)
                }
            }
            RecentActivityList()
        }

        item {
            FluxionButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quick Recharge")
            }
        }
    }
}

@Composable
fun MeterStatusHeader() {
    FluxionCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Smart Meter #FLX-9021",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Connected",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            StatusIndicator(isOnline = true)
        }
    }
}

@Composable
fun StatusIndicator(isOnline: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isOnline) Color(0xFF00E676) else Color.Gray)
        )
        Text(
            text = if (isOnline) "Online" else "Offline",
            style = MaterialTheme.typography.labelLarge,
            color = if (isOnline) Color(0xFF00E676) else Color.Gray
        )
    }
}

@Composable
fun MainMetricsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Balance",
                value = "$ 42.50",
                icon = Icons.Rounded.AccountBalanceWallet,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Today's Usage",
                value = "12.4 kWh",
                icon = Icons.Rounded.Today
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Current Power",
                value = "450 W",
                icon = Icons.Rounded.Bolt
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Voltage",
                value = "230 V",
                icon = Icons.Rounded.ElectricMeter
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    FluxionCard(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (containerColor == MaterialTheme.colorScheme.surface) 
                    MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (containerColor == MaterialTheme.colorScheme.surface)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (containerColor == MaterialTheme.colorScheme.surface)
                    MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LiveUsageChartCard() {
    FluxionCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            LiveUsageChart()
        }
    }
}

@Composable
fun LiveUsageChart() {
    val points = remember { mutableStateListOf<Float>() }
    val maxPoints = 50

    LaunchedEffect(Unit) {
        while (true) {
            val nextValue = Random.nextFloat() * 100f + 200f // Simulated watts
            if (points.size >= maxPoints) {
                points.removeAt(0)
            }
            points.add(nextValue)
            delay(500)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (points.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val spacing = width / (maxPoints - 1)
        val maxVal = 500f // Max scale
        
        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - (value / maxVal) * height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Gradient fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo((points.size - 1) * spacing, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}

@Composable
fun AIInsightsSection() {
    FluxionCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "AI Insight",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Your usage is 10% lower than yesterday. You're on track to save $5 this month!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun RecentActivityList() {
    val activities = listOf(
        ActivityItem("Recharge", "Success", "$ 20.00", "2 hours ago", Icons.Rounded.AddCard),
        ActivityItem("High Usage Alert", "Warning", "Kitchen", "5 hours ago", Icons.Rounded.Warning),
        ActivityItem("Bill Paid", "Success", "$ 35.20", "Yesterday", Icons.Rounded.Payment)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        activities.forEach { activity ->
            FluxionCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activity.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = activity.value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activity.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

data class ActivityItem(
    val title: String,
    val subtitle: String,
    val value: String,
    val time: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    FluxionTheme {
        DashboardScreen()
    }
}
