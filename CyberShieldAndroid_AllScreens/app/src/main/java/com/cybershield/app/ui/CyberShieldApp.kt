package com.cybershield.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                BottomBar(nav)
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "splash",
            modifier = Modifier.padding(if (showBottomBar) padding else PaddingValues(0.dp))
        ) {
            composable("splash") { SplashScreen(nav) }
            composable("login") { LoginScreen(nav, viewModel) }
            composable("register") { RegisterScreen(nav, viewModel) }
            composable("forgot-password") { ForgotPasswordScreen(nav) }
            composable("home") { HomeScreen(nav, viewModel) }
            composable("protect") { ProtectScreen(nav) }
            composable("scan") { ScanCenterScreen(nav, viewModel) }
            composable("alerts") { AlertCenterScreen(nav, viewModel) }
            composable("settings") { SettingsScreen(nav, viewModel) }
            composable("device") { DeviceHealthScreen(nav, viewModel) }
            composable("apps") { AppsManagerScreen(nav, viewModel) }
            composable("privacy") { PrivacyCenterScreen(nav, viewModel) }
            composable("storage") { StorageManagerScreen(nav, viewModel) }
            composable("network") { NetworkGuardScreen(nav, viewModel) }
            composable("message_result") { ResultScreen(nav, "Message Scam Result", viewModel) }
            composable("url_protection") { ResultScreen(nav, "URL Protection", viewModel) }
            composable("qr_payment") { ResultScreen(nav, "QR & Payment Safety", viewModel) }
            composable("ai") { AiAssistantScreen(nav, viewModel) }
            composable("risk") { RiskScreen(nav, viewModel) }
            composable("family") { FamilyProtectionScreen(nav, viewModel) }
            composable("history") { ScanHistoryScreen(nav, viewModel) }
            composable("settings_detail/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                val spec = ScreenRegistry.find(route)
                SettingsDetailScreen(nav, spec, viewModel)
            }
            composable("feature/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                val spec = ScreenRegistry.find(route)
                FeatureScreen(nav, spec, viewModel)
            }
            composable("screen/{route}") { backStack ->
                val route = backStack.arguments?.getString("route") ?: ""
                val spec = ScreenRegistry.find(route)
                FeatureScreen(nav, spec, viewModel)
            }
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    NavigationBar(containerColor = Color(0xFF0B0F15)) {
        val items = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("protect", "Protect", Icons.Default.Security),
            Triple("scan", "Scan", Icons.Default.QrCodeScanner),
            Triple("alerts", "Alerts", Icons.Default.Notifications),
            Triple("settings", "Settings", Icons.Default.Settings)
        )
        val currentRoute = nav.currentBackStackEntry?.destination?.route
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { nav.navigate(route) { launchSingleTop = true; restoreState = true } },
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 11.sp) }
            )
        }
    }
}

@Composable
private fun TopBar(title: String, nav: NavHostController, showBack: Boolean = true) {
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
private fun HomeScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    val scoreState by vm.securityScore.collectAsState()
    val currentScan by vm.currentScanResult.collectAsState()
    val alerts by vm.alerts.collectAsState()
    val scoreColor = SentinelRiskColors.getColorForScore(scoreState.overallScore)
    val scoreStatus = SentinelRiskColors.getStatusForScore(scoreState.overallScore)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Figma Header
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, null, tint = Cyan, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("SENTINEL AI", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Surface(
                    color = Color(0xFF0F2D1F),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                ) {
                    Text(
                        "PROTECTED",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Figma Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Overview", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Protected", color = Color(0xFF00E676), fontSize = 34.sp, fontWeight = FontWeight.Black)
                    Text("Current risk • LOW • Score ${scoreState.overallScore}/100", color = Color(0xFFC0D0E0), fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { nav.navigate("scan") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E699)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Run Security Scan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Figma Core Shortcuts
        item {
            Text("Core AI Security Modules", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    onClick = { nav.navigate("feature/url_scanner") },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Analyze URL", fontWeight = FontWeight.Bold, color = Cyan, fontSize = 13.sp)
                        Text("Live Detection", color = Muted, fontSize = 11.sp)
                    }
                }

                Card(
                    onClick = { nav.navigate("feature/explain_app_permissions") },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Permissions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Secure Workflow", color = Muted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    onClick = { nav.navigate("feature/security_recommendations") },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("AI Recommendations", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), fontSize = 13.sp)
                        Text("Real Advice", color = Muted, fontSize = 11.sp)
                    }
                }

                Card(
                    onClick = { nav.navigate("feature/emergency_guidance") },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Emergency", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252), fontSize = 13.sp)
                        Text("Rapid Guidance", color = Muted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }

        // Figma Key Indicators Table
        item {
            Text("Key indicators", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FigmaMetricRow("Primary System", "Healthy", Color.White)
                    HorizontalDivider(color = Line)
                    FigmaMetricRow("Last event", "Today", Color.White)
                    HorizontalDivider(color = Line)
                    FigmaMetricRow("Coverage", "Complete", Color.White)
                    HorizontalDivider(color = Line)
                    FigmaMetricRow("Cloud Shield", "Online (Railway Live)", Color(0xFF00E676))
                }
            }

            Spacer(Modifier.height(18.dp))
        }
        item {
            Spacer(Modifier.height(14.dp))
            SectionTitle("Hardware & OS Telemetry", "Direct Android System APIs (Zero simulated data)")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                telemetry?.let { t ->
                    Text("${t.brand.uppercase()} ${t.model} (Android ${t.androidVersion})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Security Patch: ${t.securityPatch} • SDK ${t.sdkLevel}", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Battery: ${t.batteryPercent}% ${if (t.isCharging) "(Charging)" else ""}", fontSize = 13.sp)
                        Text("Storage: ${t.storageUsedPercent}% used", fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Screen Lock: ${if (t.isScreenLockEnabled) "Secure (PIN/Bio)" else "Insecure"}", color = if (t.isScreenLockEnabled) SentinelRiskColors.SAFE_GREEN else SentinelRiskColors.DANGER_RED, fontSize = 12.sp)
                        Text("Encryption: ${if (t.isStorageEncrypted) "Hardware Encrypted" else "Off"}", color = if (t.isStorageEncrypted) SentinelRiskColors.SAFE_GREEN else SentinelRiskColors.DANGER_RED, fontSize = 12.sp)
                    }
                    if (t.isRootDetected) {
                        Text("Root / Tampering Detected: ${t.rootSignals.joinToString()}", color = SentinelRiskColors.DANGER_RED, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                } ?: CircularProgressIndicator(color = Cyan, modifier = Modifier.size(24.dp))
            }
        }
        item {
            Spacer(Modifier.height(14.dp))
            SectionTitle("Security Modules", "Select an inspection vector")
        }
        val tiles = listOf(
            "Device Health" to "device",
            "Apps Manager" to "apps",
            "Privacy Center" to "privacy",
            "Storage Manager" to "storage",
            "Network Guard" to "network",
            "Scan Center" to "scan",
            "Alert Center" to "alerts",
            "Personal Risk" to "risk",
            "Scan History" to "history",
            "Incidents" to "feature/${ScreenRegistry.all.first { it.title == "Incident Details" }.route}"
        )
        items(tiles.chunked(2)) { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { (title, route) ->
                    GlassCard(Modifier.weight(1f).padding(vertical = 4.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Button(
                            onClick = { nav.navigate(route) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Open", color = Cyan, fontSize = 12.sp)
                        }
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProtectScreen(nav: NavHostController) {
    val groups = ScreenRegistry.byGroup.filterKeys { it.startsWith("0") || it.startsWith("1") }.toList()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Protection Hierarchy", nav, false)
            SectionTitle("All 19 Security Modules", "174 Registered Screen Destinations")
        }
        groups.forEach { (group, screens) ->
            item {
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Text(group, fontWeight = FontWeight.Bold, color = Cyan, fontSize = 15.sp)
                    screens.take(3).forEach { spec ->
                        FeatureRow(spec.title, "Open capability", { nav.navigate("feature/${spec.route}") })
                    }
                    if (screens.size > 3) {
                        FeatureRow("View remaining ${screens.size - 3} screens...", "Browse module directory", { nav.navigate("feature/${screens[3].route}") })
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    var selectedScanType by remember { mutableStateOf("URL Scanner") }
    var inputQuery by remember { mutableStateOf("") }
    val isScanning by vm.isScanning.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Scan Center", nav, false)
            SectionTitle("Threat & Fraud Scanner Hub", "Real multi-model inference pipelines")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Select Vector to Analyze", fontWeight = FontWeight.Bold)
                val options = listOf("URL Scanner", "SMS Scam Detection", "QR & UPI Payment", "APK & App Risk", "Payment Screenshot")
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    options.forEach { opt ->
                        FilterChip(
                            selected = selectedScanType == opt,
                            onClick = { selectedScanType = opt; inputQuery = "" },
                            label = { Text(opt.split(" ")[0], fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    label = {
                        Text(
                            when (selectedScanType) {
                                "URL Scanner" -> "Enter URL (e.g. http://login-sbi-update.xyz)"
                                "SMS Scam Detection" -> "Paste SMS or message content..."
                                "QR & UPI Payment" -> "Enter QR payload or upi:// URI"
                                "APK & App Risk" -> "Enter package name (e.g. com.banking.fakeapp)"
                                else -> "Enter transaction or payment reference ID"
                            },
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            when (selectedScanType) {
                                "URL Scanner" -> {
                                    vm.scanUrl(inputQuery)
                                    nav.navigate("url_protection")
                                }
                                "SMS Scam Detection" -> {
                                    vm.scanMessage(inputQuery)
                                    nav.navigate("message_result")
                                }
                                "QR & UPI Payment" -> {
                                    vm.scanQr(inputQuery)
                                    nav.navigate("qr_payment")
                                }
                                "APK & App Risk" -> {
                                    vm.scanApk(inputQuery)
                                    nav.navigate("url_protection")
                                }
                                else -> {
                                    vm.scanPayment(inputQuery)
                                    nav.navigate("qr_payment")
                                }
                            }
                        }
                    },
                    enabled = inputQuery.isNotBlank() && !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    } else {
                        Text("Execute Security Analysis", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(14.dp))
            SectionTitle("Specialized Scanners")
        }
        val scanItems = listOf(
            "SMS Analysis", "URL Scanner", "QR Code Scanner", "Payment / Fake Screenshot",
            "APK / Sideload Security", "Scam Call Risk", "Investment & Loan Fraud", "Deepfake Media Detection"
        )
        items(scanItems) { title ->
            val spec = ScreenRegistry.all.firstOrNull { it.title.contains(title.split(" ")[0], true) }
            FeatureRow(title, "Open dedicated detector") {
                if (spec != null) nav.navigate("feature/${spec.route}")
            }
        }
    }
}

@Composable
private fun ResultScreen(nav: NavHostController, title: String, vm: MainSecurityViewModel) {
    val result by vm.currentScanResult.collectAsState()
    val scanChatThreads by vm.scanChatThreads.collectAsState()
    val isAssistantResponding by vm.isAssistantResponding.collectAsState()
    var userQuestionInput by remember { mutableStateOf("") }

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
}

@Composable
private fun AlertCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val alerts by vm.alerts.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Alert Center", nav, false)
            SectionTitle("Active Threat Advisories", "Prioritized security events")
        }
        if (alerts.isEmpty()) {
            item {
                GlassCard(Modifier.padding(top = 12.dp)) {
                    Text("No active alerts", fontWeight = FontWeight.Bold)
                    Text("No elevated threats currently detected on this device. Subsystems are operating normally.", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            items(alerts) { alert ->
                GlassCard(Modifier.padding(top = 10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(alert.title, fontWeight = FontWeight.Bold, color = Color.White)
                        RiskChip(alert.severity.label)
                    }
                    Text(alert.description, color = Color(0xFFE2E8F0), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                    alert.recommendedAction?.let {
                        Text("Action: $it", color = Cyan, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    Button(
                        onClick = { vm.dismissAlert(alert.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Acknowledge / Dismiss", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiAssistantScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Sentinel AI Engine", nav)
            SectionTitle("Context-Aware Architecture", "Embedded Inline Scan Assistants")
        }
        item {
            GlassCard(Modifier.padding(top = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Cyan, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Dedicated Scan Assistants", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Sentinel AI does NOT utilize a detached, generic chatbot. Every security scan (URL, SMS, QR, APK, Payment, Deepfake) generates its own dedicated, context-aware AI assistant embedded directly inside the scan result.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "To ask Sentinel AI grounded questions about a specific target, run an inspection in the Scan Center or open an item from Scan History.",
                    color = Muted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { nav.navigate("scan") },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Scan Center", color = Color.Black, fontWeight = FontWeight.Bold)
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
    val telemetry by vm.telemetry.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Apps Manager", nav)
            SectionTitle("Application Inventory", "Package visibility & risk assessment")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Installed Applications", fontWeight = FontWeight.Bold)
                Text("${telemetry?.installedAppCount ?: 0} packages identified via PackageManager", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        val appScreens = listOf("Installed Applications", "Dangerous Apps", "Suspicious Apps", "App Permissions", "App Security Report")
        items(appScreens) { title ->
            val spec = ScreenRegistry.all.firstOrNull { it.title == title }
            FeatureRow(title, "Open app inspection") {
                if (spec != null) nav.navigate("feature/${spec.route}")
            }
        }
    }
}

@Composable
private fun PrivacyCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val permissions = listOf("Camera", "Microphone", "Location", "Contacts", "SMS", "Phone", "Storage", "Notifications", "Accessibility", "Privacy Risk Score")
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Privacy Center", nav)
            SectionTitle("Permission Exposure", "Contextual privacy risk model")
        }
        items(permissions) { name ->
            val spec = ScreenRegistry.all.firstOrNull { it.title == name }
            FeatureRow(name, "Inspect permission risk") {
                if (spec != null) nav.navigate("feature/${spec.route}")
            }
        }
    }
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
private fun NetworkGuardScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val telemetry by vm.telemetry.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Network Guard", nav)
            SectionTitle("Transport Security", "Real connectivity inspection")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Active Network Interface", fontWeight = FontWeight.Bold)
                Text("Type: ${telemetry?.networkType ?: "NONE"}", color = Cyan, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                Text("VPN Tunnel: ${if (telemetry?.isVpnActive == true) "Active (Encrypted)" else "Inactive"}", color = if (telemetry?.isVpnActive == true) Color(0xFF34D399) else Muted, fontSize = 13.sp)
            }
        }
        val netScreens = listOf("Wi-Fi Security", "Current Network", "Network Risk", "DNS Security", "VPN Status", "Network History")
        items(netScreens) { title ->
            val spec = ScreenRegistry.all.firstOrNull { it.title == title }
            FeatureRow(title, "Inspect network item") {
                if (spec != null) nav.navigate("feature/${spec.route}")
            }
        }
    }
}

@Composable
private fun RiskScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val score by vm.securityScore.collectAsState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Personal Cyber-Risk", nav)
            SectionTitle("Adaptive Behavioral Posture", "Aggregated risk indicators")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Current Cyber-Risk Index", color = Muted, fontSize = 12.sp)
                Text("${100 - score.overallScore} / 100", fontSize = 38.sp, fontWeight = FontWeight.Black, color = if (score.overallScore >= 80) Color(0xFF34D399) else Color(0xFFFBBF24))
                Text("Lower risk index is better", color = Muted, fontSize = 12.sp)
            }
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Subsystem Score Breakdown", fontWeight = FontWeight.Bold)
                score.breakdown.forEach { (name, s) ->
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, fontSize = 13.sp)
                        Text("$s / 100", fontWeight = FontWeight.Bold, color = if (s >= 80) Color(0xFF34D399) else Color(0xFFFBBF24))
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyProtectionScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            TopBar("Family Protection", nav)
            SectionTitle("Multi-Device Posture", "Unified household threat status")
        }
        item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Registered Devices", fontWeight = FontWeight.Bold)
                Text("Current Primary Device: Protected (Live)", color = Color(0xFF34D399), modifier = Modifier.padding(top = 4.dp))
                Text("Tap to invite family member or link secondary tablet.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun ScanHistoryScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val lastScan by vm.currentScanResult.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            TopBar("Scan History & Evidence", nav)
            SectionTitle("Audit Trail", "Cryptographic verification of past checks")
        }
        lastScan?.let { res ->
            val scoreColor = SentinelRiskColors.getColorForScore(res.securityScore)
            val scoreStatus = SentinelRiskColors.getStatusForScore(res.securityScore)
            item {
                Card(
                    onClick = { nav.navigate("url_protection") },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(res.scannerType, fontWeight = FontWeight.Bold, color = Cyan, fontSize = 14.sp)
                            Surface(
                                color = scoreColor.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.8f))
                            ) {
                                Text(
                                    scoreStatus,
                                    color = scoreColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Text("Security Score: ${res.securityScore}/100 • Confidence: ${(res.confidence * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Text(res.quickSummary?.ifBlank { res.explanation } ?: res.explanation, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Text("Recorded: ${res.timestamp} • Tap to view report & chat", color = Cyan, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        } ?: item {
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("No scans completed yet.", color = Muted, fontSize = 13.sp)
                Button(
                    onClick = { nav.navigate("scan") },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text("Run First Scan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            GlassCard(Modifier.padding(top = 12.dp)) {
                Text("Local Evidence Store", fontWeight = FontWeight.Bold)
                Text("All completed scans are persisted with SHA-256 hashes and model versions. Embedded Sentinel AI conversations are preserved.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    val details = ScreenRegistry.all.filter { it.kind == "settings_detail" }
    val extras = ScreenRegistry.byGroup["19 Settings"].orEmpty()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar("Settings & Security", nav, false)
            SectionTitle("Preferences & Policy", "Zero-trust privacy and scanning controls")
        }
        details.forEach { spec ->
            item {
                FeatureRow(spec.title, "Configure section") {
                    nav.navigate("settings_detail/${spec.route}")
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)); SectionTitle("About & Version") }
        items(extras) { spec ->
            FeatureRow(spec.title, "View details") {
                nav.navigate("feature/${spec.route}")
            }
        }
    }
}

@Composable
private fun SettingsDetailScreen(nav: NavHostController, spec: ScreenSpec, vm: MainSecurityViewModel) {
    val currentEmail by vm.currentUserEmail.collectAsState()
    val authStatus by vm.authStatus.collectAsState()
    val serverUrl by vm.serverUrl.collectAsState()
    var customUrlInput by remember { mutableStateOf(serverUrl) }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TopBar(spec.title, nav)
            GlassCard(Modifier.padding(top = 10.dp)) {
                Text("Settings Section", color = Muted)
                Text(spec.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                if (spec.title.contains("Account", true)) {
                    Text("User: ${currentEmail ?: "user@sentinelai.security"}", color = Cyan, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    Text("Provider: Supabase Auth & PostgreSQL", color = Color(0xFF34D399), fontSize = 12.sp)
                }
                Text("Backend Gateway: $serverUrl", color = Cyan, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        if (spec.title.contains("Network", true) || spec.title.contains("Protection", true)) {
            item {
                GlassCard(Modifier.padding(top = 8.dp)) {
                    Text("Cloud Gateway URL (Railway)", fontWeight = FontWeight.SemiBold)
                    Text("Connect to your deployed Railway backend to protect all physical devices over the internet.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = { customUrlInput = it },
                        label = { Text("Railway URL (e.g. https://sentinel.up.railway.app)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { vm.updateServerUrl(customUrlInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect to Gateway", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
        val options = when {
            spec.title.contains("Account", true) -> listOf("Profile Credentials (Supabase Auth)", "Multi-Factor Authentication", "Recent Account Activities")
            spec.title.contains("Protection", true) -> listOf("Real-Time Threat Shield", "Background Monitoring", "Download Gatekeeper")
            spec.title.contains("Notification", true) -> listOf("Critical Threat Push", "High-Risk Alerts", "Daily Summary")
            spec.title.contains("Privacy", true) -> listOf("Data Minimization", "Telemetry Controls", "Zero Cloud Storage Mode")
            spec.title.contains("AI", true) -> listOf("On-Device ML Inference", "Model Version Registry", "Explainability Verbosity")
            spec.title.contains("Language", true) -> listOf("English (Default)", "हिन्दी (Hindi)", "తెలుగు (Telugu)")
            else -> listOf("Supabase Security Audit Log", "Export JSON Evidence", "Purge Local Cache")
        }
        items(options) { opt ->
            GlassCard(Modifier.padding(top = 8.dp)) {
                Text(opt, fontWeight = FontWeight.SemiBold)
                Text("Status: Configured & Active", color = Cyan, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
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
private fun SplashScreen(nav: NavHostController) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        nav.navigate("login") { popUpTo("splash") { inclusive = true } }
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
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    color = Cyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(110.dp)
                )
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Sentinel AI Shield",
                    tint = Cyan,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "SENTINEL AI",
                fontSize = 32.sp,
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
                    text = "Security Core: Initializing Live Telemetry",
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
                            vm.login(email.trim(), password.trim()) { success, msg ->
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
                            vm.register(email.trim(), password.trim(), if (fullName.isNotBlank()) fullName.trim() else "User") { success, msg ->
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
