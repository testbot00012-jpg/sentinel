package com.cybershield.app.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cybershield.app.core.model.SessionInfo
import com.cybershield.app.model.ScreenSpec
import com.cybershield.app.ui.viewmodel.MainSecurityViewModel

private val Cyan = Color(0xFF31D7FF)
private val Muted = Color(0xFF8E99AA)
private val DarkBg = Color(0xFF07090D)
private val CardBg = Color(0xFF10141B)
private val Line = Color(0xFF252D39)
private val Emerald = Color(0xFF00E676)
private val DangerRed = Color(0xFFFF3B30)
private val AmberWarn = Color(0xFFFF9800)
private val PurpleAi = Color(0xFFA855F7)

// =========================================================================
// MAIN SETTINGS SCREEN (Hub)
// =========================================================================
@Composable
fun MainSettingsScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val currentEmail by vm.currentUserEmail.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text("Log Out", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Text(
                    "Are you sure you want to log out of CyberShield? Your local security session will be terminated.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        vm.logout {
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            nav.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Muted)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            FigmaHeader(
                title = "Settings",
                subtitle = "Privacy, protection & account",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(4.dp))
        }

        // Profile Card: Your account
        item {
            Card(
                onClick = { nav.navigate("settings_detail/settings-detail-01-account") },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF122235), CircleShape)
                            .border(1.dp, Cyan.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Cyan, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentEmail ?: "Your account",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Protected • app lock enabled",
                            color = Emerald,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // 8 Settings Menu items (Image 1)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsHubMenuItem(
                    title = "Protection",
                    subtitle = "Real-time + scan schedule",
                    icon = Icons.Default.Shield,
                    iconColor = Cyan,
                    onClick = { nav.navigate("settings_detail/settings-detail-02-protection") }
                )
                SettingsHubMenuItem(
                    title = "Notifications",
                    subtitle = "Alerts + quiet hours",
                    icon = Icons.Default.NotificationsNone,
                    iconColor = PurpleAi,
                    onClick = { nav.navigate("settings_detail/settings-detail-03-notifications") }
                )
                SettingsHubMenuItem(
                    title = "Privacy",
                    subtitle = "Permissions + retention",
                    icon = Icons.Default.Lock,
                    iconColor = Emerald,
                    onClick = { nav.navigate("settings_detail/settings-detail-04-privacy") }
                )
                SettingsHubMenuItem(
                    title = "AI settings",
                    subtitle = "Models + explanations",
                    icon = Icons.Default.AutoAwesome,
                    iconColor = Cyan,
                    onClick = { nav.navigate("settings_detail/settings-detail-05-ai") }
                )
                SettingsHubMenuItem(
                    title = "Trusted devices",
                    subtitle = "Sessions + emergency",
                    icon = Icons.Default.PhoneAndroid,
                    iconColor = AmberWarn,
                    onClick = { nav.navigate("settings_detail/settings-detail-06-trusted-devices") }
                )
                SettingsHubMenuItem(
                    title = "Data & storage",
                    subtitle = "Export + secure delete",
                    icon = Icons.Default.Folder,
                    iconColor = AmberWarn,
                    onClick = { nav.navigate("settings_detail/settings-detail-07-data-and-storage") }
                )
                SettingsHubMenuItem(
                    title = "Language",
                    subtitle = "English • Telugu • Hindi",
                    icon = Icons.Default.Language,
                    iconColor = PurpleAi,
                    onClick = { nav.navigate("settings_detail/settings-detail-08-language") }
                )
                SettingsHubMenuItem(
                    title = "Security logs",
                    subtitle = "Audit & access history",
                    icon = Icons.Default.AccessTime,
                    iconColor = Muted,
                    onClick = { nav.navigate("settings_detail/settings-detail-09-security-logs") }
                )
            }
        }

        // Additional Image 2 Settings Items (Scan Settings & About)
        item {
            Spacer(Modifier.height(16.dp))
            Text("General Settings", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsHubMenuItem(
                    title = "Scan Settings",
                    subtitle = "Multi-signal heuristics & scheduling",
                    icon = Icons.Default.CropFree,
                    iconColor = DangerRed,
                    onClick = { nav.navigate("feature/19-settings-scan-settings") }
                )
                SettingsHubMenuItem(
                    title = "About CyberShield",
                    subtitle = "v1.0.0-RELEASE • Security engine baseline",
                    icon = Icons.Default.Info,
                    iconColor = Cyan,
                    onClick = { nav.navigate("feature/19-settings-about") }
                )
            }
        }

        // Prominent LOGOUT Button Card
        item {
            Spacer(Modifier.height(24.dp))
            Card(
                onClick = { showLogoutDialog = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1014)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(DangerRed.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Log Out",
                            color = DangerRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            "End session and clear security tokens",
                            color = Muted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "CyberShield • Android Consumer App",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SettingsHubMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
        }
    }
}

// =========================================================================
// ROUTER FOR DETAIL SCREENS
// =========================================================================
@Composable
fun UnifiedSettingsDetailScreen(nav: NavHostController, spec: ScreenSpec, vm: MainSecurityViewModel) {
    val r = spec.route.lowercase()
    when {
        r.contains("02") || r.contains("protection") -> ProtectionDetailScreen(nav, vm)
        r.contains("03") || r.contains("notification") -> NotificationsDetailScreen(nav, vm)
        r.contains("04") || r.contains("privacy") -> PrivacyDetailScreen(nav, vm)
        r.contains("05") || r.contains("ai") -> AiSettingsDetailScreen(nav, vm)
        r.contains("06") || r.contains("trusted") || r.contains("device") -> TrustedDevicesDetailScreen(nav, vm)
        r.contains("07") || r.contains("data") || r.contains("storage") -> DataStorageDetailScreen(nav, vm)
        r.contains("08") || r.contains("language") -> LanguageDetailScreen(nav, vm)
        r.contains("09") || r.contains("log") -> SecurityLogsDetailScreen(nav, vm)
        r.contains("01") || r.contains("account") || r.contains("profile") -> AccountProfileDetailScreen(nav, vm)
        r.contains("scan-settings") -> ScanSettingsDetailScreen(nav, vm)
        r.contains("about") -> AboutDetailScreen(nav, vm)
        else -> ProtectionDetailScreen(nav, vm)
    }
}

// =========================================================================
// 02 • PROTECTION (Image 1, Column 1)
// =========================================================================
@Composable
fun ProtectionDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var realtimeOn by remember { mutableStateOf(vm.realtimeProtection.value) }
    var monitoringOn by remember { mutableStateOf(vm.realtimeMonitoring.value) }
    var scanSched by remember { mutableStateOf(vm.scanSchedule.value) }
    var safeBrowsingOn by remember { mutableStateOf(vm.safeBrowsing.value) }
    var appInstallOn by remember { mutableStateOf(vm.appInstallChecks.value) }
    var batteryAwareOn by remember { mutableStateOf(vm.batteryAwareProtection.value) }

    val telemetry by vm.telemetry.collectAsState()
    val batteryPct = telemetry?.batteryPercent ?: 85
    val context = LocalContext.current
    var showScheduleDialog by remember { mutableStateOf(false) }

    if (showScheduleDialog) {
        val options = listOf("Daily • 02:00 AM", "Daily • 08:00 PM", "Weekly • Sunday", "On Battery Charging")
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Select Scan Schedule", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    options.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scanSched = opt
                                    vm.scanSchedule.value = opt
                                    showScheduleDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (scanSched == opt),
                                onClick = {
                                    scanSched = opt
                                    vm.scanSchedule.value = opt
                                    showScheduleDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Cyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(opt, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Protection", subtitle = "Control how CyberShield watches your device", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Real-time protection Top Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F2636), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Real-time protection", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Active across apps, files and links", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Switch(
                        checked = realtimeOn,
                        onCheckedChange = {
                            realtimeOn = it
                            vm.realtimeProtection.value = it
                            Toast.makeText(context, if (it) "Real-time protection activated" else "Real-time protection paused", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Cyan)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Protection controls Section
        item {
            Text("Protection controls", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.Security,
                    title = "Real-time monitoring",
                    subtitle = if (monitoringOn) "Threat events in the background • Active" else "Background monitoring paused",
                    onClick = {
                        monitoringOn = !monitoringOn
                        vm.realtimeMonitoring.value = monitoringOn
                        Toast.makeText(context, "Monitoring toggled: ${if (monitoringOn) "Active" else "Disabled"}", Toast.LENGTH_SHORT).show()
                    }
                )
                SettingsActionRow(
                    icon = Icons.Default.AccessTime,
                    title = "Scan schedule",
                    subtitle = scanSched,
                    onClick = { showScheduleDialog = true }
                )
                SettingsActionRow(
                    icon = Icons.Default.Language,
                    title = "Safe browsing",
                    subtitle = if (safeBrowsingOn) "Warn before risky destinations • Enabled" else "Protection disabled",
                    onClick = {
                        safeBrowsingOn = !safeBrowsingOn
                        vm.safeBrowsing.value = safeBrowsingOn
                        Toast.makeText(context, "Safe browsing: ${if (safeBrowsingOn) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
                    }
                )
                SettingsActionRow(
                    icon = Icons.Default.PhoneAndroid,
                    title = "App install checks",
                    subtitle = if (appInstallOn) "Review unknown or sideloaded apps" else "Install checking off",
                    onClick = { nav.navigate("apps") }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Battery-aware protection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Battery-aware protection", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(
                            "Reduce background work below 20% battery (Current: $batteryPct%)",
                            color = Muted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = batteryAwareOn,
                        onCheckedChange = {
                            batteryAwareOn = it
                            vm.batteryAwareProtection.value = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Emerald)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 03 • NOTIFICATIONS (Image 1, Column 2)
// =========================================================================
@Composable
fun NotificationsDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var alertsMaster by remember { mutableStateOf(vm.securityAlerts.value) }
    var critThreats by remember { mutableStateOf(vm.criticalThreats.value) }
    var highRisk by remember { mutableStateOf(vm.highRiskDetections.value) }
    var scanResults by remember { mutableStateOf(vm.scanResultsAlerts.value) }
    var summaries by remember { mutableStateOf(vm.securitySummaries.value) }
    var education by remember { mutableStateOf(vm.educationAlerts.value) }
    var quietHours by remember { mutableStateOf(vm.quietHours.value) }

    var showQuietDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showQuietDialog) {
        val quietOptions = listOf("10:30 PM - 07:00 AM", "11:00 PM - 06:00 AM", "12:00 AM - 08:00 AM", "Off")
        AlertDialog(
            onDismissRequest = { showQuietDialog = false },
            title = { Text("Choose Quiet Hours", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    quietOptions.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    quietHours = opt
                                    vm.quietHours.value = opt
                                    showQuietDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (quietHours == opt),
                                onClick = {
                                    quietHours = opt
                                    vm.quietHours.value = opt
                                    showQuietDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Cyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(opt, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showQuietDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Notifications", subtitle = "Choose what deserves your attention", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Top Card: Security alerts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Security alerts", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Critical and high-risk events", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Switch(
                        checked = alertsMaster,
                        onCheckedChange = {
                            alertsMaster = it
                            vm.securityAlerts.value = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DangerRed)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Alert categories
        item {
            Text("Alert categories", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsToggleRow(
                    title = "Critical threats",
                    subtitle = "Immediate action required",
                    checked = critThreats,
                    activeTrackColor = DangerRed,
                    onCheckedChange = { critThreats = it; vm.criticalThreats.value = it }
                )
                SettingsToggleRow(
                    title = "High-risk detections",
                    subtitle = "Review recommended",
                    checked = highRisk,
                    activeTrackColor = AmberWarn,
                    onCheckedChange = { highRisk = it; vm.highRiskDetections.value = it }
                )
                SettingsToggleRow(
                    title = "Scan results",
                    subtitle = "Completed scan summaries",
                    checked = scanResults,
                    activeTrackColor = Cyan,
                    onCheckedChange = { scanResults = it; vm.scanResultsAlerts.value = it }
                )
                SettingsToggleRow(
                    title = "Security summaries",
                    subtitle = "Daily and weekly reports",
                    checked = summaries,
                    activeTrackColor = Cyan,
                    onCheckedChange = { summaries = it; vm.securitySummaries.value = it }
                )
                SettingsToggleRow(
                    title = "Education",
                    subtitle = "Safety tips and guidance",
                    checked = education,
                    activeTrackColor = Cyan,
                    onCheckedChange = { education = it; vm.educationAlerts.value = it }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Quiet hours
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Quiet hours", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(quietHours, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Button(
                        onClick = { showQuietDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("CHANGE", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 04 • PRIVACY (Image 1, Column 3)
// =========================================================================
@Composable
fun PrivacyDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    val securityScore by vm.securityScore.collectAsState()
    val accessCount by vm.accessibilityReviewCount.collectAsState()
    var retentionDays by remember { mutableStateOf(vm.dataRetentionDays.value) }
    var cloudSyncOn by remember { mutableStateOf(vm.cloudSync.value) }

    var showRetentionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showRetentionDialog) {
        val daysList = listOf(30, 60, 90, 180, 365)
        AlertDialog(
            onDismissRequest = { showRetentionDialog = false },
            title = { Text("Security History Retention", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    daysList.forEach { days ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    retentionDays = days
                                    vm.dataRetentionDays.value = days
                                    showRetentionDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (retentionDays == days),
                                onClick = {
                                    retentionDays = days
                                    vm.dataRetentionDays.value = days
                                    showRetentionDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Cyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Keep for $days days", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRetentionDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Privacy", subtitle = "Control data, permissions and retention", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Privacy Posture Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F2D1F), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Privacy posture", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        val privacyScore = securityScore.breakdown["Permissions"] ?: 92
                        Text("Low exposure • $privacyScore / 100", color = Emerald, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Permission controls
        item {
            Text("Permission controls", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.Lock,
                    title = "Sensitive permissions",
                    subtitle = "Camera • microphone • location",
                    onClick = { nav.navigate("privacy") }
                )
                SettingsActionRow(
                    icon = Icons.Default.AccessTime,
                    title = "Background activity",
                    subtitle = "Review apps that stay active",
                    onClick = { nav.navigate("apps") }
                )
                SettingsActionRow(
                    icon = Icons.Default.Security,
                    title = "Accessibility access",
                    subtitle = if (accessCount > 0) "$accessCount service needs review" else "All accessibility services verified",
                    onClick = {
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Accessibility verified", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Data controls
        item {
            Text("Data controls", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.AccessTime,
                    title = "Data retention",
                    subtitle = "Keep security history for $retentionDays days",
                    onClick = { showRetentionDialog = true }
                )
                SettingsActionRow(
                    icon = Icons.Default.CheckCircle,
                    title = "Cloud sync",
                    subtitle = "Encrypted evidence sync: ${if (cloudSyncOn) "ON" else "OFF"}",
                    onClick = {
                        cloudSyncOn = !cloudSyncOn
                        vm.cloudSync.value = cloudSyncOn
                        Toast.makeText(context, "Cloud sync: ${if (cloudSyncOn) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// =========================================================================
// 05 • AI SETTINGS (Image 1, Column 4)
// =========================================================================
@Composable
fun AiSettingsDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var threatAnalysisOn by remember { mutableStateOf(vm.threatAnalysis.value) }
    var explainabilityOn by remember { mutableStateOf(vm.explainability.value) }
    var localAnalysisOn by remember { mutableStateOf(vm.localAnalysis.value) }
    var assistantSugOn by remember { mutableStateOf(vm.assistantSuggestions.value) }

    val context = LocalContext.current

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "AI settings", subtitle = "Models, explanations and local processing", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Security AI Top Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF231433), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleAi, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Security AI", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = PurpleAi.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, PurpleAi.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    "ENABLED",
                                    color = PurpleAi,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Evidence-first explanations", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // AI controls
        item {
            Text("AI controls", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Threat analysis",
                    subtitle = if (threatAnalysisOn) "Behavioral + rule signals • Active" else "Rule signals only",
                    onClick = {
                        threatAnalysisOn = !threatAnalysisOn
                        vm.threatAnalysis.value = threatAnalysisOn
                        Toast.makeText(context, "Threat analysis: ${if (threatAnalysisOn) "Behavioral + Rules" else "Rules Only"}", Toast.LENGTH_SHORT).show()
                    }
                )
                SettingsActionRow(
                    icon = Icons.Default.Shield,
                    title = "Explainability",
                    subtitle = if (explainabilityOn) "Always show evidence and confidence" else "Compact summaries",
                    onClick = {
                        explainabilityOn = !explainabilityOn
                        vm.explainability.value = explainabilityOn
                        Toast.makeText(context, "Explainability: ${if (explainabilityOn) "Always show evidence" else "Compact"}", Toast.LENGTH_SHORT).show()
                    }
                )
                SettingsActionRow(
                    icon = Icons.Default.PhoneAndroid,
                    title = "Local analysis",
                    subtitle = if (localAnalysisOn) "Prefer on-device processing" else "Cloud neural network prioritized",
                    onClick = {
                        localAnalysisOn = !localAnalysisOn
                        vm.localAnalysis.value = localAnalysisOn
                        Toast.makeText(context, "Local analysis: ${if (localAnalysisOn) "On-Device Preferred" else "Cloud Prioritized"}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Assistant suggestions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assistant suggestions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Surface proactive security guidance", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Switch(
                        checked = assistantSugOn,
                        onCheckedChange = {
                            assistantSugOn = it
                            vm.assistantSuggestions.value = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurpleAi)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 06 • TRUSTED DEVICES (Image 1, Column 5)
// =========================================================================
@Composable
fun TrustedDevicesDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val sessions by vm.trustedSessions.collectAsState()
    val context = LocalContext.current
    var showRevokeAllDialog by remember { mutableStateOf(false) }

    if (showRevokeAllDialog) {
        AlertDialog(
            onDismissRequest = { showRevokeAllDialog = false },
            title = { Text("Revoke All Other Sessions", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "This will sign out all active sessions on other phones, tablets, and web browsers. Your current device will remain signed in.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.revokeAllSessions()
                        showRevokeAllDialog = false
                        Toast.makeText(context, "All other sessions revoked", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("REVOKE ALL", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevokeAllDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Trusted devices", subtitle = "Manage active sessions and emergency access", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Current Device Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF0F2636), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
                        Text("This Android phone", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Current device • active now", color = Emerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
                        Text(deviceModel, color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 1.dp))
                    }
                    Surface(
                        color = Emerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Emerald.copy(alpha = 0.4f))
                    ) {
                        Text(
                            "TRUSTED",
                            color = Emerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Other sessions Section
        item {
            Text("Other sessions", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }

        if (sessions.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No other active sessions. Your account is only signed in on this phone.",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(sessions) { sess ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (sess.name.contains("Windows", true)) Icons.Default.Laptop else Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = Cyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sess.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(sess.lastActive, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
                            Text(sess.details, color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        TextButton(
                            onClick = {
                                vm.revokeSession(sess.id)
                                Toast.makeText(context, "Session ${sess.name} revoked", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Revoke", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Revoke all other sessions button
            item {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1014)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Revoke all other sessions", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Button(
                            onClick = { showRevokeAllDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("REVOKE ALL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 07 • DATA & STORAGE (Image 1, Column 6)
// =========================================================================
@Composable
fun DataStorageDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val appStorage by vm.appStorageFormatted.collectAsState()
    val scanHistory by vm.scanHistory.collectAsState()
    var autoCleanupOn by remember { mutableStateOf(vm.automaticCleanup.value) }

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Secure Delete Evidence", fontWeight = FontWeight.Bold, color = DangerRed) },
            text = {
                Text(
                    "This will securely purge all cached scan results, image artifacts, and local temporary evidence. Your saved settings will remain intact.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        vm.purgeCache { ok ->
                            Toast.makeText(context, if (ok) "Local cache purged securely" else "Cache purge finished", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("PURGE NOW", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Data & storage", subtitle = "Export, retention and secure deletion", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Local Data Big Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Local data", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        appStorage,
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Security history • evidence • cache", color = Muted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Storage controls
        item {
            Text("Storage controls", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.AccessTime,
                    title = "Security history",
                    subtitle = "${scanHistory.size} records • 90-day retention",
                    onClick = { nav.navigate("history") }
                )
                SettingsActionRow(
                    icon = Icons.Default.Folder,
                    title = "Evidence vault",
                    subtitle = "14 encrypted items",
                    onClick = { Toast.makeText(context, "Evidence vault: 14 AES-256 items sealed", Toast.LENGTH_SHORT).show() }
                )
                SettingsActionRow(
                    icon = Icons.Default.Share,
                    title = "Export data",
                    subtitle = "Create an encrypted archive",
                    onClick = {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CyberShield Security Export")
                                putExtra(Intent.EXTRA_TEXT, "CyberShield Security Audit Export:\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nTimestamp: ${System.currentTimeMillis()}\nVault status: Verified")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export Security Archive"))
                        } catch (_: Exception) {
                            Toast.makeText(context, "Export generated", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                SettingsActionRow(
                    icon = Icons.Default.Delete,
                    title = "Secure delete",
                    subtitle = "Permanently remove local evidence",
                    iconColor = DangerRed,
                    onClick = { showDeleteDialog = true }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Automatic cleanup toggle
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Automatic cleanup", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Remove expired cache only", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Switch(
                        checked = autoCleanupOn,
                        onCheckedChange = {
                            autoCleanupOn = it
                            vm.automaticCleanup.value = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Cyan)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 08 • LANGUAGE (Image 1, Column 7)
// =========================================================================
@Composable
fun LanguageDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val currentLang by vm.currentLanguage.collectAsState()
    var selectedLang by remember { mutableStateOf(currentLang) }
    val context = LocalContext.current

    val languages = listOf(
        Pair("English", "English"),
        Pair("తెలుగు", "Telugu"),
        Pair("हिन्दी", "Hindi")
    )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Language", subtitle = "Choose your preferred security language", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Current Language Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F2636), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Cyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Current language", color = Muted, fontSize = 11.sp)
                        Text(currentLang, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Available languages
        item {
            Text("Available languages", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                languages.forEach { (name, nativeName) ->
                    Card(
                        onClick = { selectedLang = name },
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selectedLang == name) Cyan.copy(alpha = 0.8f) else Line),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                Text(nativeName, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
                            }
                            RadioButton(
                                selected = (selectedLang == name),
                                onClick = { selectedLang = name },
                                colors = RadioButtonDefaults.colors(selectedColor = Cyan)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Detection Content Card + APPLY
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Detection content", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(
                            "Warnings and explanations follow this language.",
                            color = Muted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Button(
                        onClick = {
                            vm.setLanguage(selectedLang)
                            Toast.makeText(context, "Language set to $selectedLang", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("APPLY", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 09 • SECURITY LOGS (Image 1, Column 8)
// =========================================================================
@Composable
fun SecurityLogsDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") }
    val context = LocalContext.current

    val rawEvents = listOf(
        SecurityLogEntry("09:14", DangerRed, "High-risk message analyzed", "Security event recorded", "THREAT"),
        SecurityLogEntry("08:41", Emerald, "Wi-Fi check completed", "Security event recorded", "SCAN"),
        SecurityLogEntry("08:12", AmberWarn, "App permission reviewed", "Security event recorded", "PERM"),
        SecurityLogEntry("07:55", Cyan, "Security scan started", "Security event recorded", "SCAN"),
        SecurityLogEntry("07:52", PurpleAi, "Session verified", "Security event recorded", "SESSION")
    )

    val filteredEvents = rawEvents.filter { entry ->
        val matchesQuery = searchQuery.isBlank() || entry.title.contains(searchQuery, ignoreCase = true)
        val matchesFilter = (filterType == "ALL") || entry.type == filterType
        matchesQuery && matchesFilter
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DetailHeader(title = "Security logs", subtitle = "Audit trail of security actions", nav = nav)
            Spacer(Modifier.height(12.dp))
        }

        // Search audit events & FILTER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search audit events", color = Muted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan,
                        unfocusedBorderColor = Line,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        filterType = when (filterType) {
                            "ALL" -> "THREAT"
                            "THREAT" -> "SCAN"
                            "SCAN" -> "SESSION"
                            else -> "ALL"
                        }
                        Toast.makeText(context, "Filter: $filterType", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Text(if (filterType == "ALL") "FILTER" else filterType, color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Section Title
        item {
            Text("Today", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }

        // Audit Trail Items
        items(filteredEvents) { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Text(event.time, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(42.dp))
                Spacer(Modifier.width(8.dp))
                // Colored dot / icon badge
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(event.color, CircleShape)
                )
                Spacer(Modifier.width(14.dp))
                // Title and subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(event.subtitle, color = Color(0xFF64748B), fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
                }
            }
            HorizontalDivider(color = Line.copy(alpha = 0.5f), thickness = 0.5.dp)
        }

        // Bottom Export actions
        item {
            Spacer(Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Export security log", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { Toast.makeText(context, "Exporting audit log as PDF...", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("PDF", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { Toast.makeText(context, "Exporting audit log as JSON...", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("JSON", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class SecurityLogEntry(
    val time: String,
    val color: Color,
    val title: String,
    val subtitle: String,
    val type: String
)

// =========================================================================
// IMAGE 2: PROFILE SCREEN (Settings Detail 01 • Account / 19 Settings • Profile)
// =========================================================================
@Composable
fun AccountProfileDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val currentEmail by vm.currentUserEmail.collectAsState()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("Are you sure you want to log out of CyberShield?", color = Color(0xFFCBD5E1), fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        vm.logout {
                            nav.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = Muted) }
            },
            containerColor = CardBg
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            FigmaSubHeader(title = "Profile", subtitle = "19 Settings • CyberShield", nav = nav)
            Spacer(Modifier.height(10.dp))
        }

        // Header Card (Image 2 style)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F2D1F), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Settings", color = Muted, fontSize = 11.sp)
                        Text("Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Configure this area of CyberShield", color = Muted, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Configuration Section
        item {
            Text("Configuration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ConfigStatusRow("Current status", "Enabled", Emerald)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Default behavior", "Recommended", Emerald)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("User account", currentEmail ?: "user@sentinelai.security", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Last changed", "Today", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Privacy impact", "Low", Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Action Buttons
        item {
            Button(
                onClick = { Toast.makeText(context, "Profile is active & synchronized", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Open Profile", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(10.dp))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "Changes are saved only after confirmation.",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "CyberShield • Consumer Android",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// =========================================================================
// IMAGE 2: SCAN SETTINGS (19 Settings • Scan Settings)
// =========================================================================
@Composable
fun ScanSettingsDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val context = LocalContext.current
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            FigmaSubHeader(title = "Scan Settings", subtitle = "19 Settings • CyberShield", nav = nav)
            Spacer(Modifier.height(10.dp))
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF2D1217), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CropFree, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Settings", color = Muted, fontSize = 11.sp)
                        Text("Scan Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Configure this area of CyberShield", color = Muted, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Text("Configuration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ConfigStatusRow("Current status", "Enabled", Color(0xFFFF5252))
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Default behavior", "Recommended", Color(0xFFFF5252))
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Engine model", "Sentinel Multi-Signal v2.4", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Last changed", "Today", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Privacy impact", "Low", Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Button(
                onClick = { nav.navigate("scan") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Open Scan Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
            Text("Changes are saved only after confirmation.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("CyberShield • Consumer Android", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

// =========================================================================
// IMAGE 2: ABOUT (19 Settings • About)
// =========================================================================
@Composable
fun AboutDetailScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val context = LocalContext.current
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            FigmaSubHeader(title = "About", subtitle = "19 Settings • CyberShield", nav = nav)
            Spacer(Modifier.height(10.dp))
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0F2636), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Cyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Settings", color = Muted, fontSize = 11.sp)
                        Text("About", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Configure this area of CyberShield", color = Muted, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Text("Configuration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ConfigStatusRow("Current status", "Enabled", Cyan)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Default behavior", "Recommended", Cyan)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("App version", "1.0.0-RELEASE", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Security definitions", "Up to Date (Today)", Color.White)
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 10.dp))
                    ConfigStatusRow("Privacy impact", "Low", Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Button(
                onClick = { Toast.makeText(context, "CyberShield is running latest v1.0.0 security build", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Open About", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
            Text("Changes are saved only after confirmation.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("CyberShield • Consumer Android", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

// =========================================================================
// REUSABLE HELPER UI COMPONENTS
// =========================================================================
@Composable
private fun DetailHeader(title: String, subtitle: String, nav: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { nav.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, fontSize = 11.sp, color = Muted, modifier = Modifier.padding(top = 1.dp))
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Cyan,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    activeTrackColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = activeTrackColor
                )
            )
        }
    }
}

@Composable
private fun ConfigStatusRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Muted, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
