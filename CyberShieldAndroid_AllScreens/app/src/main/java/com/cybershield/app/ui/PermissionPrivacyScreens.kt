package com.cybershield.app.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cybershield.app.core.security.InspectedAppInfo
import com.cybershield.app.ui.viewmodel.MainSecurityViewModel

private val DarkBg = Color(0xFF090D14)
private val CardBg = Color(0xFF101622)
private val Line = Color(0xFF1E2838)
private val Cyan = Color(0xFF00E5FF)
private val Muted = Color(0xFF7E8B9B)
private val Emerald = Color(0xFF00E676)
private val DangerRed = Color(0xFFFF3B30)
private val AmberWarn = Color(0xFFF59E0B)

/**
 * Privacy Center Screen (Image 1, Top-Left "08 Privacy Center")
 * Represents the main "Protect" tab in CyberShield.
 */
@Composable
fun PrivacyCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val inspectedApps by vm.inspectedApps.collectAsState()
    val privacyScore = remember(inspectedApps) { vm.getPrivacyScore() }

    val cameraApps = remember(inspectedApps) { vm.getAppsWithPermission("Camera") }
    val micApps = remember(inspectedApps) { vm.getAppsWithPermission("Microphone") }
    val locApps = remember(inspectedApps) { vm.getAppsWithPermission("Location") }
    val contactsApps = remember(inspectedApps) { vm.getAppsWithPermission("Contacts") }
    val smsApps = remember(inspectedApps) { vm.getAppsWithPermission("SMS") }
    val phoneApps = remember(inspectedApps) { vm.getAppsWithPermission("Phone") }
    val storageApps = remember(inspectedApps) { vm.getAppsWithPermission("Storage") }
    val accessibilityApps = remember(inspectedApps) { vm.getAppsWithPermission("Accessibility") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header: "Privacy / Permissions & background access" + Bell icon
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Privacy",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Permissions & background access",
                        fontSize = 12.sp,
                        color = Muted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    color = Color(0xFF121822),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { nav.navigate("alerts") }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Alerts",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Privacy Risk Card (LOW 92/100, Circular score badge 92 SAFE)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Privacy risk",
                            color = Muted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        val riskTitle = if (privacyScore >= 80) "LOW" else if (privacyScore >= 60) "MODERATE" else "HIGH"
                        val riskColor = if (privacyScore >= 80) Emerald else if (privacyScore >= 60) AmberWarn else DangerRed
                        Text(
                            text = riskTitle,
                            color = riskColor,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "$privacyScore / 100",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Circular Score Badge
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color(0xFF0B141E), CircleShape)
                            .border(2.dp, Cyan.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$privacyScore",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (privacyScore >= 80) "SAFE" else "RISK",
                                color = if (privacyScore >= 80) Emerald else DangerRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
        }

        // Sensitive Permissions Section Title
        item {
            Text(
                text = "Sensitive permissions",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // 1. Camera Item
        item {
            PermissionSummaryRow(
                title = "Camera",
                countText = "${cameraApps.size} apps",
                icon = Icons.Default.Lock,
                iconColor = AmberWarn,
                badgeText = if (cameraApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (cameraApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Camera") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 2. Microphone Item
        item {
            PermissionSummaryRow(
                title = "Microphone",
                countText = "${micApps.size} app${if (micApps.size == 1) "" else "s"}",
                icon = Icons.Default.Lock,
                iconColor = Cyan,
                badgeText = if (micApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (micApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Microphone") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 3. Location Item
        item {
            PermissionSummaryRow(
                title = "Location",
                countText = "${locApps.size} apps",
                icon = Icons.Default.Lock,
                iconColor = AmberWarn,
                badgeText = if (locApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (locApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Location") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 4. Contacts Item
        item {
            PermissionSummaryRow(
                title = "Contacts",
                countText = "${contactsApps.size} apps",
                icon = Icons.Default.Lock,
                iconColor = Emerald,
                badgeText = if (contactsApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (contactsApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Contacts") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 5. SMS Item
        item {
            PermissionSummaryRow(
                title = "SMS",
                countText = "${smsApps.size} app${if (smsApps.size == 1) "" else "s"}",
                icon = Icons.Default.Lock,
                iconColor = Emerald,
                badgeText = if (smsApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (smsApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/SMS") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 6. Phone Item
        item {
            PermissionSummaryRow(
                title = "Phone",
                countText = "${phoneApps.size} apps",
                icon = Icons.Default.Lock,
                iconColor = Cyan,
                badgeText = if (phoneApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (phoneApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Phone") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 7. Storage Item
        item {
            PermissionSummaryRow(
                title = "Storage",
                countText = "${storageApps.size} apps",
                icon = Icons.Default.Lock,
                iconColor = Cyan,
                badgeText = if (storageApps.any { it.isRisky }) "Review" else "Good",
                badgeColor = if (storageApps.any { it.isRisky }) AmberWarn else Emerald,
                onClick = { nav.navigate("permission_detail/Storage") }
            )
            Spacer(Modifier.height(10.dp))
        }

        // 8. Accessibility Item
        item {
            PermissionSummaryRow(
                title = "Accessibility",
                countText = "${accessibilityApps.size.coerceAtLeast(1)} service${if (accessibilityApps.size == 1) "" else "s"}",
                icon = Icons.Default.Lock,
                iconColor = AmberWarn,
                badgeText = "Review",
                badgeColor = AmberWarn,
                onClick = { nav.navigate("permission_detail/Accessibility") }
            )
        }
    }
}

@Composable
private fun PermissionSummaryRow(
    title: String,
    countText: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF131A26), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = countText,
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            Surface(
                color = badgeColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Detailed Permission Screen (Images 2-8: 04 Permission & Privacy • [Camera/Microphone/etc.])
 * Lists the EXACT real apps on the user's device that have this permission.
 */
@Composable
fun PermissionPrivacyDetailScreen(
    permissionName: String,
    nav: NavHostController,
    vm: MainSecurityViewModel
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val appsWithPermission = remember(permissionName) { vm.getAppsWithPermission(permissionName) }

    val filteredApps = remember(appsWithPermission, searchQuery) {
        if (searchQuery.isBlank()) {
            appsWithPermission
        } else {
            appsWithPermission.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val dangerCount = remember(filteredApps) { filteredApps.count { it.isRisky } }
    val safeCount = remember(filteredApps) { filteredApps.count { !it.isRisky } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Bar
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text = permissionName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "04 Permission & Privacy • Sentinel AI",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
        }

        // Archetype 1: Insight / Warning Card (matching Figma Screen 3 & 7)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131726)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF251A14), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, null, tint = AmberWarn, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Insight", color = AmberWarn, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Security signal evaluated", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Confidence 91% • ${appsWithPermission.size} active applications holding $permissionName hardware / telemetry hooks on this device.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Archetype 2: "Why it matters" & "Safe next action" Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Why it matters", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "This screen focuses strictly on $permissionName. Applications with this permission can access real-time device inputs. Review third-party apps below.",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Surface(
                        color = Color(0xFF0F1722),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Line),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Safe next action", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Review details below before changing Android OS security settings.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_PRIVACY_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberWarn),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open System Permission Manager", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Search & Filter Box (from Figma Screen 2 & 6: "Search / filter: Active")
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed apps with $permissionName...", color = Muted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Cyan, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = Line,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(10.dp))

            // Filter status chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFF121B27),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line)
                ) {
                    Text(
                        "Total: ${filteredApps.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = if (dangerCount > 0) DangerRed.copy(alpha = 0.15f) else Color(0xFF121B27),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (dangerCount > 0) DangerRed.copy(alpha = 0.5f) else Line)
                ) {
                    Text(
                        "Danger (3rd-Party): $dangerCount",
                        color = if (dangerCount > 0) DangerRed else Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = Emerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Emerald.copy(alpha = 0.4f))
                ) {
                    Text(
                        "Safe: $safeCount",
                        color = Emerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Section Title: Apps Granted This Permission
        item {
            Text(
                "Apps granted $permissionName (${filteredApps.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (filteredApps.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Emerald, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No matching apps found", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("No applications on this device match your filter criteria.", color = Muted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredApps) { app ->
                AppPermissionItemCard(app = app, permissionName = permissionName, context = context)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AppPermissionItemCard(
    app: InspectedAppInfo,
    permissionName: String,
    context: android.content.Context
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (app.isRisky) DangerRed.copy(alpha = 0.4f) else Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (app.isRisky) Color(0xFF2B1214) else Color(0xFF102232),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (app.isRisky) Icons.Default.Warning else Icons.Default.Apps,
                        contentDescription = null,
                        tint = if (app.isRisky) DangerRed else Cyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = app.packageName,
                        color = Muted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 1.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val originLabel = when {
                            app.isSystemApp -> "System App"
                            app.isPlayStore -> "Google Play"
                            else -> "Third-Party Sideloaded"
                        }
                        val originColor = when {
                            app.isSystemApp -> Emerald
                            app.isPlayStore -> Emerald
                            else -> DangerRed
                        }

                        Surface(
                            color = originColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = originLabel,
                                color = originColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Score: ${app.securityScore}/100",
                            color = if (app.securityScore >= 90) Emerald else DangerRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Action: "Review" opens Android OS App Details Settings directly!
            OutlinedButton(
                onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", app.packageName, null)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (app.isRisky) DangerRed else Cyan
                ),
                border = BorderStroke(1.dp, if (app.isRisky) DangerRed.copy(alpha = 0.7f) else Cyan.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = if (app.isRisky) "Review" else "Inspect",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
