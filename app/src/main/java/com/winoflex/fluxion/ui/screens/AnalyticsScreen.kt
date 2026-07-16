package com.winoflex.fluxion.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.winoflex.fluxion.ui.components.FluxionCard
import com.winoflex.fluxion.ui.theme.FluxionTheme

import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun AnalyticsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Daily", "Weekly", "Monthly")

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "${tabs[selectedTab]} Consumption",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ConsumptionChartCard(selectedTab)
            }

            item {
                MetricsSummary(selectedTab)
            }

            item {
                Text(
                    text = "Appliance Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ApplianceBreakdownList()
            }
        }
    }
}

@Composable
fun ConsumptionChartCard(tabIndex: Int) {
    FluxionCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total: 145.2 kWh",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BarChart(tabIndex)
        }
    }
}

@Composable
fun BarChart(tabIndex: Int) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val data = remember(tabIndex) {
        when (tabIndex) {
            0 -> List(24) { (10..100).random().toFloat() } // 24 hours
            1 -> List(7) { (200..800).random().toFloat() } // 7 days
            else -> List(12) { (1000..5000).random().toFloat() } // 12 months
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val barCount = data.size
        val barSpacing = 8.dp.toPx()
        val barWidth = (width - (barCount - 1) * barSpacing) / barCount
        val maxVal = data.maxOrNull() ?: 1f

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * height
            val x = index * (barWidth + barSpacing)
            val y = height - barHeight
            
            drawRect(
                color = primaryColor.copy(alpha = if (index == data.size - 1) 1f else 0.4f),
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
            )
        }
    }
}

@Composable
fun MetricsSummary(tabIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricSummaryCard(
            modifier = Modifier.weight(1f),
            label = "Peak Usage",
            value = "4.2 kWh",
            icon = Icons.AutoMirrored.Rounded.TrendingUp
        )
        MetricSummaryCard(
            modifier = Modifier.weight(1f),
            label = "Average",
            value = "1.8 kWh",
            icon = Icons.Rounded.Timeline
        )
    }
}

@Composable
fun MetricSummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector
) {
    FluxionCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ApplianceBreakdownList() {
    val items = listOf(
        ApplianceItem("Air Conditioner", "45%", "65.3 kWh", Icons.Rounded.AcUnit, Color(0xFF4FC3F7)),
        ApplianceItem("Refrigerator", "20%", "29.0 kWh", Icons.Rounded.Kitchen, Color(0xFFFFB74D)),
        ApplianceItem("Lighting", "15%", "21.8 kWh", Icons.Rounded.Light, Color(0xFFFFF176)),
        ApplianceItem("Others", "20%", "29.1 kWh", Icons.Rounded.DevicesOther, Color(0xFFAED581))
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            FluxionCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = item.percentage,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = item.color
                        )
                        Text(
                            text = item.usage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

data class ApplianceItem(
    val name: String,
    val percentage: String,
    val usage: String,
    val icon: ImageVector,
    val color: Color
)

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    FluxionTheme {
        AnalyticsScreen()
    }
}
