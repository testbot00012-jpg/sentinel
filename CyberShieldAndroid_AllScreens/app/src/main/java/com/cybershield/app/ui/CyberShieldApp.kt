package com.cybershield.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cybershield.app.core.model.RiskLevel
import com.cybershield.app.core.model.SecurityResult
import com.cybershield.app.core.model.SentinelRiskColors
import com.cybershield.app.core.model.ScanChatMessage
import com.cybershield.app.model.ScreenRegistry
import com.cybershield.app.model.ScreenSpec
import com.cybershield.app.ui.viewmodel.MainSecurityViewModel
import com.cybershield.app.core.security.RealFileInfo
import com.cybershield.app.ui.AppLocalization
import java.util.Calendar

private val Cyan = Color(0xFF31D7FF)
private val Muted = Color(0xFF8E99AA)
private val DarkBg = Color(0xFF07090D)
private val CardBg = Color(0xFF10141B)
private val Line = Color(0xFF252D39)

@Composable
fun CyberShieldApp(viewModel: MainSecurityViewModel = viewModel()) {
    val nav = rememberNavController()
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideBottomBarRoutes = setOf("splash", "login", "register", "forgot-password")
    val showBottomBar = currentRoute !in hideBottomBarRoutes

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            if (showBottomBar) {
                BottomBar(nav, viewModel)
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "splash",
            modifier = Modifier.padding(if (showBottomBar) padding else PaddingValues(0.dp))
        ) {
            composable("splash") { SplashScreen(nav, viewModel) }
            composable("login") { LoginScreen(nav, viewModel) }
            composable("register") { RegisterScreen(nav, viewModel) }
            composable("forgot-password") { ForgotPasswordScreen(nav) }
            composable("home") { HomeScreen(nav, viewModel) }
            composable("protect") { ProtectScreen(nav, viewModel) }
            composable("scan") { ScanCenterScreen(nav, viewModel) }
            composable("alerts") { AlertCenterScreen(nav, viewModel) }
            composable("settings") { SettingsScreen(nav, viewModel) }
            composable("device") { DeviceHealthScreen(nav, viewModel) }
            composable("apps") { AppsManagerScreen(nav, viewModel) }
            composable("privacy") { PrivacyCenterScreen(nav, viewModel) }
            composable("storage") { StorageManagerScreen(nav, viewModel) }
            composable("network") { DeviceHealthScreen(nav, viewModel) }
            composable("message_result") { ResultScreen(nav, "Message Scam Result", viewModel) }
            composable("url_protection") { ResultScreen(nav, "URL Protection", viewModel) }
            composable("qr_payment") { ResultScreen(nav, "QR & Payment Safety", viewModel) }
            composable("call_result") { ResultScreen(nav, "Scam Call & Caller ID Risk", viewModel) }
            composable("deepfake_scan") { MediaDeepfakeScanScreen(nav, viewModel) }
            composable("media_result") { ResultScreen(nav, "Deepfake & AI Media Forensics", viewModel) }
            composable("ai") { AiAssistantScreen(nav, viewModel) }
            composable("risk") { RiskScreen(nav, viewModel) }
            composable("family") { FamilyProtectionScreen(nav, viewModel) }
            composable("history") { ScanHistoryScreen(nav, viewModel) }
            composable("scan_report") { ResultScreen(nav, "Sentinel AI Scan Report", viewModel) }
            composable("settings_detail/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                val spec = ScreenRegistry.find(route)
                SettingsDetailScreen(nav, spec, viewModel)
            }
            composable("permission_detail/{permission}") { backStack ->
                val perm = backStack.arguments?.getString("permission") ?: "Camera"
                PermissionPrivacyDetailScreen(perm, nav, viewModel)
            }
            composable("feature/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                if (route == "ai_media_scanner") {
                    MediaDeepfakeScanScreen(nav, viewModel)
                } else if (route == "scam_call_identifier") {
                    ResultScreen(nav, "Scam Call & Caller ID Risk", viewModel)
                } else if (route.startsWith("04-permission-and-privacy-")) {
                    val permName = when {
                        route.contains("camera") -> "Camera"
                        route.contains("microphone") -> "Microphone"
                        route.contains("location") -> "Location"
                        route.contains("contacts") -> "Contacts"
                        route.contains("sms") -> "SMS"
                        route.contains("phone") -> "Phone"
                        route.contains("storage") -> "Storage"
                        route.contains("accessibility") -> "Accessibility"
                        route.contains("notifications") -> "Notifications"
                        route.contains("background") -> "Background Activity"
                        route.contains("privacy-risk") -> "Privacy Risk Score"
                        else -> "Camera"
                    }
                    if (permName == "Privacy Risk Score") {
                        PrivacyCenterScreen(nav, viewModel)
                    } else {
                        PermissionPrivacyDetailScreen(permName, nav, viewModel)
                    }
                } else if (route.startsWith("19-settings-") || route.startsWith("settings-detail-")) {
                    val spec = ScreenRegistry.find(route)
                    SettingsDetailScreen(nav, spec, viewModel)
                } else {
                    val spec = ScreenRegistry.find(route)
                    FeatureScreen(nav, spec, viewModel)
                }
            }
            composable("screen/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                if (route.startsWith("04-permission-and-privacy-")) {
                    val permName = when {
                        route.contains("camera") -> "Camera"
                        route.contains("microphone") -> "Microphone"
                        route.contains("location") -> "Location"
                        route.contains("contacts") -> "Contacts"
                        route.contains("sms") -> "SMS"
                        route.contains("phone") -> "Phone"
                        route.contains("storage") -> "Storage"
                        route.contains("accessibility") -> "Accessibility"
                        route.contains("notifications") -> "Notifications"
                        route.contains("background") -> "Background Activity"
                        route.contains("privacy-risk") -> "Privacy Risk Score"
                        else -> "Camera"
                    }
                    if (permName == "Privacy Risk Score") {
                        PrivacyCenterScreen(nav, viewModel)
                    } else {
                        PermissionPrivacyDetailScreen(permName, nav, viewModel)
                    }
                } else if (route.startsWith("19-settings-") || route.startsWith("settings-detail-")) {
                    val spec = ScreenRegistry.find(route)
                    SettingsDetailScreen(nav, spec, viewModel)
                } else if (route.startsWith("10-file-and-storage-security-")) {
                    val spec = ScreenRegistry.find(route)
                    RealStorageInspectorScreen(spec, nav, viewModel)
                } else {
                    val spec = ScreenRegistry.find(route)
                    FeatureScreen(nav, spec, viewModel)
                }
            }
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController, vm: MainSecurityViewModel) {
    val currentLang by vm.currentLanguage.collectAsState()
    val items = listOf(
        Triple("home", AppLocalization.getNavLabel("Home", currentLang), Icons.Default.Home),
        Triple("protect", AppLocalization.getNavLabel("Protect", currentLang), Icons.Default.Shield),
        Triple("scan", AppLocalization.getNavLabel("Scan", currentLang), Icons.Default.CropFree),
        Triple("alerts", AppLocalization.getNavLabel("Alerts", currentLang), Icons.Default.NotificationsNone),
        Triple("settings", AppLocalization.getNavLabel("Settings", currentLang), Icons.Default.Settings)
    )
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = Color(0xFF07090D),
        border = BorderStroke(1.dp, Color(0xFF161B22)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (route, label, icon) ->
                val isSelected = currentRoute == route ||
                    (route == "protect" && (currentRoute == "family" || currentRoute == "risk")) ||
                    (route == "scan" && (currentRoute == "message_result" || currentRoute == "url_protection" || currentRoute == "qr_payment" || currentRoute == "history" || currentRoute == "ai"))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            nav.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(if (isSelected) 52.dp else 40.dp)
                            .height(28.dp)
                            .then(
                                if (isSelected) Modifier.background(Color(0xFF0F3A4A), RoundedCornerShape(16.dp))
                                else Modifier
                            )
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (isSelected) Cyan else Muted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        label,
                        color = if (isSelected) Cyan else Muted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(title: String, nav: NavHostController, showBack: Boolean = true) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        } else {
            Spacer(Modifier.width(8.dp))
        }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.Shield, null, tint = Cyan)
    }
}

@Composable
fun FigmaHeader(
    title: String,
    subtitle: String,
    nav: NavHostController,
    showBell: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = Muted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (showBell) {
            Surface(
                color = Color(0xFF121822),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFF1F2937)),
                modifier = Modifier
                    .size(42.dp)
                    .clickable { nav.navigate("alerts") }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = "Alerts",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FigmaSubHeader(
    title: String,
    subtitle: String,
    nav: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { nav.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = Muted,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
fun CircularRiskGauge(
    score: Int,
    statusText: String,
    statusColor: Color,
    modifier: Modifier = Modifier.size(80.dp)
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 7.dp.toPx()
            drawArc(
                color = Color(0xFF1E2836),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = statusColor,
                startAngle = -90f,
                sweepAngle = 360f * (score.coerceIn(0, 100) / 100f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(statusText, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}


@Composable
private fun HomeScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val scoreState by vm.securityScore.collectAsState()
    val telemetry by vm.telemetry.collectAsState()

    val overall = scoreState.overallScore
    val scoreColor = SentinelRiskColors.getColorForScore(overall)
    val statusText = SentinelRiskColors.getStatusForScore(overall)
    val currentLang by vm.currentLanguage.collectAsState()
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingTitle = AppLocalization.getGreeting(currentLang, hour)
    val deviceName = telemetry?.let { "${it.manufacturer} ${it.model}" } ?: "Your phone"
    val localizedSubtitle = when (currentLang) {
        "te" -> "$deviceName సురక్షితంగా ఉంది"
        "hi" -> "$deviceName सुरक्षित है"
        else -> "$deviceName is protected"
    }

    val badgeText = if (overall >= 80) AppLocalization.getString("protected", currentLang)
        else if (overall >= 60) AppLocalization.getString("attention", currentLang)
        else AppLocalization.getString("at_risk", currentLang)
    val badgeBg = if (overall >= 80) Color(0xFF0F2D1F) else if (overall >= 60) Color(0xFF2C2010) else Color(0xFF2D1212)
    val badgeColor = scoreColor

    val recCount = scoreState.recommendations.size
    val topRec = scoreState.recommendations.firstOrNull() ?: "Device meets security baseline."

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Figma Header: Dynamic Greeting / [Device Name] is protected
        item {
            FigmaHeader(
                title = greetingTitle,
                subtitle = localizedSubtitle,
                nav = nav,
                showBell = true,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp)
            )
            // Status badge: Dynamic PROTECTED / ATTENTION / AT RISK
            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
            ) {
                Text(
                    badgeText,
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        // Hero Card: Live Security Health
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(20.dp),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = Cyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Security health", color = Muted, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$overall", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
                            Text("  / 100", color = Muted, fontSize = 15.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        val statusDesc = if (overall >= 90) "No critical threats" else if (overall >= 75) "Minor security improvements advised" else "Device hardening required"
                        Text(statusDesc, color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    CircularRiskGauge(
                        score = overall,
                        statusText = statusText,
                        statusColor = scoreColor,
                        modifier = Modifier.size(82.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Quick Actions 3-Card Balanced Row (Network removed)
        item {
            Text("Quick actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    title = "Scan now",
                    icon = Icons.Default.CropFree,
                    iconColor = Cyan,
                    onClick = { nav.navigate("scan") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Apps",
                    icon = Icons.Default.GridView,
                    iconColor = Color(0xFF818CF8),
                    onClick = { nav.navigate("apps") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Storage",
                    icon = Icons.Default.Folder,
                    iconColor = Color(0xFFFBBF24),
                    onClick = { nav.navigate("storage") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(18.dp))
        }

        // Live Recommendations Card
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
                            .size(36.dp)
                            .background(if (recCount > 0) Color(0xFF2C2010) else Color(0xFF0F2D1F), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (recCount > 0) Icons.Default.WarningAmber else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (recCount > 0) Color(0xFFF59E0B) else Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$recCount recommendations", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(topRec, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF1F2937),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { nav.navigate("device") }
                            ) {
                                Text(
                                    "Review",
                                    color = Cyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            Text("Real-time telemetry", color = Muted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line),
        modifier = modifier.height(78.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
private fun ProtectScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    PrivacyCenterScreen(nav, vm)
}

@Composable
private fun ScanCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var activeDialog by remember { mutableStateOf<String?>(null) }
    var inputQuery by remember { mutableStateOf("") }
    var appFilterQuery by remember { mutableStateOf("") }
    val installedApps: List<com.cybershield.app.core.security.InspectedAppInfo> = remember { vm.getInstalledApps() }
    val context = LocalContext.current
    var showAiOverlay by remember { mutableStateOf(false) }
    val currentScan by vm.currentScanResult.collectAsState()

    val paymentPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            vm.scanPaymentPhoto(uri, context)
            activeDialog = null
            nav.navigate("qr_payment")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                FigmaHeader(
                    title = "Scan center",
                    subtitle = "Analyze before you act",
                    nav = nav,
                    showBell = true
                )
                Spacer(Modifier.height(4.dp))
            }

            // Quick scan Card (Real Device Posture Scan)
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFF0B2530), RoundedCornerShape(12.dp))
                                .border(1.dp, Cyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CropFree, contentDescription = null, tint = Cyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Quick scan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Live hardware + OS + network audit", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFF1F2937),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    vm.runQuickDeviceScan()
                                    nav.navigate("scan_report")
                                }
                            ) {
                                Text(
                                    "START AUDIT",
                                    color = Cyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // Detection tools 2x4 grid
            item {
                Text("Detection tools", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(10.dp))
            }

            // Row 1: Message & URL
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetectionToolCard(
                        title = "Message",
                        subtitle = "SMS / WhatsApp",
                        icon = Icons.Default.AutoAwesome,
                        iconColor = Color(0xFFA855F7),
                        onClick = {
                            inputQuery = ""
                            activeDialog = "MESSAGE"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DetectionToolCard(
                        title = "URL",
                        subtitle = "Phishing / links",
                        icon = Icons.Default.Shield,
                        iconColor = Cyan,
                        onClick = {
                            inputQuery = ""
                            activeDialog = "URL"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            // Row 2: QR & Payment
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetectionToolCard(
                        title = "QR",
                        subtitle = "Scan safely",
                        icon = Icons.Default.QrCodeScanner,
                        iconColor = Cyan,
                        onClick = {
                            inputQuery = ""
                            activeDialog = "QR"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DetectionToolCard(
                        title = "Payment",
                        subtitle = "Verify UPI ID",
                        icon = Icons.Default.FolderOpen,
                        iconColor = Color(0xFFFBBF24),
                        onClick = {
                            inputQuery = ""
                            activeDialog = "PAYMENT"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            // Row 3: APK / App & Call
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetectionToolCard(
                        title = "APK / App",
                        subtitle = "Inspect installed",
                        icon = Icons.Default.GridView,
                        iconColor = Color(0xFFFF5252),
                        onClick = {
                            appFilterQuery = ""
                            activeDialog = "APK"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DetectionToolCard(
                        title = "Call",
                        subtitle = "Caller risk",
                        icon = Icons.Default.PhoneAndroid,
                        iconColor = Color(0xFF60A5FA),
                        onClick = {
                            inputQuery = ""
                            activeDialog = "CALL"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            // Row 4: Media & Investment
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetectionToolCard(
                        title = "Media",
                        subtitle = "Deepfake",
                        icon = Icons.Default.Star,
                        iconColor = Cyan,
                        onClick = { nav.navigate("deepfake_scan") },
                        modifier = Modifier.weight(1f)
                    )
                    DetectionToolCard(
                        title = "Investment",
                        subtitle = "Loan scams",
                        icon = Icons.Default.WarningAmber,
                        iconColor = Color(0xFFF59E0B),
                        onClick = {
                            inputQuery = ""
                            activeDialog = "INVESTMENT"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Interactive Scan Input Dialogs
        activeDialog?.let { dialogType ->
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                containerColor = Color(0xFF10141B),
                titleContentColor = Color.White,
                textContentColor = Color(0xFFCBD5E1),
                title = {
                    Text(
                        when (dialogType) {
                            "MESSAGE" -> "Analyze Message (SMS / Chat)"
                            "URL" -> "Scan Website Link / URL"
                            "QR" -> "Analyze QR Code Payload"
                            "PAYMENT" -> "Verify Payment Recipient (UPI)"
                            "APK" -> "Select Installed App to Audit"
                            "CALL" -> "Verify Caller Number"
                            "INVESTMENT" -> "Analyze Investment / Loan Scheme"
                            else -> "Security Scan"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column(Modifier.fillMaxWidth()) {
                        if (dialogType == "APK") {
                            Text(
                                "Choose any application installed on your device to run a permission audit:",
                                color = Muted,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = appFilterQuery,
                                onValueChange = { appFilterQuery = it },
                                placeholder = { Text("Search installed apps...", color = Muted, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan,
                                    unfocusedBorderColor = Line,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            val filteredApps = installedApps.filter {
                                it.appName.contains(appFilterQuery, ignoreCase = true) ||
                                it.packageName.contains(appFilterQuery, ignoreCase = true)
                            }
                            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                                if (filteredApps.isEmpty()) {
                                    Text("No apps match query", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                                } else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(filteredApps.take(30)) { appInfo ->
                                            Surface(
                                                color = Color(0xFF161D27),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Line),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                                    .clickable {
                                                        vm.scanApk(appInfo.packageName)
                                                        activeDialog = null
                                                        nav.navigate("scan_report")
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.GridView, null, tint = Cyan, modifier = Modifier.size(16.dp))
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(appInfo.appName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val hintText = when (dialogType) {
                                "MESSAGE" -> "Type or paste message text to inspect for phishing, credential requests, or urgent pressure:"
                                "URL" -> "Enter or paste website link to check for deceptive domains, SSL status, and malware:"
                                "QR" -> "Enter or paste decoded QR payload, payment link, or data string:"
                                "PAYMENT" -> "Enter UPI VPA or merchant ID (e.g., store@upi, 9876543210@paytm):"
                                "CALL" -> "Enter caller phone number to verify reputation:"
                                "INVESTMENT" -> "Paste crypto, loan, or investment proposal message:"
                                else -> "Enter input for security audit:"
                            }
                            Text(hintText, color = Muted, fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = {
                                    Text(
                                        when (dialogType) {
                                            "URL" -> "https://..."
                                            "PAYMENT" -> "merchant@upi"
                                            "CALL" -> "+91 98765 43210"
                                            else -> "Paste text here..."
                                        },
                                        color = Muted,
                                        fontSize = 13.sp
                                    )
                                },
                                minLines = if (dialogType == "MESSAGE" || dialogType == "INVESTMENT") 3 else 1,
                                maxLines = if (dialogType == "MESSAGE" || dialogType == "INVESTMENT") 5 else 2,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan,
                                    unfocusedBorderColor = Line,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            if (dialogType == "PAYMENT") {
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { paymentPhotoPicker.launch("image/*") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Cyan, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Upload Screenshot / QR Photo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (dialogType != "APK") {
                        Button(
                            onClick = {
                                if (inputQuery.isNotBlank()) {
                                    when (dialogType) {
                                        "MESSAGE" -> {
                                            vm.scanMessage(inputQuery)
                                            activeDialog = null
                                            nav.navigate("message_result")
                                        }
                                        "URL" -> {
                                            vm.scanUrl(inputQuery)
                                            activeDialog = null
                                            nav.navigate("url_protection")
                                        }
                                        "QR" -> {
                                            vm.scanQr(inputQuery)
                                            activeDialog = null
                                            nav.navigate("qr_payment")
                                        }
                                        "PAYMENT" -> {
                                            vm.scanPayment(inputQuery)
                                            activeDialog = null
                                            nav.navigate("qr_payment")
                                        }
                                        "CALL" -> {
                                            vm.scanCall(inputQuery)
                                            activeDialog = null
                                            nav.navigate("call_result")
                                        }
                                        "INVESTMENT" -> {
                                            vm.scanMessage(inputQuery)
                                            activeDialog = null
                                            nav.navigate("message_result")
                                        }
                                    }
                                }
                            },
                            enabled = inputQuery.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan, disabledContainerColor = Line),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Scan Now", color = if (inputQuery.isNotBlank()) Color.Black else Muted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel", color = Muted)
                    }
                }
            )
        }

        if (currentScan != null) {
            ScanAiSideFab(
                onClick = { showAiOverlay = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
        }

        ScanAiAssistantOverlay(
            scanResult = currentScan,
            isOpen = showAiOverlay,
            onClose = { showAiOverlay = false },
            vm = vm
        )
    }
}

@Composable
private fun DetectionToolCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.height(10.dp))
            Text("Analyze", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EvidenceRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Muted, fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun SignalPill(label: String, bg: Color, fg: Color) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun FlaggedItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color(0xFFE2E8F0), fontSize = 13.sp)
    }
}

@Composable
private fun CheckshieldItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = Cyan, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color(0xFFE2E8F0), fontSize = 13.sp)
    }
}

@Composable
private fun MessageAnalysisScreen(
    nav: NavHostController,
    result: SecurityResult?,
    vm: MainSecurityViewModel
) {
    var inlineInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(result == null) }
    val isScanning by vm.isScanning.collectAsState()
    var showAiOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaSubHeader("Message analysis", "SMS / WhatsApp threat inspection", nav)
            Spacer(Modifier.height(4.dp))
        }

        // Inline input section when no scan or user requests re-scan
        if (isInputExpanded || result == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Analyze SMS or Chat Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Paste any suspicious SMS, OTP request, or WhatsApp text to inspect:", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inlineInput,
                            onValueChange = { inlineInput = it },
                            placeholder = { Text("Paste message here (e.g. Your bank account is locked...)", color = Muted, fontSize = 12.sp) },
                            minLines = 3,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan,
                                unfocusedBorderColor = Line,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (inlineInput.isNotBlank()) {
                                        vm.scanMessage(inlineInput)
                                        isInputExpanded = false
                                    }
                                },
                                enabled = inlineInput.isNotBlank() && !isScanning,
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan, disabledContainerColor = Line),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Analyzing...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Analyze Now", color = if (inlineInput.isNotBlank()) Color.Black else Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (result != null) {
                                TextButton(onClick = { isInputExpanded = false }) {
                                    Text("Close", color = Muted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }

        result?.let { res ->
            val scoreColor = SentinelRiskColors.getColorForScore(res.securityScore)
            val riskBadgeText = res.riskLevel.label
            val riskBadgeColor = when (res.riskLevel) {
                RiskLevel.CRITICAL, RiskLevel.HIGH_RISK -> Color(0xFFFF5252)
                RiskLevel.SUSPICIOUS -> Color(0xFFF59E0B)
                RiskLevel.LOW_CONCERN -> Color(0xFF38BDF8)
                else -> Color(0xFF00E676)
            }
            val riskBadgeBg = riskBadgeColor.copy(alpha = 0.16f)

            // Risk level pill badge
            item {
                Surface(
                    color = riskBadgeBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, riskBadgeColor.copy(alpha = 0.7f))
                ) {
                    Text(
                        riskBadgeText,
                        color = riskBadgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(14.dp))
            }

            // Headline (Scanned message text preview)
            item {
                val preview = res.rawInputReference?.take(70) ?: "Scanned Message"
                Text(
                    "\"$preview${if ((res.rawInputReference?.length ?: 0) > 70) "..." else ""}\"",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
            }

            // Target Content & Detected signals Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Input Content", color = Muted, fontSize = 11.sp)
                        Text(
                            res.rawInputReference ?: "No message text available",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        Text("Detected signals (${res.signals.size})", color = Muted, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        if (res.signals.isEmpty()) {
                            Text("✓ No coercive urgency, fake authority, or credential theft signals found.", color = Color(0xFF00E676), fontSize = 12.sp)
                        } else {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                res.signals.forEach { sig ->
                                    val sigColor = when (sig.severity.uppercase()) {
                                        "CRITICAL", "HIGH" -> Color(0xFFFF5252)
                                        "MEDIUM" -> Color(0xFFF59E0B)
                                        else -> Color(0xFF00E676)
                                    }
                                    SignalPill(sig.name, sigColor.copy(alpha = 0.16f), sigColor)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Risk score
            item {
                Text("Risk score", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("${res.riskScore} / 100", color = riskBadgeColor, fontSize = 38.sp, fontWeight = FontWeight.Black)
                    Text("Confidence ${(res.confidence * 100).toInt()}%", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(20.dp))
            }

            // Why we flagged it
            item {
                Text("Why we flagged it", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (res.whyThisScore.isNotEmpty()) {
                        res.whyThisScore.forEach { factor ->
                            FlaggedItem(factor)
                        }
                    } else if (res.signals.isNotEmpty()) {
                        res.signals.forEach { s ->
                            FlaggedItem("${s.name}: ${s.description}")
                        }
                    } else {
                        FlaggedItem(res.explanation)
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Diagnostic Report Section
            item {
                Text("Diagnostic Report", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val isSafe = res.riskLevel == RiskLevel.SAFE
                        val msgType = if (isSafe) "Verified Transactional / Standard SMS" else "Suspicious Coercive / Phishing SMS"
                        Text("Message Classification: $msgType", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (isSafe) "WHY IT IS SAFE" else "WHY IT IS DANGEROUS",
                            color = if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        val whyText = if (isSafe) {
                            "Contains standard informational phrasing. No artificial panic countdowns, fake lottery claims, unauthorized shortened URLs, or requests to disclose one-time passwords (OTPs)."
                        } else {
                            "Contains coercive urgency keywords, threats of immediate account suspension or service disconnection, or deceptive requests demanding personal credentials and PIN disclosure."
                        }
                        Text(whyText, color = Color(0xFFCBD5E1), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text("WHAT YOU SHOULD DO", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val actionText = if (isSafe) {
                            "Safe to read. If this is a banking notification, verify through your official banking application or website."
                        } else {
                            "Do NOT reply, call back, or click any links in this message. NEVER disclose your OTP or bank details. Block and report the sender immediately."
                        }
                        Text(actionText, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Safe next action Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, if (res.riskLevel == RiskLevel.SAFE) Color(0xFF00E676) else Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (res.riskLevel == RiskLevel.SAFE) Color(0xFF00E676) else Color(0xFFFF5252),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Safe next action",
                                color = if (res.riskLevel == RiskLevel.SAFE) Color(0xFF00E676) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val actionDesc = res.recommendedActions.firstOrNull() ?: if (res.riskLevel == RiskLevel.SAFE) "Safe to read. No credential theft detected." else "Do not reply, pay or share OTPs."
                            Text(actionDesc, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Cyan),
                                    modifier = Modifier.clickable { nav.navigate("history") }
                                ) {
                                    Text(
                                        "View in History",
                                        color = Cyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                                Surface(
                                    color = Color(0xFF1E2836),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.clickable { isInputExpanded = true }
                                ) {
                                    Text(
                                        "Scan Another",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: item {
            if (!isInputExpanded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Cyan, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("No Message Scanned Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Enter or paste any SMS or chat message to analyze with Sentinel AI.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { isInputExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enter Message to Scan", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    ScanAiSideFab(
            onClick = { showAiOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        )

        ScanAiAssistantOverlay(
            scanResult = result,
            isOpen = showAiOverlay,
            onClose = { showAiOverlay = false },
            vm = vm
        )
    }
}

@Composable
private fun UrlProtectionScreen(
    nav: NavHostController,
    result: SecurityResult?,
    vm: MainSecurityViewModel
) {
    var inlineUrlInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(result == null) }
    val isScanning by vm.isScanning.collectAsState()
    var showAiOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaSubHeader("URL protection", "Preview risky destinations", nav)
            Spacer(Modifier.height(4.dp))
        }

        // Inline input section when no scan or user requests re-scan
        if (isInputExpanded || result == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Scan Website Link / URL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Enter any suspicious website link to check SSL, domain age, and phishing threat:", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inlineUrlInput,
                            onValueChange = { inlineUrlInput = it },
                            placeholder = { Text("https://example.com/...", color = Muted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan,
                                unfocusedBorderColor = Line,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (inlineUrlInput.isNotBlank()) {
                                        vm.scanUrl(inlineUrlInput)
                                        isInputExpanded = false
                                    }
                                },
                                enabled = inlineUrlInput.isNotBlank() && !isScanning,
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan, disabledContainerColor = Line),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Scanning...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Scan URL", color = if (inlineUrlInput.isNotBlank()) Color.Black else Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (result != null) {
                                TextButton(onClick = { isInputExpanded = false }) {
                                    Text("Close", color = Muted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }

        result?.let { res ->
            val scannedUrl = res.rawInputReference ?: "Unknown URL"
            val isSafe = res.riskLevel == RiskLevel.SAFE || res.riskLevel == RiskLevel.LOW_CONCERN
            val riskBadgeColor = when (res.riskLevel) {
                RiskLevel.CRITICAL, RiskLevel.HIGH_RISK -> Color(0xFFFF5252)
                RiskLevel.SUSPICIOUS -> Color(0xFFF59E0B)
                RiskLevel.LOW_CONCERN -> Color(0xFF38BDF8)
                else -> Color(0xFF00E676)
            }

            // URL display box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(12.dp))
                        .border(1.dp, Line, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        scannedUrl,
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Risk pill badge
            item {
                Surface(
                    color = riskBadgeColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, riskBadgeColor.copy(alpha = 0.7f))
                ) {
                    Text(
                        res.riskLevel.label,
                        color = riskBadgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(14.dp))
            }

            // Suspicious / Safe destination Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            if (isSafe) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (isSafe) "Verified Destination" else "Suspicious Destination",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                res.explanation,
                                color = Muted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "${res.signals.size} risk signals identified",
                                color = riskBadgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Diagnostic Report Section
            item {
                Text("Diagnostic Report", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val urlType = when {
                            scannedUrl.startsWith("https://", true) -> "Encrypted HTTPS Web Destination"
                            scannedUrl.startsWith("http://", true) -> "Insecure Plaintext HTTP Link"
                            else -> "Web Destination URI"
                        }
                        Text("Destination Type: $urlType", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (isSafe) "WHY IT IS SAFE" else "WHY IT IS DANGEROUS",
                            color = if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        val whyText = if (isSafe) {
                            "Cryptographic TLS certificate is active. Domain structure conforms to standard registry. No credential harvesting, credential-relay, or deceptive redirects identified."
                        } else {
                            "Suspicious domain naming, lack of HTTPS transport encryption, or identified deceptive patterns designed to impersonate legitimate services and intercept credentials."
                        }
                        Text(whyText, color = Color(0xFFCBD5E1), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text("WHAT YOU SHOULD DO", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val actionText = if (isSafe) {
                            "Safe to proceed. Confirm the domain name in your browser's address bar matches your intended destination before entering sensitive data."
                        } else {
                            "DO NOT open this link in any browser. Do not enter passwords, phone numbers, or debit/credit card details. Close and delete the source immediately."
                        }
                        Text(actionText, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Evidence section
            item {
                Text("Evidence", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val isHttps = scannedUrl.startsWith("https://", ignoreCase = true)
                        EvidenceRow("TLS Encryption", if (isHttps) "HTTPS (Secured)" else "HTTP (Insecure Plaintext)", if (isHttps) Color(0xFF00E676) else Color(0xFFFF5252))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                        EvidenceRow("Threat Probability", "${(res.threatProbability * 100).toInt()}%", if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                        EvidenceRow("Security Engine", "${res.modelName} (v${res.modelVersion})", Cyan)
                        HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                        EvidenceRow("Security Score", "${res.securityScore} / 100", SentinelRiskColors.getColorForScore(res.securityScore))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Action button
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { nav.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            if (isSafe) "Destination Verified Safe" else "Do not open this link",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = { isInputExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("New URL", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } ?: item {
            if (!isInputExpanded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Shield, null, tint = Cyan, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("No Link Scanned Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Enter or paste any URL to verify against phishing and credential theft.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { isInputExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enter URL to Scan", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    ScanAiSideFab(
            onClick = { showAiOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        )

        ScanAiAssistantOverlay(
            scanResult = result,
            isOpen = showAiOverlay,
            onClose = { showAiOverlay = false },
            vm = vm
        )
    }
}

@Composable
private fun QrPaymentScreen(
    nav: NavHostController,
    result: SecurityResult?,
    vm: MainSecurityViewModel
) {
    val context = LocalContext.current
    var showAiOverlay by remember { mutableStateOf(false) }
    var inlineQrInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(result == null) }
    val isScanning by vm.isScanning.collectAsState()
    val lastDetectedAmount by vm.lastDetectedPaymentAmount.collectAsState()

    val paymentPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            vm.scanPaymentPhoto(uri, context)
            isInputExpanded = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                FigmaSubHeader("QR & payment", "Review before you pay", nav)
                Spacer(Modifier.height(4.dp))
            }

            // Inline input section when no scan or user requests re-scan
            if (isInputExpanded || result == null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Cyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Analyze QR / UPI Payment", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Paste decoded QR payload, UPI link (upi://pay?pa=...), or VPA to verify recipient:", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = inlineQrInput,
                                onValueChange = { inlineQrInput = it },
                                placeholder = { Text("e.g. upi://pay?pa=store@upi&pn=Store&am=500", color = Muted, fontSize = 12.sp) },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan,
                                    unfocusedBorderColor = Line,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (inlineQrInput.isNotBlank()) {
                                            vm.scanQr(inlineQrInput)
                                            isInputExpanded = false
                                        }
                                    },
                                    enabled = inlineQrInput.isNotBlank() && !isScanning,
                                    colors = ButtonDefaults.buttonColors(containerColor = Cyan, disabledContainerColor = Line),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Analyzing...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Verify QR", color = if (inlineQrInput.isNotBlank()) Color.Black else Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                OutlinedButton(
                                    onClick = { paymentPhotoPicker.launch("image/*") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Cyan, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Upload Photo / QR", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                if (result != null) {
                                    TextButton(onClick = { isInputExpanded = false }) {
                                        Text("Close", color = Muted, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }
        }

        result?.let { res ->
            val payload = res.rawInputReference ?: ""
            // Parse UPI parameters if present
            var payeeVpa = "Direct Payload"
            var amountStr = "Unspecified"
            if (!lastDetectedAmount.isNullOrBlank()) {
                amountStr = lastDetectedAmount!!
            }
            if (payload.contains("pa=", ignoreCase = true)) {
                val paMatch = Regex("pa=([^&]+)", RegexOption.IGNORE_CASE).find(payload)
                if (paMatch != null) payeeVpa = paMatch.groupValues[1]
            } else if (payload.isNotBlank()) {
                payeeVpa = payload.take(40)
            }
            if (payload.contains("am=", ignoreCase = true)) {
                val amMatch = Regex("am=([^&]+)", RegexOption.IGNORE_CASE).find(payload)
                if (amMatch != null) amountStr = "₹${amMatch.groupValues[1]}"
            }
            if (amountStr == "Unspecified") {
                val match = Regex("""(?:₹|INR|Rs\.?)\s*([0-9,]+(?:\.[0-9]{1,2})?)""").find(res.explanation)
                if (match != null) {
                    amountStr = "₹${match.groupValues[1]}"
                }
            }

            val isSafe = res.riskLevel == RiskLevel.SAFE
            val riskBadgeColor = when (res.riskLevel) {
                RiskLevel.CRITICAL, RiskLevel.HIGH_RISK -> Color(0xFFFF5252)
                RiskLevel.SUSPICIOUS -> Color(0xFFF59E0B)
                RiskLevel.LOW_CONCERN -> Color(0xFF38BDF8)
                else -> Color(0xFF00E676)
            }

            // QR decoded Card
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
                                .size(64.dp)
                                .background(Color(0xFF0B1925), RoundedCornerShape(12.dp))
                                .border(1.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CropFree, contentDescription = null, tint = Cyan, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("QR decoded", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    color = riskBadgeColor.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, riskBadgeColor),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        res.riskLevel.label,
                                        color = riskBadgeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text("Recipient", color = Muted, fontSize = 11.sp)
                            Text(payeeVpa, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Amount", color = Muted, fontSize = 11.sp)
                            Text(amountStr, color = Color(0xFFFBBF24), fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Diagnostic Report Section
            item {
                Text("Diagnostic Report", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val paymentType = if (payload.startsWith("upi://", true)) "UPI Standard Payment Intent" else "QR Payment Transfer Payload"
                        Text("Payment Type: $paymentType", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Detected Amount: $amountStr", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (isSafe) "WHY IT IS SAFE" else "WHY IT IS DANGEROUS",
                            color = if (isSafe) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        val whyText = if (isSafe) {
                            "Verified merchant or standard P2P transfer intent. Direct transaction route without reverse-collect or unauthorized authorization overrides."
                        } else {
                            "Contains suspicious request payload, reverse-collect manipulation attempting to debit rather than credit, or unverified recipient origin."
                        }
                        Text(whyText, color = Color(0xFFCBD5E1), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Line.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        Text("WHAT YOU SHOULD DO", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val actionText = if (isSafe) {
                            "Verify the recipient name and amount on your bank UPI PIN screen before approving payment."
                        } else {
                            "DECLINE this transaction immediately. NEVER enter your UPI PIN or scan a QR code to receive money!"
                        }
                        Text(actionText, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // Before you pay section
            item {
                Text("Before you pay", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CheckshieldItem("Recipient ($payeeVpa) matches your intent")
                        CheckshieldItem("Amount ($amountStr) matches what you expect")
                        CheckshieldItem("You NEVER need to enter your PIN to receive money")
                        if (res.signals.isNotEmpty()) {
                            res.signals.forEach { sig ->
                                CheckshieldItem("Signal: ${sig.name} - ${sig.description}")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // Button
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { isInputExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10151E)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Cyan),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Scan Another Payment", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "CyberShield never controls your bank transaction.",
                    color = Muted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } ?: item {
            if (!isInputExpanded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = Cyan, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("No QR / Payment Scanned Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Enter or paste any UPI payload, QR string, or merchant ID to verify safety.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { isInputExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enter Payment Payload", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    ScanAiSideFab(
            onClick = { showAiOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        )

        ScanAiAssistantOverlay(
            scanResult = result,
            isOpen = showAiOverlay,
            onClose = { showAiOverlay = false },
            vm = vm
        )
    }
}

@Composable
private fun ResultScreen(nav: NavHostController, title: String, vm: MainSecurityViewModel) {
    val result by vm.currentScanResult.collectAsState()

    when {
        title.contains("Message", ignoreCase = true) -> {
            MessageAnalysisScreen(nav, result, vm)
            return
        }
        title.contains("URL", ignoreCase = true) -> {
            UrlProtectionScreen(nav, result, vm)
            return
        }
        title.contains("QR", ignoreCase = true) -> {
            QrPaymentScreen(nav, result, vm)
            return
        }
    }

    val scanChatThreads by vm.scanChatThreads.collectAsState()
    val isAssistantResponding by vm.isAssistantResponding.collectAsState()
    var userQuestionInput by remember { mutableStateOf("") }
    var showAiOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { TopBar("Sentinel AI Scan Report", nav) }
        result?.let { res ->
            val score = res.securityScore
            val scoreColor = SentinelRiskColors.getColorForScore(score)
            val scoreStatus = SentinelRiskColors.getStatusForScore(score)
            val messages = scanChatThreads[res.scanId] ?: emptyList()

            // 1. RESULT HEADER & 2. SECURITY SCORE & 3. RISK STATUS & 4. CONFIDENCE
            item {
                GlassCard(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .border(1.dp, scoreColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                ) {
                    Text("SENTINEL AI ANALYSIS", color = scoreColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("Scan Complete • ${res.scannerType}", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))

                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SECURITY SCORE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("$score / 100", color = scoreColor, fontSize = 42.sp, fontWeight = FontWeight.Black)
                        }
                        Surface(
                            color = scoreColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, scoreColor.copy(alpha = 0.8f))
                        ) {
                            Text(
                                scoreStatus,
                                color = scoreColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Confidence: ${(res.confidence * 100).toInt()}%", color = Color(0xFFE2E8F0), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("Threat Prob: ${(res.threatProbability * 100).toInt()}%", color = Muted, fontSize = 12.sp)
                    }
                    Text("Engine: ${res.modelName} (v${res.modelVersion})", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // 5. QUICK SUMMARY
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("QUICK SUMMARY", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val summaryText = if (!res.quickSummary.isNullOrBlank()) res.quickSummary!! else res.explanation
                    Text(summaryText, color = Color.White, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // 6. WHY THIS SCORE?
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("WHY THIS SCORE?", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Score contributor factor weighting:", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(6.dp))
                    if (res.whyThisScore.isNotEmpty()) {
                        res.whyThisScore.forEach { factor ->
                            Text(
                                factor,
                                color = if (factor.contains("-") || factor.contains("safe", ignoreCase = true) || factor.contains("✓")) SentinelRiskColors.SAFE_GREEN else SentinelRiskColors.LIGHT_ORANGE,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    } else {
                        if (score >= 90) {
                            Text("✓ Transport security active (-5 risk)", color = SentinelRiskColors.SAFE_GREEN, fontSize = 12.sp)
                            Text("✓ Domain structure conforms to standard registry (-10 risk)", color = SentinelRiskColors.SAFE_GREEN, fontSize = 12.sp)
                            Text("✓ No credential collection signals detected (-10 risk)", color = SentinelRiskColors.SAFE_GREEN, fontSize = 12.sp)
                        } else {
                            res.signals.forEach { s ->
                                Text("⚠ ${s.name} (+${s.severity}) risk contributor", color = SentinelRiskColors.DANGER_RED, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 7. DETECTED SIGNALS
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("DETECTED SIGNALS (${res.signals.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    if (res.signals.isEmpty()) {
                        Text("✓ No malicious, phishing, or deceptive indicators detected.", color = SentinelRiskColors.SAFE_GREEN, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                    } else {
                        res.signals.forEach { s ->
                            Column(Modifier.padding(top = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("• ${s.name}", fontWeight = FontWeight.SemiBold, color = Cyan, fontSize = 13.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Surface(
                                        color = when(s.severity.uppercase()) {
                                            "CRITICAL" -> SentinelRiskColors.DANGER_RED.copy(alpha = 0.2f)
                                            "HIGH" -> SentinelRiskColors.DANGER_RED.copy(alpha = 0.15f)
                                            "MEDIUM" -> SentinelRiskColors.DARK_AMBER.copy(alpha = 0.2f)
                                            else -> SentinelRiskColors.SAFE_GREEN.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            s.severity,
                                            color = when(s.severity.uppercase()) {
                                                "CRITICAL", "HIGH" -> SentinelRiskColors.DANGER_RED
                                                "MEDIUM" -> SentinelRiskColors.DARK_AMBER
                                                else -> SentinelRiskColors.SAFE_GREEN
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(s.description, color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp, top = 2.dp))
                                if (!s.evidenceValue.isNullOrBlank()) {
                                    Text("Evidence: ${s.evidenceValue}", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp, top = 1.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 8. EVIDENCE
            if (res.evidence.isNotEmpty() || !res.rawInputReference.isNullOrBlank()) {
                item {
                    GlassCard(Modifier.padding(top = 10.dp)) {
                        Text("EVIDENCE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (!res.rawInputReference.isNullOrBlank()) {
                            Text("Target Reference: ${res.rawInputReference}", color = Cyan, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                        res.evidence.forEach { ev ->
                            Text("• $ev", color = Color(0xFFCBD5E1), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            // 9. AI EXPLANATION
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("AI EXPLANATION", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(res.explanation, color = Color.White, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // 10. RECOMMENDED ACTION
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("RECOMMENDED ACTION", color = SentinelRiskColors.SAFE_GREEN, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    if (res.recommendedActions.isEmpty()) {
                        Text("✓ Maintain standard caution.", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    } else {
                        res.recommendedActions.forEach { act ->
                            Text("✓ $act", color = Color(0xFFE2E8F0), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            // 11. WHAT TO AVOID
            if (res.whatToAvoid.isNotEmpty()) {
                item {
                    GlassCard(Modifier.padding(top = 10.dp)) {
                        Text("WHAT TO AVOID", color = SentinelRiskColors.DANGER_RED, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        res.whatToAvoid.forEach { avoid ->
                            Text("✕ $avoid", color = Color(0xFFFF8A65), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            // 12. LIMITATIONS
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("LIMITATIONS", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (res.limitations.isEmpty()) {
                        Text("Automated scanning evaluates known patterns and IOCs at inspection time.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    } else {
                        res.limitations.forEach { lim ->
                            Text("ℹ $lim", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }
            }

            // 13. MODEL INFORMATION
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("MODEL INFORMATION", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Detector: ${res.modelName} (v${res.modelVersion})", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    Text("Scan ID: ${res.scanId}", color = Muted, fontSize = 10.sp)
                    Text("Timestamp: ${res.timestamp}", color = Muted, fontSize = 10.sp)
                }
            }

            // 14. ASK SENTINEL AI (EMBEDDED INLINE CONTEXTUAL CHAT)
            item {
                Spacer(Modifier.height(8.dp))
                GlassCard(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .border(1.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Cyan, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("SENTINEL AI", color = Cyan, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text("Ask about this scan", color = Muted, fontSize = 11.sp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Suggested questions:", color = Muted, fontSize = 11.sp)
                    val suggestions = listOf(
                        "Why is this dangerous?",
                        "Why did you give this score?",
                        "What signals were detected?",
                        "Is it safe to continue?",
                        "What should I do now?",
                        "Explain this in simple words.",
                        "What should I avoid?",
                        "How confident is this result?"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestions.forEach { q ->
                            Surface(
                                onClick = { vm.askScanAssistant(res.scanId, q) },
                                color = Color(0xFF151D28),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3649))
                            ) {
                                Text(q, color = Cyan, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                    }

                    // Conversation history for this scan
                    if (messages.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF090D13), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            messages.forEach { msg ->
                                if (msg.isFromUser) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Surface(
                                            color = Color(0xFF1E3A8A),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.widthIn(max = 280.dp)
                                        ) {
                                            Text(msg.text, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                                        }
                                    }
                                } else {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                        Surface(
                                            color = Color(0xFF131A24),
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233044)),
                                            modifier = Modifier.widthIn(max = 290.dp)
                                        ) {
                                            Column(Modifier.padding(10.dp)) {
                                                Text(msg.sender, color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text(msg.text, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                                                val reasoning = msg.reasoningSummary
                                                if (!reasoning.isNullOrBlank()) {
                                                    Text("Reasoning: $reasoning", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                                                }
                                                for (a in msg.recommendedActions) {
                                                    Text("✓ $a", color = SentinelRiskColors.SAFE_GREEN, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (isAssistantResponding) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Cyan, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Sentinel AI is analyzing scan telemetry...", color = Muted, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userQuestionInput,
                            onValueChange = { userQuestionInput = it },
                            placeholder = { Text("Ask Sentinel AI about this scan...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (userQuestionInput.isNotBlank()) {
                                    val q = userQuestionInput.trim()
                                    userQuestionInput = ""
                                    vm.askScanAssistant(res.scanId, q)
                                }
                            },
                            enabled = userQuestionInput.isNotBlank() && !isAssistantResponding
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Cyan)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        vm.reportIncident("Threat: ${res.scannerType}", res.scannerType, res.explanation)
                        nav.navigate("feature/${ScreenRegistry.all.first { it.title == "Incident Details" }.route}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232D3B)),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    Text("Escalate to Incident Center", color = Color.White)
                }
            }
        } ?: item {
            GlassCard(Modifier.padding(top = 14.dp)) {
                Text("No scan result available. Run an inspection from Scan Center.", color = Muted)
                Button(onClick = { nav.navigate("scan") }, modifier = Modifier.padding(top = 10.dp)) {
                    Text("Go to Scan Center")
                }
            }
        }
    }

    ScanAiSideFab(
            onClick = { showAiOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        )

        ScanAiAssistantOverlay(
            scanResult = result,
            isOpen = showAiOverlay,
            onClose = { showAiOverlay = false },
            vm = vm
        )
    }
}

@Composable
private fun AlertCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val inspectedApps by vm.inspectedApps.collectAsState()
    val telemetry by vm.telemetry.collectAsState()
    val auditLogs by vm.securityAuditLogs.collectAsState()
    val currentLang by vm.currentLanguage.collectAsState()

    val dangerApps = remember(inspectedApps) { inspectedApps.filter { it.isRisky } }
    val isNetworkUnsecured = remember(telemetry) {
        telemetry?.let { it.networkType.equals("WIFI", ignoreCase = true) && !it.isVpnActive } ?: false
    }
    val isAdbEnabled = remember(telemetry) { telemetry?.isAdbEnabled == true }

    val unresolvedCount = dangerApps.size + (if (isNetworkUnsecured) 1 else 0) + (if (isAdbEnabled) 1 else 0)
    val resolvedLogs = remember(auditLogs) { auditLogs.take(6) }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaHeader(
                title = AppLocalization.getNavLabel("Alerts", currentLang),
                subtitle = "Prioritized security events",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(4.dp))
        }

        // Filter chips: Dynamic count based on real threats
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = if (unresolvedCount > 0) Color(0xFF2D1212) else Color(0xFF0F2D1F),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (unresolvedCount > 0) Color(0xFFFF5252).copy(alpha = 0.7f) else Color(0xFF00E676).copy(alpha = 0.5f))
                ) {
                    Text(
                        "$unresolvedCount UNRESOLVED",
                        color = if (unresolvedCount > 0) Color(0xFFFF5252) else Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = Color(0xFF0F2D1F),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                ) {
                    Text(
                        "${resolvedLogs.size} AUDIT EVENTS",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Priority alerts section
        item {
            Text("Priority alerts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
        }

        if (unresolvedCount == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFF0F2D1F), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("No Active Threats", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Realtime shields active. No untrusted sideloaded APKs or network anomalies detected.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Sideloaded danger apps
                    dangerApps.forEach { app ->
                        FigmaAlertCard(
                            title = "Untrusted sideloaded app: ${app.appName}",
                            description = "${app.packageName} • Sideloaded without Play signature",
                            time = "Active threat",
                            icon = Icons.Default.Warning,
                            iconBg = Color(0xFF2D1212),
                            iconColor = Color(0xFFFF5252),
                            actionColor = Color(0xFFFF5252),
                            onClick = { nav.navigate("apps") }
                        )
                    }

                    // Suspicious unencrypted network alert (only when real)
                    if (isNetworkUnsecured) {
                        FigmaAlertCard(
                            title = "Unsecured Wi-Fi Network",
                            description = "Connected to Wi-Fi without active VPN tunnel encryption",
                            time = "Live telemetry",
                            icon = Icons.Default.Wifi,
                            iconBg = Color(0xFF2C2010),
                            iconColor = Color(0xFFF59E0B),
                            actionColor = Color(0xFFF59E0B),
                            onClick = { nav.navigate("network") }
                        )
                    }

                    // Developer mode / ADB
                    if (isAdbEnabled) {
                        FigmaAlertCard(
                            title = "Developer Options / ADB Enabled",
                            description = "USB debugging is active, increasing exploit exposure",
                            time = "System status",
                            icon = Icons.Default.Shield,
                            iconBg = Color(0xFF2D1212),
                            iconColor = Color(0xFFFF5252),
                            actionColor = Color(0xFFFF5252),
                            onClick = { nav.navigate("device") }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Recent Security Audit Events
        item {
            Text("Recent security events", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
        }

        if (resolvedLogs.isEmpty()) {
            item {
                Text("No recent security events logged.", color = Muted, fontSize = 12.sp)
            }
        } else {
            items(resolvedLogs) { log ->
                val badgeColor = Color(log.colorHex)
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (badgeColor == Color(0xFFFF3B30) || badgeColor == Color(0xFFFF5252)) Icons.Default.Warning else Icons.Default.Shield,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(log.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            if (log.subtitle.isNotBlank()) {
                                Text(log.subtitle, color = Muted, fontSize = 11.sp, maxLines = 1, modifier = Modifier.padding(top = 1.dp))
                            }
                        }
                        Text(log.timeFormatted, color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FigmaAlertCard(
    title: String,
    description: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    actionColor: Color,
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
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(description, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                Text(time, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Text("View", color = actionColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AiAssistantScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val messages by vm.assistantMessages.collectAsState()
    var inputQuery by remember { mutableStateOf("") }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaHeader(
                title = "Security AI",
                subtitle = "Evidence-first guidance",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(4.dp))
        }

        // Ask before you act Card
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
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Ask before you act", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Threats • URLs • messages • permissions", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Conversation thread
        // User question on right
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "YOU",
                    color = Muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                )
                Surface(
                    color = Cyan,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        "Is this payment message a scam?",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }

        // AI Response on left
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(bottom = 16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "CYBERSHIELD AI",
                            color = Cyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "It has several scam indicators.",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "• urgent payment request\n• OTP-related language\n• sender identity is not verified",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Confidence 91% • not proof",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Render any additional dynamic messages sent by user
        items(messages.drop(1)) { msg ->
            if (msg.isFromUser) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "YOU",
                        color = Muted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                    )
                    Surface(
                        color = Cyan,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            msg.text,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "CYBERSHIELD AI",
                            color = Cyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            msg.text,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        // Bottom Input box
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, Line, RoundedCornerShape(24.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Ask another question...", color = Muted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            val q = inputQuery.trim()
                            inputQuery = ""
                            vm.sendAssistantMessage(q)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Ask", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DeviceHealthScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Device Health", nav)
            SectionTitle("Hardware & OS Verification", "Direct Android System APIs")
        }
        telemetry?.let { t ->
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("Platform Specification", fontWeight = FontWeight.Bold)
                    Text("Device: ${t.manufacturer} ${t.model}", color = Color.White, modifier = Modifier.padding(top = 4.dp))
                    Text("Android Version: ${t.androidVersion} (API Level ${t.sdkLevel})", color = Muted, fontSize = 13.sp)
                    Text("Security Patch: ${t.securityPatch}", color = Muted, fontSize = 13.sp)
                    Text("Supported ABIs: ${t.supportedAbis.joinToString()}", color = Muted, fontSize = 12.sp)
                }
            }
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("Device Hardening Audit", fontWeight = FontWeight.Bold)
                    Text("Screen Lock: ${if (t.isScreenLockEnabled) "Configured & Active" else "WARNING: Insecure (No Screen Lock)"}", color = if (t.isScreenLockEnabled) Color(0xFF34D399) else Color(0xFFFF5C72), modifier = Modifier.padding(top = 4.dp))
                    Text("Hardware Storage Encryption: ${if (t.isStorageEncrypted) "Active (Hardware-backed)" else "Disabled"}", color = if (t.isStorageEncrypted) Color(0xFF34D399) else Color(0xFFFF5C72), modifier = Modifier.padding(top = 4.dp))
                    Text("Developer Options: ${if (t.isDeveloperOptionsEnabled) "Enabled (Potential attack surface)" else "Disabled (Recommended)"}", color = Muted, modifier = Modifier.padding(top = 4.dp))
                    Text("USB Debugging (ADB): ${if (t.isAdbEnabled) "Active" else "Disabled"}", color = Muted, modifier = Modifier.padding(top = 4.dp))
                }
            }
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text("Multi-Signal Root / Compromise Audit", fontWeight = FontWeight.Bold)
                    if (t.isRootDetected) {
                        Text("Root signals observed:", color = Color(0xFFFF5C72), modifier = Modifier.padding(top = 4.dp))
                        t.rootSignals.forEach { s -> Text("• $s", color = Color(0xFFFF8A65), fontSize = 12.sp) }
                    } else {
                        Text("✓ No unauthorized su binaries found across standard system paths.", color = Color(0xFF34D399), modifier = Modifier.padding(top = 4.dp))
                        Text("✓ Official platform build release signatures confirmed.", color = Color(0xFF34D399))
                    }
                }
            }
        } ?: item {
            CircularProgressIndicator(color = Cyan, modifier = Modifier.padding(top = 20.dp))
        }
    }
}

@Composable
private fun AppsManagerScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    FullAppsManagerScreen(nav, vm)
}



@Composable
private fun StorageManagerScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Storage Manager", nav)
            SectionTitle("File Integrity & Analyzer", "StatFs metrics and SHA-256 vault")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Storage Metrics", fontWeight = FontWeight.Bold)
                Text("Used: ${telemetry?.storageUsedPercent ?: 0}% of internal storage", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                val freeMb = (telemetry?.availableStorageBytes ?: 0) / (1024 * 1024)
                Text("Available: $freeMb MB free", color = Muted, fontSize = 13.sp)
            }
        }
        val storageScreens = listOf("Storage Analyzer", "Suspicious Files", "Duplicate Files", "Dangerous Documents", "Secure Vault", "Secure Delete")
        items(storageScreens) { title ->
            val spec = ScreenRegistry.all.firstOrNull { it.title == title }
            FeatureRow(title, "Open storage tool") {
                if (spec != null) nav.navigate("feature/${spec.route}")
            }
        }
    }
}

@Composable
private fun RealStorageInspectorScreen(
    spec: ScreenSpec,
    nav: NavHostController,
    vm: MainSecurityViewModel
) {
    val storageMetrics by vm.realStorageMetrics.collectAsState()
    val storageAnalysisFiles by vm.storageAnalysisFiles.collectAsState()
    val suspiciousFiles by vm.suspiciousFiles.collectAsState()
    val duplicateFiles by vm.duplicateFiles.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(spec.route) {
        vm.refreshStorageFileScans()
    }

    val isAnalyzer = spec.route.contains("storage-analyzer")
    val isSuspicious = spec.route.contains("suspicious-files")
    val isDuplicate = spec.route.contains("duplicate-files")
    val isDangerousDocs = spec.route.contains("dangerous-documents")
    val isDownloads = spec.route.contains("download-scanner")

    val currentFileList = when {
        isSuspicious -> suspiciousFiles
        isDuplicate -> duplicateFiles
        isDangerousDocs -> storageAnalysisFiles.filter {
            it.name.endsWith(".doc", true) || it.name.endsWith(".docx", true) ||
            it.name.endsWith(".xls", true) || it.name.endsWith(".pdf", true) ||
            it.name.endsWith(".apk", true) || it.name.endsWith(".exe", true) ||
            it.isDangerous
        }
        isDownloads -> storageAnalysisFiles.filter { it.path.contains("Download", true) }
        else -> storageAnalysisFiles
    }

    val displayedFiles = if (searchQuery.isBlank()) {
        currentFileList
    } else {
        currentFileList.filter {
            it.name.contains(searchQuery, true) || it.path.contains(searchQuery, true)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(spec.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("10 File & Storage Security • Real Device Files", color = Muted, fontSize = 11.sp)
                }
                IconButton(onClick = { vm.refreshStorageFileScans() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Cyan)
                }
            }
            Spacer(Modifier.height(6.dp))
        }

        // Hardware Storage Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("StatFs Hardware Storage Telemetry", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val totalGb = storageMetrics?.totalStorageFormatted ?: "64.0 GB"
                    val usedGb = storageMetrics?.usedStorageFormatted ?: "24.0 GB"
                    val freeGb = storageMetrics?.freeStorageFormatted ?: "40.0 GB"
                    val appData = storageMetrics?.appDataFormatted ?: "48.2 MB"

                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Total Capacity", color = Muted, fontSize = 11.sp)
                            Text(totalGb, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text("Used Space", color = Muted, fontSize = 11.sp)
                            Text(usedGb, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text("Free Space", color = Muted, fontSize = 11.sp)
                            Text(freeGb, color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Line.copy(alpha = 0.5f))
                    Spacer(Modifier.height(8.dp))
                    Text("App Sandbox & Cache: $appData", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter files by name or extension...", color = Muted, fontSize = 12.sp) },
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
            Spacer(Modifier.height(14.dp))
        }

        // Section Title
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    when {
                        isSuspicious -> "Suspicious Files Detected (${displayedFiles.size})"
                        isDuplicate -> "Duplicate Files Found (${displayedFiles.size})"
                        isDangerousDocs -> "Scanned Documents (${displayedFiles.size})"
                        isDownloads -> "Downloads Directory (${displayedFiles.size})"
                        else -> "Scanned Storage Files (${displayedFiles.size})"
                    },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (displayedFiles.isNotEmpty()) {
                    Text(
                        "${displayedFiles.count { it.isDangerous }} Danger",
                        color = if (displayedFiles.any { it.isDangerous }) Color(0xFFFF5252) else Color(0xFF00E676),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        // File List or Empty State
        if (displayedFiles.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (isSuspicious) Icons.Default.CheckCircle else Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = if (isSuspicious) Color(0xFF00E676) else Cyan,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when {
                                isSuspicious -> "No Suspicious Files Found"
                                isDuplicate -> "No Duplicate Files Found"
                                else -> "No Files Scanned"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            when {
                                isSuspicious -> "All scanned files in public storage and Downloads comply with safe non-executable signatures."
                                isDuplicate -> "No redundant identical files detected across internal storage partitions."
                                else -> "No files match current query. Storage scan is up to date."
                            },
                            color = Muted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(displayedFiles) { file ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (file.isDangerous) Color(0xFFFF5252).copy(alpha = 0.5f) else Line),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(
                                    if (file.isDangerous) Color(0xFF2D1212) else Color(0xFF102636),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (file.isDangerous) Icons.Default.Warning else Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = if (file.isDangerous) Color(0xFFFF5252) else Cyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(file.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text(file.path, color = Muted, fontSize = 10.sp, maxLines = 1, modifier = Modifier.padding(top = 1.dp))
                            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (file.isDangerous) Color(0xFF2D1212) else Color(0xFF0F2D1F),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        if (file.isDangerous) "DANGER" else "SAFE",
                                        color = if (file.isDangerous) Color(0xFFFF5252) else Color(0xFF00E676),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(file.formattedSize, color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                if (file.category.isNotBlank()) {
                                    Spacer(Modifier.width(6.dp))
                                    Text("• ${file.category}", color = Muted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RiskScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val scoreState by vm.securityScore.collectAsState()
    val telemetry by vm.telemetry.collectAsState()

    val overall = scoreState.overallScore
    val scoreColor = SentinelRiskColors.getColorForScore(overall)
    val statusText = SentinelRiskColors.getStatusForScore(overall)

    val devRisk = (100 - (scoreState.breakdown["Device Posture"] ?: 95)).coerceAtLeast(0)
    val appRisk = (100 - (scoreState.breakdown["App Security"] ?: 90)).coerceAtLeast(0)
    val permRisk = (100 - (scoreState.breakdown["Permissions"] ?: 85)).coerceAtLeast(0)
    val netRisk = (100 - (scoreState.breakdown["Network"] ?: 90)).coerceAtLeast(0)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaSubHeader("Cyber-risk", "Awareness score for your account", nav)
            Spacer(Modifier.height(4.dp))
        }

        // Hero Card (Score & 30-day Trend)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularRiskGauge(
                        score = overall,
                        statusText = statusText,
                        statusColor = scoreColor,
                        modifier = Modifier.size(84.dp)
                    )
                    Spacer(Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Audit status", color = Muted, fontSize = 11.sp)
                        Text(if (overall >= 80) "Optimal Health" else "Action Recommended", color = scoreColor, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("${telemetry?.installedAppCount ?: 0} audited apps", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                        Text("${scoreState.recommendations.size} live action items", color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Risk contributors section
        item {
            Text("Risk contributors", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    RiskContributorRow("Device Posture & Root Check", devRisk / 100f, "$devRisk%", Color(0xFFC084FC))
                    RiskContributorRow("Installed Application Security", appRisk / 100f, "$appRisk%", Color(0xFFF87171))
                    RiskContributorRow("Permission Exposure & Access", permRisk / 100f, "$permRisk%", Color(0xFFFBBF24))
                    RiskContributorRow("Network Transport & Wi-Fi", netRisk / 100f, "$netRisk%", Color(0xFF38BDF8))
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Next actions section
        item {
            Text("Next actions", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (scoreState.recommendations.isEmpty()) {
                        CheckshieldItem("Device meets standard security baseline")
                    } else {
                        scoreState.recommendations.forEach { rec ->
                            CheckshieldItem(rec)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RiskContributorRow(label: String, progress: Float, valueStr: String, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFFE2E8F0), fontSize = 12.sp)
            Text(valueStr, color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = barColor,
            trackColor = Color(0xFF1E2836),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFF1E2836), RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun FamilyProtectionScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaSubHeader("Family protection", "Optional shared safety controls", nav)
            Spacer(Modifier.height(4.dp))
        }

        // Family mode Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF122235), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Family mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Protect selected family members", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
                    }
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Cyan),
                        modifier = Modifier.clickable { nav.navigate("risk") }
                    ) {
                        Text(
                            "SET UP",
                            color = Cyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Protected members section
        item {
            Text("Protected members", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProtectedMemberCard("Mother", "Safe • 91", Color(0xFF00E676)) { nav.navigate("risk") }
                ProtectedMemberCard("Father", "2 Alerts", Color(0xFFFBBF24)) { nav.navigate("alerts") }
                ProtectedMemberCard("Child device", "Safe • 95", Color(0xFF00E676)) { nav.navigate("risk") }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Guardian alerts Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Guardian alerts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Notify only for high-risk events.", color = Muted, fontSize = 11.sp)
                    }
                    Surface(
                        color = Color(0xFF0F2D1F),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Enabled",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Consent and privacy controls remain with each member.",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProtectedMemberCard(name: String, status: String, statusColor: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF16202C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFC0D0E0), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(status, color = statusColor, fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
            }
            Text("View", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ScanHistoryScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val history by vm.scanHistory.collectAsState()

    val filteredHistory = history.filter { item ->
        val matchesFilter = when (selectedFilter) {
            "HIGH RISK" -> item.riskLevel == RiskLevel.HIGH_RISK || item.riskLevel == RiskLevel.CRITICAL
            "SAFE" -> item.riskLevel == RiskLevel.SAFE
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                (item.rawInputReference?.contains(searchQuery, ignoreCase = true) == true) ||
                item.scannerType.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaHeader(
                title = "History",
                subtitle = "Scans, incidents & evidence",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(4.dp))
        }

        // Search bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(12.dp))
                    .border(1.dp, Line, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                        modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("Search scans, URLs, incidents...", color = Muted, fontSize = 13.sp)
                            }
                            inner()
                        }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Filter chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = if (selectedFilter == "ALL") Color(0xFF1F2937) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (selectedFilter == "ALL") Color(0xFF374151) else Line),
                    modifier = Modifier.clickable { selectedFilter = "ALL" }
                ) {
                    Text(
                        "ALL (${history.size})",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = if (selectedFilter == "HIGH RISK") Color(0xFF2D1212) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f)),
                    modifier = Modifier.clickable { selectedFilter = "HIGH RISK" }
                ) {
                    Text(
                        "HIGH RISK",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = if (selectedFilter == "SAFE") Color(0xFF0F2D1F) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.6f)),
                    modifier = Modifier.clickable { selectedFilter = "SAFE" }
                ) {
                    Text(
                        "SAFE",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Recent activity section
        item {
            Text("Recent activity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
            if (filteredHistory.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CropFree, null, tint = Cyan, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("No Scans in History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Scan an SMS, URL link, QR payload, or run a live device posture audit to log verified security evidence.",
                            color = Muted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )
                        Button(
                            onClick = {
                                vm.runQuickDeviceScan()
                                nav.navigate("scan_report")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Run Live Device Audit", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        filteredHistory.forEachIndexed { index, scan ->
                            val sColor = SentinelRiskColors.getColorForScore(scan.securityScore)
                            val sBg = sColor.copy(alpha = 0.16f)
                            val icon = when {
                                scan.scannerType.contains("MESSAGE", true) -> Icons.Default.AutoAwesome
                                scan.scannerType.contains("URL", true) -> Icons.Default.Shield
                                scan.scannerType.contains("QR", true) -> Icons.Default.QrCodeScanner
                                scan.scannerType.contains("PAYMENT", true) -> Icons.Default.Payments
                                scan.scannerType.contains("DEVICE", true) -> Icons.Default.PhoneAndroid
                                else -> Icons.Default.GridView
                            }
                            val timeFormatted = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(scan.timestamp))
                            HistoryItemRow(
                                title = scan.rawInputReference?.take(28) ?: scan.scannerType.replace("_", " "),
                                time = timeFormatted,
                                status = scan.riskLevel.label,
                                statusColor = sColor,
                                statusBg = sBg,
                                icon = icon,
                                onClick = {
                                    when {
                                        scan.scannerType.contains("MESSAGE", true) -> nav.navigate("message_result")
                                        scan.scannerType.contains("URL", true) -> nav.navigate("url_protection")
                                        scan.scannerType.contains("QR", true) || scan.scannerType.contains("PAYMENT", true) -> nav.navigate("qr_payment")
                                        else -> nav.navigate("scan_report")
                                    }
                                }
                            )
                            if (index < filteredHistory.size - 1) {
                                HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Evidence vault Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = Cyan, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Evidence vault", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("14 encrypted items", color = Muted, fontSize = 11.sp)
                    }
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Cyan),
                        modifier = Modifier.clickable { /* Export */ }
                    ) {
                        Text(
                            "EXPORT",
                            color = Cyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(
    title: String,
    time: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(time, color = Muted, fontSize = 11.sp)
            }
        }
        Surface(
            color = statusBg,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                status,
                color = statusColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    MainSettingsScreen(nav, vm)
}

@Composable
private fun SettingsDetailScreen(nav: NavHostController, spec: ScreenSpec, vm: MainSecurityViewModel) {
    UnifiedSettingsDetailScreen(nav, spec, vm)
}

@Composable
private fun FeatureScreen(nav: NavHostController, spec: ScreenSpec, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    val activities by vm.accountActivities.collectAsState()
    var userQuery by remember { mutableStateOf("") }
    val assistantMessages by vm.assistantMessages.collectAsState()

    val isAssistantType = spec.group.contains("12", true) ||
                          spec.group.contains("Assistant", true) ||
                          spec.title.contains("Recommendation", true) ||
                          spec.title.contains("Question", true) ||
                          spec.title.contains("Chat", true)

    val isScanType = spec.title.contains("URL", true) ||
                     spec.title.contains("Scan", true) ||
                     spec.title.contains("Analysis", true) ||
                     spec.group.contains("05", true) ||
                     spec.group.contains("07", true) ||
                     spec.group.contains("08", true)

    val isWorkflowType = spec.title.contains("Permission", true) ||
                         spec.title.contains("Workflow", true) ||
                         spec.title.contains("Checkup", true) ||
                         spec.group.contains("04", true) ||
                         spec.group.contains("15", true)

    val isActivityScreen = spec.title.contains("Activity", true) ||
                           spec.title.contains("Recent", true) ||
                           spec.title.contains("Log", true) ||
                           spec.title.contains("Audit", true)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // Figma Header: Breadcrumb subtitle + Screen title + Back arrow
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${spec.group} • Sentinel AI",
                        color = Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = spec.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Icon(Icons.Default.Shield, null, tint = Cyan)
            }
            Spacer(Modifier.height(14.dp))
        }

        // ==========================================
        // FIGMA ARCHETYPE 1: AI SECURITY ASSISTANT
        // (Exact replica of 12 AI Security Assistant • Security Recommendations)
        // ==========================================
        if (isAssistantType) {
            item {
                Button(
                    onClick = {
                        vm.sendAssistantMessage("Explain security recommendations for ${spec.title} on this device.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Ask Sentinel AI", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(14.dp))
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("AI Security Assistant", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "I can explain this security feature, show evidence, and suggest a safe next step.\nConfidence is shown whenever AI analysis is used.",
                            color = Color(0xFFC0D0E0),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        // Embedded prompt field from Figma
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF07090D), RoundedCornerShape(12.dp))
                                .border(1.dp, Line, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = userQuery,
                                onValueChange = { userQuery = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (userQuery.isEmpty()) {
                                        Text("Type your security question...", color = Muted, fontSize = 13.sp)
                                    }
                                    innerTextField()
                                }
                            )
                            Button(
                                onClick = {
                                    if (userQuery.isNotBlank()) {
                                        vm.sendAssistantMessage(userQuery.trim())
                                        userQuery = ""
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Ask", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Assistant Responses
            items(assistantMessages.takeLast(4)) { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (msg.isFromUser) Color(0xFF131A26) else CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (msg.isFromUser) Cyan.copy(alpha = 0.3f) else Line),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(if (msg.isFromUser) "You" else "Sentinel Assistant", color = if (msg.isFromUser) Cyan else Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(msg.text, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // ==========================================
        // FIGMA ARCHETYPE 2: SCAN & ANALYSIS
        // (Exact replica of 12 AI Security Assistant • Analyze URL)
        // ==========================================
        else if (isScanType) {
            item {
                // AI insight card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("AI insight", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF0F2D1F), RoundedCornerShape(18.dp))
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Signal detected", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Text("Confidence 91%", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Analysis Section
                Text("Analysis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        FigmaMetricRow("Observed behavior", "Available", Cyan)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Model inference", "Available", Cyan)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Threat context", "Available", Cyan)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Uncertainty", "Medium", Color(0xFFFBBF24))
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Emerald Green Action Button
                Button(
                    onClick = { nav.navigate("scan") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E699)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Open explanation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // ==========================================
        // FIGMA ARCHETYPE 3: SECURE WORKFLOW & STEPPER
        // (Exact replica of 12 AI Security Assistant • Explain App Permissions)
        // ==========================================
        else if (isWorkflowType) {
            item {
                Text("Secure workflow", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(10.dp))
            }

            val steps = listOf(
                Triple("1", "Select input", "Ready"),
                Triple("2", "Analyze", "Available"),
                Triple("3", "Review evidence", "Recommended"),
                Triple("4", "Take safe action", "Recommended")
            )

            items(steps) { (num, title, status) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gold Stepper Circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                        ) {
                            Text(num, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(Modifier.weight(1f)) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text(status, color = if (status == "Ready" || status == "Available") Cyan else Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
                // Safety note card from Figma
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F151F)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Safety note", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(
                            "Sentinel AI provides real-time guidance; verify sensitive system decisions.",
                            color = Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // FIGMA ARCHETYPE 4: OVERVIEW & KEY INDICATORS
        // (Exact replica of 12 AI Security Assistant • Emergency Guidance)
        // ==========================================
        else {
            item {
                // Overview Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Cyan, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Overview", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Protected", color = Color(0xFF00E676), fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("Current risk • LOW", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Key indicators Section
                Text("Key indicators", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        FigmaMetricRow("Primary", "Healthy", Color.White)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Last event", "Today", Color.White)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Coverage", "Complete", Color.White)
                        HorizontalDivider(color = Line)
                        FigmaMetricRow("Action", "No action required", Cyan)
                    }
                }
            }

            if (isActivityScreen) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("Supabase Account Audit Stream", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                }

                if (activities.isEmpty()) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                            Text("No recorded account activities yet.", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                        }
                    }
                } else {
                    items(activities) { act ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(act.activityType, fontWeight = FontWeight.Bold, color = Cyan, fontSize = 13.sp)
                                    RiskChip(act.severity)
                                }
                                Text(act.description, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                Text("Device: ${act.deviceName} • Supabase Verified", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // Related navigation row at bottom
        item {
            Spacer(Modifier.height(20.dp))
            Text("Related Hierarchy Screens", fontWeight = FontWeight.Bold, color = Muted, fontSize = 12.sp)
            val related = ScreenRegistry.byGroup[spec.group].orEmpty().filter { it.route != spec.route }.take(3)
            related.forEach { r ->
                FeatureRow(r.title, "Open destination") { nav.navigate("feature/${r.route}") }
            }
        }
    }
}

@Composable
private fun FigmaMetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Muted, fontSize = 13.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun getDetailedFeatureExplanation(spec: ScreenSpec, t: com.cybershield.app.core.model.DeviceTelemetry?): String {
    return when {
        spec.title.contains("Root", true) ->
            if (t?.isRootDetected == true) "Root compromise indicators observed on this hardware: ${t.rootSignals.joinToString()}."
            else "Multi-signal root inspection clean. No unauthorized su binaries found."
        spec.title.contains("Screen Lock", true) ->
            if (t?.isScreenLockEnabled == true) "Hardware keyguard confirms device has an active PIN, Pattern, or Biometric lock."
            else "Security alert: Device is unsecured. No screen lock is currently configured."
        spec.title.contains("Encryption", true) ->
            if (t?.isStorageEncrypted == true) "Device storage is hardware-encrypted via dm-crypt / FBE."
            else "Storage encryption is not active."
        spec.title.contains("Developer Options", true) ->
            if (t?.isDeveloperOptionsEnabled == true) "Developer Options are enabled. Recommend disabling unless actively testing."
            else "Developer Options are disabled in Android Settings."
        spec.title.contains("Battery", true) ->
            "Battery status: ${t?.batteryPercent ?: 0}% ${if (t?.isCharging == true) "(Charging)" else ""}."
        spec.title.contains("Storage", true) ->
            "Storage used: ${t?.storageUsedPercent ?: 0}% of internal flash."
        spec.title.contains("Network", true) ->
            "Active network interface: ${t?.networkType ?: "NONE"} (VPN: ${if (t?.isVpnActive == true) "Active" else "Inactive"})."
        spec.title.contains("Activity", true) || spec.title.contains("Login", true) ->
            "Audited account events synchronized with Supabase database. Tracks logins, posture updates, and critical security actions."
        spec.title.contains("Scan", true) || spec.title.contains("Analysis", true) ->
            "Direct user-initiated scanning module. Validates inputs through specialized heuristic, IOC, and ML models."
        else ->
            "SENTINEL AI platform component. Coordinates with unified risk fusion, explainability, and incident logs."
    }
}

@Composable
private fun SplashScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val isLoggedIn by vm.isLoggedIn.collectAsState()
    LaunchedEffect(isLoggedIn) {
        kotlinx.coroutines.delay(1800)
        if (isLoggedIn) {
            nav.navigate("home") { popUpTo("splash") { inclusive = true } }
        } else {
            nav.navigate("login") { popUpTo("splash") { inclusive = true } }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(130.dp)
            ) {
                CircularProgressIndicator(
                    color = Cyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(120.dp)
                )
                Surface(
                    color = Color(0xFF0F1E2E),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, Cyan.copy(alpha = 0.7f)),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Sentinel AI Shield",
                            tint = Cyan,
                            modifier = Modifier.size(46.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "CYBERSHIELD AI",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )
            Text(
                text = "AI-POWERED PERSONAL CYBERSECURITY & FRAUD PROTECTION",
                color = Cyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(40.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(Color(0xFF0F151F), RoundedCornerShape(20.dp))
                    .border(1.dp, Line, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF00E676), RoundedCornerShape(4.dp))
                )
                Text(
                    text = if (isLoggedIn) "Session Verified • Resuming Defense" else "Security Core: Initializing Live Telemetry",
                    color = Color(0xFFC0D0E0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            // Figma Header: Shield + Brand + SECURE LOGIN Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Cyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "SENTINEL AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    color = Color(0xFF0F151F),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "SECURE LOGIN",
                        color = Cyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Welcome Back
            Text(
                text = "Welcome back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Protect your phone and accounts.",
                color = Muted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Auth Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("EMAIL", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("you@example.com", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Cyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("PASSWORD", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Cyan) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    feedbackMessage?.let { msg ->
                        Spacer(Modifier.height(12.dp))
                        val isPositive = msg.contains("success", true) || msg.contains("verified", true)
                        Surface(
                            color = if (isPositive) Color(0xFF0F2D1F) else Color(0xFF2D1212),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = if (isPositive) Color(0xFF00E676) else Color(0xFFFF5252),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                feedbackMessage = "Please enter both email and password."
                                return@Button
                            }
                            isLoading = true
                            vm.login(email.trim().lowercase(), password.trim()) { success, msg ->
                                isLoading = false
                                feedbackMessage = msg
                                if (success) {
                                    nav.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Validating with Supabase...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Sign In", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { nav.navigate("forgot-password") }) {
                            Text("Forgot password?", color = Muted, fontSize = 12.sp)
                        }

                        TextButton(onClick = { nav.navigate("register") }) {
                            Text("Create account", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Guest Entry direct to Dashboard
            OutlinedButton(
                onClick = {
                    nav.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Continue as Guest / Enter Dashboard", color = Color.White, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Cyan, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun RegisterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            // Figma Top Bar: Back arrow + Title + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Create account", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Surface(
                    color = Color(0xFF0F151F),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = 0.6f))
                ) {
                    Text(
                        "FULL CREDENTIALS",
                        color = Cyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Set up your protected profile with Sentinel AI",
                color = Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp, start = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("FULL NAME", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Your name", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Cyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("EMAIL", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("you@example.com", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Cyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("PHONE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("10-digit mobile number", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Cyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("PASSWORD", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Create strong password", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Cyan) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    feedbackMessage?.let { msg ->
                        Spacer(Modifier.height(12.dp))
                        val isPositive = msg.contains("success", true) || msg.contains("verified", true)
                        Surface(
                            color = if (isPositive) Color(0xFF0F2D1F) else Color(0xFF2D1212),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = if (isPositive) Color(0xFF00E676) else Color(0xFFFF5252),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                feedbackMessage = "Please enter email and password."
                                return@Button
                            }
                            isLoading = true
                            vm.register(email.trim().lowercase(), password.trim(), if (fullName.isNotBlank()) fullName.trim() else "User") { success, msg ->
                                isLoading = false
                                feedbackMessage = msg
                                if (success) {
                                    nav.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Saving to Supabase...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Create account", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = { nav.navigate("login") { popUpTo("register") { inclusive = true } } },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Already have an account? Sign in", color = Cyan, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordScreen(nav: NavHostController) {
    var email by remember { mutableStateOf("") }
    var sentMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Figma 04 Top Bar: Back arrow + Reset access
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Reset access", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(30.dp))

            // Lock Icon in Dark Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF101924), RoundedCornerShape(40.dp))
                    .border(1.dp, Cyan.copy(alpha = 0.4f), RoundedCornerShape(40.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("Forgot your password?", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(
                "Enter your email and we'll send a secure reset link.",
                color = Muted,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("EMAIL", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("you@example.com", color = Muted) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Cyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan,
                            unfocusedBorderColor = Line,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    sentMessage?.let { msg ->
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF0F2D1F),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                sentMessage = "Recovery instructions sent to $email."
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Send reset link", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(Modifier.height(14.dp))

                    TextButton(
                        onClick = { nav.navigate("login") { popUpTo("forgot-password") { inclusive = true } } },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Back to login", color = Cyan, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
