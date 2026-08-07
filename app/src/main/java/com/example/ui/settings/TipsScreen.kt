package com.example.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.service.EyeBreakReceiver
import com.example.service.EyeFloatService

data class EyeTip(val title: String, val category: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(
    forceElegantDark: Boolean,
    onToggleElegantDark: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenPremium: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(EyeBreakReceiver.PREF_NAME, Context.MODE_PRIVATE) }
    var remindersEnabled by remember { mutableStateOf(prefs.getBoolean(EyeBreakReceiver.KEY_ENABLED, false)) }
    var selectedInterval by remember { mutableStateOf(prefs.getLong(EyeBreakReceiver.KEY_INTERVAL, 20L)) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            remindersEnabled = true
            prefs.edit().putBoolean(EyeBreakReceiver.KEY_ENABLED, true).apply()
            EyeBreakReceiver.scheduleAlarm(context, selectedInterval)
        }
    }

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
                title = { Text("Settings & Eye Health Tips") },
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
            // Theme & Appearance Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Force Elegant Dark Theme", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Enforce eye-saving dark palette or follow device system settings.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = forceElegantDark,
                            onCheckedChange = onToggleElegantDark,
                            modifier = Modifier.testTag("force_dark_switch")
                        )
                    }
                }
            }

            // Screen Break Reminders Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Screen Break Reminders", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Periodic alerts to rest your eyes (20-20-20 rule).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = remindersEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            remindersEnabled = true
                                            prefs.edit().putBoolean(EyeBreakReceiver.KEY_ENABLED, true).apply()
                                            EyeBreakReceiver.scheduleAlarm(context, selectedInterval)
                                        }
                                    } else {
                                        remindersEnabled = false
                                        prefs.edit().putBoolean(EyeBreakReceiver.KEY_ENABLED, false).apply()
                                        EyeBreakReceiver.cancelAlarm(context)
                                    }
                                },
                                modifier = Modifier.testTag("break_reminders_switch")
                            )
                        }

                        if (remindersEnabled) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Text("Reminder Interval", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(10L to "10 min", 20L to "20 min", 30L to "30 min", 60L to "1 hour").forEach { (mins, label) ->
                                    FilterChip(
                                        selected = selectedInterval == mins,
                                        onClick = {
                                            selectedInterval = mins
                                            prefs.edit().putLong(EyeBreakReceiver.KEY_INTERVAL, mins).apply()
                                            if (remindersEnabled) {
                                                EyeBreakReceiver.scheduleAlarm(context, mins)
                                            }
                                        },
                                        label = { Text(label) },
                                        modifier = Modifier.testTag("interval_chip_$mins")
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { EyeBreakReceiver.showInstantNotification(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_notification_btn")
                        ) {
                            Text("Test Break Notification Now")
                        }
                    }
                }
            }

            // Floating Button Over Other Apps Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var floatingActive by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Floating Quick Break Button", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Display a floating button over other apps for instant breaks without opening the app.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = floatingActive,
                                onCheckedChange = { active ->
                                    if (active) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                            val intent = Intent(
                                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                Uri.parse("package:${context.packageName}")
                                            )
                                            context.startActivity(intent)
                                        } else {
                                            floatingActive = true
                                            EyeFloatService.startService(context)
                                        }
                                    } else {
                                        floatingActive = false
                                        EyeFloatService.stopService(context)
                                    }
                                },
                                modifier = Modifier.testTag("floating_button_switch")
                            )
                        }
                    }
                }
            }

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

            // Developer Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Developer Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Temeselew Buta (Temu)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Lead Android & AI Vision Software Engineer dedicated to creating professional eye wellness solutions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
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
