package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToBlueLight: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToFatigue: () -> Unit,
    onNavigateToVisionTest: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var ideaInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_logo_1786061571283),
                                contentDescription = "App Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text("EyeCare Pro", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setAppIdeaDialogVisible(true) }, modifier = Modifier.testTag("send_idea_button")) {
                        Icon(Icons.Default.Send, contentDescription = "Send App Idea")
                    }
                    IconButton(onClick = onNavigateToTips, modifier = Modifier.testTag("tips_button")) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Eye Health Tips")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Professional Logo & App Idea Banner Card
            Card(
                onClick = { viewModel.setAppIdeaDialogVisible(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_idea_banner_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1786061571283),
                            contentDescription = "Professional Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Have an App Idea or Feedback?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap here to send messages & feature suggestions directly to the team.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Eye Strain Risk Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("risk_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Eye Strain Risk",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.eyeStrainRisk,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Based on ${uiState.screenTimeMinutes} mins today & ambient light (${uiState.ambientLux} lux)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageIconsForRisk(uiState.eyeStrainRisk),
                            contentDescription = "Risk Status",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 20-20-20 Rule Reminder Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rule_20_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "20-20-20 Rule Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                            Text("Active", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 1f - (uiState.timeToNextBreakSeconds / 1200f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next break in ${uiState.timeToNextBreakSeconds / 60} mins",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = { viewModel.triggerBreakReminder() }) {
                            Text("Take Break Now")
                        }
                    }
                }
            }

            // Quick Actions Grid
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "AI Fatigue Scan",
                    subtitle = "Blink rate & PERCLOS",
                    icon = Icons.Default.CameraAlt,
                    modifier = Modifier.weight(1f).testTag("btn_fatigue"),
                    onClick = onNavigateToFatigue
                )
                ActionCard(
                    title = "Eye Exercises",
                    subtitle = "Palming & Focus",
                    icon = Icons.Default.SelfImprovement,
                    modifier = Modifier.weight(1f).testTag("btn_exercises"),
                    onClick = onNavigateToExercises
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "Blue Light Filter",
                    subtitle = "1000K-5000K Dimmer",
                    icon = Icons.Default.WbSunny,
                    modifier = Modifier.weight(1f).testTag("btn_bluelight"),
                    onClick = onNavigateToBlueLight
                )
                ActionCard(
                    title = "Vision Tests",
                    subtitle = "Snellen & Amsler",
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f).testTag("btn_vision"),
                    onClick = onNavigateToVisionTest
                )
            }
        }
    }

    // App Idea Messaging Dialog
    if (uiState.isAppIdeaDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.setAppIdeaDialogVisible(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1786061571283),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text("Send App Idea & Feedback")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (uiState.appIdeaStatusMessage != null) {
                        Text(
                            text = uiState.appIdeaStatusMessage!!,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text("Describe your app idea, feature suggestion, or feedback below:")
                        OutlinedTextField(
                            value = ideaInput,
                            onValueChange = { ideaInput = it },
                            placeholder = { Text("E.g., Add dark mode schedules or audio reminders...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp).testTag("app_idea_input"),
                            maxLines = 5
                        )
                    }
                }
            },
            confirmButton = {
                if (uiState.appIdeaStatusMessage == null) {
                    Button(
                        onClick = { viewModel.sendAppIdeaMessage(ideaInput) },
                        modifier = Modifier.testTag("submit_app_idea_btn")
                    ) {
                        Text("Send Message")
                    }
                } else {
                    Button(
                        onClick = {
                            ideaInput = ""
                            viewModel.setAppIdeaDialogVisible(false)
                        },
                        modifier = Modifier.testTag("close_app_idea_btn")
                    ) {
                        Text("Done")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setAppIdeaDialogVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Full screen gentle reminder dialog when break is triggered
    if (uiState.isBreakActive) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissBreak() },
            icon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Time for your 20-20-20 Break!") },
            text = { Text("Look at least 20 feet away from your screen at an object for at least 20 seconds to relax your eye muscles.") },
            confirmButton = {
                Button(onClick = { viewModel.dismissBreak() }, modifier = Modifier.testTag("dismiss_break_btn")) {
                    Text("I'm Ready")
                }
            }
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun imageIconsForRisk(risk: String) = when (risk) {
    "High" -> Icons.Default.Warning
    "Moderate" -> Icons.Default.Info
    else -> Icons.Default.CheckCircle
}

