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
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.service.EyeBreakReceiver
import com.example.service.EyeFloatService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class BreakType(val id: String, val title: String, val desc: String, val icon: ImageVector) {
    RULE_20_20_20("20_20_20", "20-20-20 Distance Focus", "Look 20 feet away for 20 seconds to relax ciliary muscles.", Icons.Default.Visibility),
    BLINK_HYDRATE("blink", "Deep Blink & Hydrate", "10 intentional slow blinks to replenish tear film and soothe dry eyes.", Icons.Default.WaterDrop),
    EYE_STRETCH("stretch", "Figure-8 & Eye Roll", "Gentle eye movements in 8-patterns to relieve ocular tension.", Icons.Default.SelfImprovement),
    PALMING_REST("palming", "Warm Palming Rest", "Cup warm palms over closed eyes for 30s of deep darkness.", Icons.Default.Spa)
}

enum class SoundMode(val id: String, val label: String, val icon: ImageVector) {
    ALL("all", "Sound & Vibrate", Icons.Default.NotificationsActive),
    VIBRATE("vibrate", "Vibrate Only", Icons.Default.Vibration),
    SILENT("silent", "Silent Banner", Icons.Default.NotificationsOff)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    forceElegantDark: Boolean,
    onToggleElegantDark: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenPremium: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(EyeBreakReceiver.PREF_NAME, Context.MODE_PRIVATE) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var remindersEnabled by remember {
        mutableStateOf(prefs.getBoolean(EyeBreakReceiver.KEY_ENABLED, true))
    }
    var selectedInterval by remember {
        mutableStateOf(prefs.getLong(EyeBreakReceiver.KEY_INTERVAL, 20L))
    }
    var selectedSoundMode by remember {
        mutableStateOf(prefs.getString(EyeBreakReceiver.KEY_SOUND_MODE, SoundMode.ALL.id) ?: SoundMode.ALL.id)
    }
    var selectedBreakType by remember {
        mutableStateOf(prefs.getString(EyeBreakReceiver.KEY_BREAK_TYPE, BreakType.RULE_20_20_20.id) ?: BreakType.RULE_20_20_20.id)
    }
    var quietHoursEnabled by remember {
        mutableStateOf(prefs.getBoolean(EyeBreakReceiver.KEY_QUIET_HOURS, false))
    }
    var floatingActive by remember {
        mutableStateOf(false)
    }
    var waterGlasses by remember {
        mutableStateOf(5)
    }
    var showCustomInputDialog by remember {
        mutableStateOf(false)
    }
    var customMinutesText by remember {
        mutableStateOf("")
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            remindersEnabled = true
            prefs.edit().putBoolean(EyeBreakReceiver.KEY_ENABLED, true).apply()
            EyeBreakReceiver.scheduleAlarm(context, selectedInterval)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Eye break reminders enabled every $selectedInterval mins!")
            }
        }
    }

    val presetIntervals = listOf(
        5L to "5m Micro",
        10L to "10m Short",
        15L to "15m Focus",
        20L to "20m Standard",
        30L to "30m Balance",
        45L to "45m Deep",
        60L to "60m Hourly"
    )

    fun applyInterval(mins: Long) {
        val boundedMins = mins.coerceIn(1L, 180L)
        selectedInterval = boundedMins
        prefs.edit().putLong(EyeBreakReceiver.KEY_INTERVAL, boundedMins).apply()
        if (remindersEnabled) {
            EyeBreakReceiver.scheduleAlarm(context, boundedMins)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Reminder interval updated to $boundedMins minutes")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings & Reminders",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Customize eye health break frequency & alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        EyeBreakReceiver.showInstantNotification(
                            context,
                            "👁️ Reminder Frequency: ${selectedInterval}m",
                            "Current active break routine: ${BreakType.values().find { it.id == selectedBreakType }?.title ?: "20-20-20 Rule"}"
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Test alert dispatched!")
                        }
                    }, modifier = Modifier.testTag("top_test_notification_btn")) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Test Notification",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Reminder Switch & Status Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("master_reminder_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (remindersEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (remindersEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (remindersEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        tint = if (remindersEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Eye Break Reminders",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (remindersEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (remindersEnabled) "Active • Every $selectedInterval mins" else "Disabled • Tap to activate",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (remindersEnabled) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Eye break reminders scheduled every $selectedInterval minutes")
                                            }
                                        }
                                    } else {
                                        remindersEnabled = false
                                        prefs.edit().putBoolean(EyeBreakReceiver.KEY_ENABLED, false).apply()
                                        EyeBreakReceiver.cancelAlarm(context)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Eye break reminders paused")
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("break_reminders_switch")
                            )
                        }

                        if (remindersEnabled) {
                            val nextTime = remember(selectedInterval) {
                                val cal = Calendar.getInstance().apply {
                                    add(Calendar.MINUTE, selectedInterval.toInt())
                                }
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Next Scheduled Break:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = "~ $nextTime",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Frequency Customization Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("frequency_customization_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reminder Frequency",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$selectedInterval mins",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Text(
                            text = "Choose how often you receive gentle alerts to look away and rest your eyes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick Presets
                        Text(
                            text = "Quick Presets",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Presets Chips Grid / Row
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                presetIntervals.take(4).forEach { (mins, label) ->
                                    FilterChip(
                                        selected = selectedInterval == mins,
                                        onClick = { applyInterval(mins) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("interval_chip_$mins"),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                presetIntervals.drop(4).forEach { (mins, label) ->
                                    FilterChip(
                                        selected = selectedInterval == mins,
                                        onClick = { applyInterval(mins) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("interval_chip_$mins"),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                                // Custom Button
                                OutlinedButton(
                                    onClick = {
                                        customMinutesText = selectedInterval.toString()
                                        showCustomInputDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("custom_interval_btn"),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Custom", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Continuous Slider Adjuster
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fine Tune Frequency",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$selectedInterval min interval",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Slider(
                                value = selectedInterval.toFloat(),
                                onValueChange = { newMins ->
                                    selectedInterval = newMins.toLong()
                                },
                                onValueChangeFinished = {
                                    applyInterval(selectedInterval)
                                },
                                valueRange = 5f..120f,
                                steps = 22,
                                modifier = Modifier.testTag("frequency_slider")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("5 min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("20 min (20-20-20)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text("120 min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Stepper row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { applyInterval(selectedInterval - 5) },
                                modifier = Modifier.weight(1f).testTag("decrement_5m_btn"),
                                enabled = selectedInterval > 5
                            ) {
                                Text("- 5 Minutes")
                            }
                            Button(
                                onClick = { applyInterval(selectedInterval + 5) },
                                modifier = Modifier.weight(1f).testTag("increment_5m_btn"),
                                enabled = selectedInterval < 120
                            ) {
                                Text("+ 5 Minutes")
                            }
                        }
                    }
                }
            }

            // Break Routine Type Customization
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Preferred Break Routine", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        BreakType.values().forEach { breakType ->
                            val isSelected = selectedBreakType == breakType.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedBreakType = breakType.id
                                        prefs.edit().putString(EyeBreakReceiver.KEY_BREAK_TYPE, breakType.id).apply()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Break routine set: ${breakType.title}")
                                        }
                                    }
                                    .testTag("routine_${breakType.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedBreakType = breakType.id
                                            prefs.edit().putString(EyeBreakReceiver.KEY_BREAK_TYPE, breakType.id).apply()
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        breakType.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = breakType.title,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = breakType.desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notification Sound & Style Customization
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Alert Sound & Haptics", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SoundMode.values().forEach { mode ->
                                val isSelected = selectedSoundMode == mode.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedSoundMode = mode.id
                                        prefs.edit().putString(EyeBreakReceiver.KEY_SOUND_MODE, mode.id).apply()
                                    },
                                    leadingIcon = {
                                        Icon(mode.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(mode.label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.weight(1f).testTag("sound_chip_${mode.id}")
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Quiet Hours
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Do Not Disturb / Quiet Hours", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text("Pause reminder notifications overnight (10:00 PM – 7:00 AM).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = quietHoursEnabled,
                                onCheckedChange = { enabled ->
                                    quietHoursEnabled = enabled
                                    prefs.edit().putBoolean(EyeBreakReceiver.KEY_QUIET_HOURS, enabled).apply()
                                },
                                modifier = Modifier.testTag("quiet_hours_switch")
                            )
                        }

                        Button(
                            onClick = {
                                EyeBreakReceiver.showInstantNotification(
                                    context,
                                    "👁️ Test Break Reminder",
                                    "Look 20 feet away for 20 seconds to rest your eyes! (Frequency: ${selectedInterval}m)"
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sample eye health notification dispatched!")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_notification_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Test Notification Now")
                        }
                    }
                }
            }

            // Theme & Display Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Display & System Preferences", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                        // Force Elegant Dark
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Force Elegant Dark Theme", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text("Enforce eye-comfort dark palette to reduce glare during night use.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = forceElegantDark,
                                onCheckedChange = onToggleElegantDark,
                                modifier = Modifier.testTag("force_dark_switch")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Floating overlay button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Floating Quick Break Overlay", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text("Display a floating button over other apps for instant breaks anywhere.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // Hydration Tracker Quick Goal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
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
                                Text("Hydration Goal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Text("$waterGlasses / 8 Glasses", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { if (waterGlasses > 0) waterGlasses-- },
                                modifier = Modifier.testTag("water_minus_btn")
                            ) {
                                Text("- Decrease")
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

            // Clinical Tips & Pro Upgrade
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Eye Health Best Practices",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onOpenPremium, modifier = Modifier.testTag("upgrade_pro_btn")) {
                                Text("✨ Pro Features")
                            }
                        }
                        Text("• Follow the 20-20-20 rule during prolonged digital screen use.")
                        Text("• Keep monitors 20-28 inches away at slight downward angle.")
                        Text("• Blink deliberately when concentrating on code, games, or spreadsheets.")
                        Text("• Use ambient lighting to match display brightness.")
                    }
                }
            }
        }
    }

    // Custom Interval Dialog
    if (showCustomInputDialog) {
        AlertDialog(
            onDismissRequest = { showCustomInputDialog = false },
            title = { Text("Set Custom Reminder Frequency") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your desired break reminder interval in minutes (1 to 180):", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = customMinutesText,
                        onValueChange = { customMinutesText = it.filter { char -> char.isDigit() } },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_minutes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = customMinutesText.toLongOrNull()
                        if (mins != null && mins in 1..180) {
                            applyInterval(mins)
                            showCustomInputDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_custom_minutes_btn")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
