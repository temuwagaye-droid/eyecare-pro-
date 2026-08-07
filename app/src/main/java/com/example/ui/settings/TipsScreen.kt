package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class EyeTip(val title: String, val category: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(onBack: () -> Unit, onOpenPremium: () -> Unit) {
    var waterGlasses by remember { mutableStateOf(5) }

    val tips = listOf(
        EyeTip("Stay Hydrated", "Hydration", "Drink at least 8 glasses of water daily. Dehydration is a primary contributor to dry eyes."),
        EyeTip("Omega-3 Fatty Acids", "Nutrition", "Include salmon, chia seeds, and walnuts in your diet to support tear gland function."),
        EyeTip("Proper Screen Distance", "Ergonomics", "Keep your monitor 20-28 inches away from your eyes, with the top of the screen at eye level."),
        EyeTip("Adjust Room Lighting", "Environment", "Avoid reading or working in a completely dark room to prevent excessive pupil constriction strain."),
        EyeTip("The 20-20-20 Rule", "Habits", "Every 20 minutes, look at an object 20 feet away for 20 seconds to relax your focusing muscles.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Eye Health & Nutrition") },
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
            // Hydration Tracker Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hydration Tracker", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Text("$waterGlasses / 8 Glasses", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (waterGlasses > 0) waterGlasses-- },
                                modifier = Modifier.testTag("water_minus_btn")
                            ) {
                                Text("-")
                            }
                            Button(
                                onClick = { if (waterGlasses < 12) waterGlasses++ },
                                modifier = Modifier.testTag("water_plus_btn")
                            ) {
                                Text("+ Drink Glass")
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expert Clinical Tips",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onOpenPremium, modifier = Modifier.testTag("upgrade_pro_btn")) {
                        Text("✨ Upgrade Pro")
                    }
                }
            }

            items(tips) { tip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tip.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(tip.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text(tip.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumUpgradeModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock EyeCare Pro ✨") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Get lifetime access to advanced features:")
                Text("• Unlimited AI Fatigue & PERCLOS Scans")
                Text("• Advanced Clinical Vision Tests & PDF Export")
                Text("• Full Blue Light & Night Dimmer Scheduler")
                Text("• Ad-free premium experience")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.testTag("subscribe_btn")) {
                Text("Start 7-Day Free Trial")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}
