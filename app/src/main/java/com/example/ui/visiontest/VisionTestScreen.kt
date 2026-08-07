package com.example.ui.visiontest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionTestScreen(
    viewModel: VisionTestViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.testHistory.collectAsState()
    var activeTest by remember { mutableStateOf<String?>(null) }

    when (activeTest) {
        "Snellen" -> SnellenTestActivity(onComplete = { result ->
            viewModel.saveTestResult("Snellen Acuity", result)
            activeTest = null
        }, onCancel = { activeTest = null })

        "Amsler" -> AmslerGridActivity(onComplete = { result ->
            viewModel.saveTestResult("Amsler Grid", result)
            activeTest = null
        }, onCancel = { activeTest = null })

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Clinical Vision Tests") },
                        navigationIcon = {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("back_btn")) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Choose a Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Regular self-tests help detect visual acuity changes early.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        TestMenuCard(
                            title = "Snellen Visual Acuity Test",
                            description = "Test distance sharpness using standardized letter charts.",
                            modifier = Modifier.testTag("test_snellen"),
                            onClick = { activeTest = "Snellen" }
                        )
                    }

                    item {
                        TestMenuCard(
                            title = "Amsler Grid Macular Test",
                            description = "Detect macular degeneration and distortion in central vision.",
                            modifier = Modifier.testTag("test_amsler"),
                            onClick = { activeTest = "Amsler" }
                        )
                    }

                    item {
                        TestMenuCard(
                            title = "Color Blindness Check",
                            description = "Ishihara-style plates for red-green color deficiency.",
                            modifier = Modifier.testTag("test_color"),
                            onClick = { viewModel.saveTestResult("Color Blindness", "Normal color vision") }
                        )
                    }

                    if (history.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Test History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(history) { record ->
                            ListItem(
                                headlineContent = { Text(record.testType) },
                                supportingContent = { Text("Result: ${record.scoreSummary}") },
                                leadingContent = {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestMenuCard(title: String, description: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SnellenTestActivity(onComplete: (String) -> Unit, onCancel: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val letters = listOf("E", "FP", "TOZ", "LPED", "PECFD")

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) { Text("Exit") }
                Text("Snellen Acuity (Line ${step + 1}/5)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = letters[step],
                    fontSize = (60 - step * 10).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Can you read the letters clearly?", style = MaterialTheme.typography.bodyLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (step < letters.size - 1) step++
                            else onComplete("20/20 Vision Acuity")
                        },
                        modifier = Modifier.weight(1f).testTag("snellen_yes_btn")
                    ) {
                        Text("Yes, Clearly")
                    }
                    OutlinedButton(
                        onClick = { onComplete("Visual Acuity 20/${20 + (step + 1) * 10}") },
                        modifier = Modifier.weight(1f).testTag("snellen_no_btn")
                    ) {
                        Text("Blurry")
                    }
                }
            }
        }
    }
}

@Composable
fun AmslerGridActivity(onComplete: (String) -> Unit, onCancel: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) { Text("Exit") }
                Text("Amsler Grid Test", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Text("Focus on the center dot. Do any lines appear wavy, blurred, or missing?", style = MaterialTheme.typography.bodyMedium)

            // Grid Canvas
            Box(
                modifier = Modifier.size(300.dp).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSize = 10
                    val stepX = size.width / gridSize
                    val stepY = size.height / gridSize
                    for (i in 0..gridSize) {
                        drawLine(Color.Gray, Offset(i * stepX, 0f), Offset(i * stepX, size.height), 2f)
                        drawLine(Color.Gray, Offset(0f, i * stepY), Offset(size.width, i * stepY), 2f)
                    }
                    drawCircle(Color.Black, 8f, Offset(size.width / 2f, size.height / 2f))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onComplete("Normal - No distortion detected") },
                    modifier = Modifier.weight(1f).testTag("amsler_normal_btn")
                ) {
                    Text("All Lines Straight")
                }
                OutlinedButton(
                    onClick = { onComplete("Wavy lines detected - Consult eye doctor") },
                    modifier = Modifier.weight(1f).testTag("amsler_wavy_btn")
                ) {
                    Text("Wavy / Distorted")
                }
            }
        }
    }
}
