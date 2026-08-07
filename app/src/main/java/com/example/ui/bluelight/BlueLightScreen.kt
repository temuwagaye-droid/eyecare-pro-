package com.example.ui.bluelight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueLightScreen(
    viewModel: BlueLightViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Selected Color Theme and Tint Calculation
    val tintColor = when (uiState.selectedColorTheme) {
        "Amber" -> Color(0xFFFF9800)
        "Red" -> Color(0xFFD32F2F)
        "Yellow" -> Color(0xFFFFEB3B)
        "Green" -> Color(0xFF4CAF50)
        "Sepia" -> Color(0xFF795548)
        else -> Color(0xFFFF9800)
    }.copy(alpha = (uiState.opacityPercent / 100f) * 0.5f)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Blue Light Filter & Dimmer") },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("back_btn")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Master Toggle Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Blue Light Shield",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Reduce eye fatigue during night use",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = uiState.isEnabled,
                            onCheckedChange = { viewModel.toggleEnabled(it) },
                            modifier = Modifier.testTag("filter_switch")
                        )
                    }
                }

                // Full System Dim Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Full System Dimmer Overlay", fontWeight = FontWeight.Bold)
                            Text("Apply dim filter over notification bar and all apps", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = uiState.isFullSystemDim,
                            onCheckedChange = { viewModel.toggleFullSystemDim(it) },
                            modifier = Modifier.testTag("system_dim_switch")
                        )
                    }
                }

                // Color Option Selector Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Filter Color Option", fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Amber", "Red", "Yellow", "Green", "Sepia").forEach { theme ->
                                FilterChip(
                                    selected = uiState.selectedColorTheme == theme,
                                    onClick = { viewModel.selectColorTheme(theme) },
                                    label = { Text(theme) },
                                    modifier = Modifier.testTag("color_chip_$theme")
                                )
                            }
                        }
                    }
                }

                // Opacity Slider Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dimmer Intensity", fontWeight = FontWeight.Bold)
                            Text("${uiState.opacityPercent}%", color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = uiState.opacityPercent.toFloat(),
                            onValueChange = { viewModel.updateOpacity(it.toInt()) },
                            valueRange = 10f..90f,
                            steps = 8,
                            modifier = Modifier.testTag("opacity_slider")
                        )
                    }
                }

                // Options: Reading Mode & Auto-schedule
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Reading Mode (Sepia)", fontWeight = FontWeight.Bold)
                                Text("Enhanced paper-like contrast for eBooks", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = uiState.isReadingModeEnabled,
                                onCheckedChange = { viewModel.toggleReadingMode(it) },
                                modifier = Modifier.testTag("reading_mode_switch")
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Auto-Schedule (Sunset to Sunrise)", fontWeight = FontWeight.Bold)
                                Text("Automatically activate filter based on local time", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = uiState.isAutoScheduleEnabled,
                                onCheckedChange = { viewModel.toggleAutoSchedule(it) },
                                modifier = Modifier.testTag("auto_schedule_switch")
                            )
                        }
                    }
                }
            }
        }

        // Full System / In-App Dimmer Overlay Preview if enabled
        if (uiState.isEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tintColor)
            )
        }
    }
}
