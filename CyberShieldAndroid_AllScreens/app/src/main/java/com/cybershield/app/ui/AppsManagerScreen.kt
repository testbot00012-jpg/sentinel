package com.cybershield.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cybershield.app.core.model.SentinelRiskColors
import com.cybershield.app.core.security.InspectedAppInfo
import com.cybershield.app.ui.viewmodel.MainSecurityViewModel

private val Cyan = Color(0xFF31D7FF)
private val Muted = Color(0xFF8E99AA)
private val DarkBg = Color(0xFF07090D)
private val CardBg = Color(0xFF10141B)
private val Line = Color(0xFF252D39)

@Composable
fun FullAppsManagerScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val apps by vm.inspectedApps.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "RISKY", "SAFE", "SYSTEM"
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(selectedFilter) {
        if (selectedFilter == "SYSTEM") {
            vm.refreshInspectedApps(includeSystem = true)
        } else {
            vm.refreshInspectedApps(includeSystem = false)
        }
    }

    val userApps = remember(apps) { apps.filter { !it.isSystemApp } }
    val riskyApps = remember(userApps) { userApps.filter { it.isRisky } }
    val safeApps = remember(userApps) { userApps.filter { !it.isRisky } }
    val systemApps = remember(apps) { apps.filter { it.isSystemApp } }

    val displayedApps = remember(apps, selectedFilter, searchQuery) {
        val baseList = when (selectedFilter) {
            "RISKY" -> riskyApps
            "SAFE" -> safeApps
            "SYSTEM" -> systemApps
            else -> userApps
        }
        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Header
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
                    Text("Apps Manager", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("100% verified Android package inspection", fontSize = 11.sp, color = Muted, modifier = Modifier.padding(top = 1.dp))
                }
                IconButton(onClick = { vm.refreshInspectedApps(selectedFilter == "SYSTEM") }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Cyan)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        // Summary Metric Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Third-Party App Security Inventory", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text("System OEM apps are strictly separated to prevent false positives.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCounterBox(
                            label = "Safe Apps",
                            count = safeApps.size,
                            color = SentinelRiskColors.SAFE_GREEN,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCounterBox(
                            label = "Risky Apps",
                            count = riskyApps.size,
                            color = if (riskyApps.isEmpty()) SentinelRiskColors.SAFE_GREEN else SentinelRiskColors.DANGER_RED,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCounterBox(
                            label = "Total Scanned",
                            count = userApps.size,
                            color = Cyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed apps by name or package...", color = Muted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = Line,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabChip("All User Apps (${userApps.size})", selected = (selectedFilter == "ALL")) {
                    selectedFilter = "ALL"
                }
                FilterTabChip("Risky (${riskyApps.size})", selected = (selectedFilter == "RISKY"), activeColor = SentinelRiskColors.DANGER_RED) {
                    selectedFilter = "RISKY"
                }
                FilterTabChip("Safe (${safeApps.size})", selected = (selectedFilter == "SAFE"), activeColor = SentinelRiskColors.SAFE_GREEN) {
                    selectedFilter = "SAFE"
                }
                FilterTabChip("System Apps (${if (systemApps.isNotEmpty()) systemApps.size else "OEM"})", selected = (selectedFilter == "SYSTEM"), activeColor = Muted) {
                    selectedFilter = "SYSTEM"
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // App Items List
        if (displayedApps.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SentinelRiskColors.SAFE_GREEN, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("No apps match this category", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("All inspected packages satisfy baseline security constraints.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        } else {
            items(displayedApps) { app ->
                AppInspectionCard(app = app, context = context)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun MetricCounterBox(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(label, fontSize = 10.sp, color = Muted, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun FilterTabChip(
    label: String,
    selected: Boolean,
    activeColor: Color = Cyan,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) activeColor.copy(alpha = 0.2f) else CardBg,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (selected) activeColor else Line),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) activeColor else Muted,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AppInspectionCard(app: InspectedAppInfo, context: Context) {
    var isExpanded by remember { mutableStateOf(false) }
    val scoreColor = SentinelRiskColors.getColorForScore(app.securityScore)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (app.isRisky) scoreColor.copy(alpha = 0.6f) else Line),
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (app.isRisky) scoreColor.copy(alpha = 0.15f) else Color(0xFF131D2A), RoundedCornerShape(10.dp))
                        .border(1.dp, scoreColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        app.appName.take(1).uppercase(),
                        color = scoreColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(12.dp))

                // App Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(app.appName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "${app.packageName} • v${app.versionName}",
                        color = Muted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Text(
                        app.installSource,
                        color = if (app.isSideloaded) SentinelRiskColors.LIGHT_ORANGE else Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Security Score Badge
                Surface(
                    color = scoreColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${app.securityScore}/100",
                            color = scoreColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            app.riskLevel,
                            color = scoreColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dangerous Permissions Chips
            if (app.dangerousPermissions.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    app.dangerousPermissions.forEach { perm ->
                        Surface(
                            color = Color(0xFF2A151B),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, SentinelRiskColors.DANGER_RED.copy(alpha = 0.5f))
                        ) {
                            Text(
                                "⚠ $perm",
                                color = SentinelRiskColors.DANGER_RED,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Expandable Explanations & Manage Action
            if (isExpanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(8.dp))

                Text("Security Assessment:", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                app.riskReasons.forEach { reason ->
                    Text(
                        "• $reason",
                        color = if (reason.contains("Standard", true)) SentinelRiskColors.SAFE_GREEN else Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", app.packageName, null)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open settings: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2636)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text("Manage Permissions in Android Settings", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
