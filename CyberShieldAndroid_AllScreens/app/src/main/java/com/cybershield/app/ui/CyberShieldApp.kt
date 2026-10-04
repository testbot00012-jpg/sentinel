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
            composable("protect") { ProtectScreen(nav, viewModel) }
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
    val items = listOf(
        Triple("home", "Home", Icons.Default.Home),
        Triple("protect", "Protect", Icons.Default.Shield),
        Triple("scan", "Scan", Icons.Default.CropFree),
        Triple("alerts", "Alerts", Icons.Default.NotificationsNone),
        Triple("settings", "Settings", Icons.Default.Settings)
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
private fun FigmaHeader(
    title: String,
    subtitle: String,
    nav: NavHostController,
    showBell: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
private fun FigmaSubHeader(
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

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Figma Header: "Good morning / Your phone is protected"
        item {
            FigmaHeader(
                title = "Good morning",
                subtitle = "Your phone is protected",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(2.dp))
        }

        // Status badge: PROTECTED
        item {
            Surface(
                color = Color(0xFF0F2D1F),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
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
            Spacer(Modifier.height(14.dp))
        }

        // Hero Card: Security Health
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
                            Text("91", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
                            Text("  / 100", color = Muted, fontSize = 15.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("No critical threats", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    CircularRiskGauge(
                        score = 91,
                        statusText = "SAFE",
                        statusColor = Color(0xFF00E676),
                        modifier = Modifier.size(82.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Quick Actions 2x2 Grid
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
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    title = "Storage",
                    icon = Icons.Default.Folder,
                    iconColor = Color(0xFFFBBF24),
                    onClick = { nav.navigate("storage") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Network",
                    icon = Icons.Default.Wifi,
                    iconColor = Color(0xFF34D399),
                    onClick = { nav.navigate("network") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(18.dp))
        }

        // 2 recommendations Card
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
                            .background(Color(0xFF2C2010), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("2 recommendations", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Review risky app permissions", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF1F2937),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { nav.navigate("privacy") }
                            ) {
                                Text(
                                    "Review",
                                    color = Cyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            Text("Updated now", color = Muted, fontSize = 11.sp)
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
        modifier = modifier.height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProtectScreen(nav: NavHostController, vm: MainSecurityViewModel) {
    FamilyProtectionScreen(nav, vm)
}

@Composable
private fun ScanCenterScreen(nav: NavHostController, vm: MainSecurityViewModel) {
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

        // Quick scan Card
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
                        Text("Device + apps + network", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF1F2937),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable {
                                vm.scanUrl("https://secure-pay.example")
                                nav.navigate("url_protection")
                            }
                        ) {
                            Text(
                                "START",
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
                        vm.scanMessage("Your account will be blocked unless you verify credentials immediately.")
                        nav.navigate("message_result")
                    },
                    modifier = Modifier.weight(1f)
                )
                DetectionToolCard(
                    title = "URL",
                    subtitle = "Phishing / links",
                    icon = Icons.Default.Shield,
                    iconColor = Cyan,
                    onClick = {
                        vm.scanUrl("https://secure-pay.example")
                        nav.navigate("url_protection")
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
                        vm.scanQr("upi://pay?pa=merchant@upi&pn=Merchant&am=18500")
                        nav.navigate("qr_payment")
                    },
                    modifier = Modifier.weight(1f)
                )
                DetectionToolCard(
                    title = "Payment",
                    subtitle = "Fake screenshot",
                    icon = Icons.Default.FolderOpen,
                    iconColor = Color(0xFFFBBF24),
                    onClick = {
                        vm.scanPayment("merchant@upi")
                        nav.navigate("qr_payment")
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
                    subtitle = "Permissions",
                    icon = Icons.Default.GridView,
                    iconColor = Color(0xFFFF5252),
                    onClick = { nav.navigate("apps") },
                    modifier = Modifier.weight(1f)
                )
                DetectionToolCard(
                    title = "Call",
                    subtitle = "Caller risk",
                    icon = Icons.Default.PhoneAndroid,
                    iconColor = Color(0xFF60A5FA),
                    onClick = { nav.navigate("feature/scam_call_identifier") },
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
                    onClick = { nav.navigate("feature/ai_media_scanner") },
                    modifier = Modifier.weight(1f)
                )
                DetectionToolCard(
                    title = "Investment",
                    subtitle = "Loan scams",
                    icon = Icons.Default.WarningAmber,
                    iconColor = Color(0xFFF59E0B),
                    onClick = { nav.navigate("feature/investment_fraud_analyzer") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
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
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaSubHeader("Message analysis", "SMS / WhatsApp", nav)
            Spacer(Modifier.height(4.dp))
        }

        // HIGH RISK pill badge
        item {
            Surface(
                color = Color(0xFF2D1212),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.7f))
            ) {
                Text(
                    "HIGH RISK",
                    color = Color(0xFFFF5252),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        // Headline
        item {
            Text(
                "\"Your account will be blocked...\"",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(14.dp))
        }

        // Sender & Detected signals Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sender", color = Muted, fontSize = 11.sp)
                    Text("+91 •••• 7284", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Detected signals", color = Muted, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SignalPill("Urgency", Color(0xFF2D1212), Color(0xFFFF5252))
                        SignalPill("OTP request", Color(0xFF2C2010), Color(0xFFF59E0B))
                        SignalPill("Look-alike", Color(0xFF2D1212), Color(0xFFFF5252))
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
                Text("87 / 100", color = Color(0xFFFF5252), fontSize = 38.sp, fontWeight = FontWeight.Black)
                Text("Confidence 91%", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(Modifier.height(20.dp))
        }

        // Why we flagged it
        item {
            Text("Why we flagged it", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FlaggedItem("Urgent account threat language")
                FlaggedItem("Requests sensitive credentials")
                FlaggedItem("Sender identity is unverified")
            }
            Spacer(Modifier.height(22.dp))
        }

        // Safe next action Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFF00E676)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Safe next action", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Do not reply, pay or share OTPs.", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Cyan),
                            modifier = Modifier.clickable { nav.navigate("history") }
                        ) {
                            Text(
                                "Preserve evidence",
                                color = Cyan,
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
}

@Composable
private fun UrlProtectionScreen(
    nav: NavHostController,
    result: SecurityResult?,
    vm: MainSecurityViewModel
) {
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
                    result?.rawInputReference ?: "https://secure-pay.example",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // HIGH RISK pill badge
        item {
            Surface(
                color = Color(0xFF2D1212),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.7f))
            ) {
                Text(
                    "HIGH RISK",
                    color = Color(0xFFFF5252),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        // Suspicious destination Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Suspicious destination", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Look-alike domain + redirect chain", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("3 risk signals", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
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
                    EvidenceRow("Domain age", "2 days", Color(0xFFFBBF24))
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                    EvidenceRow("Redirects", "3 hops", Color(0xFFFBBF24))
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                    EvidenceRow("Credential pattern", "Detected", Color(0xFFFF5252))
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))
                    EvidenceRow("TLS", "Valid", Color(0xFF00E676))
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Do not open this link button
        item {
            Button(
                onClick = { nav.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Do not open this link", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun QrPaymentScreen(
    nav: NavHostController,
    result: SecurityResult?,
    vm: MainSecurityViewModel
) {
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
                                color = Color(0xFF2C2010),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "SUSPICIOUS",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Recipient", color = Muted, fontSize = 11.sp)
                        Text("merchant@upi", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Amount", color = Muted, fontSize = 11.sp)
                        Text("₹18,500", color = Color(0xFFFBBF24), fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }
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
                    CheckshieldItem("Recipient matches your intent")
                    CheckshieldItem("Amount matches what you expect")
                    CheckshieldItem("No urgent or coercive instruction")
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Button
        item {
            Button(
                onClick = { /* review action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10151E)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Review in your payment app", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            FigmaHeader(
                title = "Alerts",
                subtitle = "Prioritized security events",
                nav = nav,
                showBell = true
            )
            Spacer(Modifier.height(4.dp))
        }

        // Filter chips: 2 UNRESOLVED / 7 RESOLVED
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFF2D1212),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.7f))
                ) {
                    Text(
                        "2 UNRESOLVED",
                        color = Color(0xFFFF5252),
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
                        "7 RESOLVED",
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

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Alert 1: High risk app
                FigmaAlertCard(
                    title = "High risk app",
                    description = "SMS + accessibility access",
                    time = "8 min ago",
                    icon = Icons.Default.Warning,
                    iconBg = Color(0xFF2D1212),
                    iconColor = Color(0xFFFF5252),
                    actionColor = Color(0xFFFF5252),
                    onClick = { nav.navigate("apps") }
                )

                // Alert 2: Suspicious network
                FigmaAlertCard(
                    title = "Suspicious network",
                    description = "Unknown connection blocked",
                    time = "1 hr ago",
                    icon = Icons.Default.Wifi,
                    iconBg = Color(0xFF2C2010),
                    iconColor = Color(0xFFF59E0B),
                    actionColor = Color(0xFFF59E0B),
                    onClick = { nav.navigate("network") }
                )

                // Alert 3: Security update
                FigmaAlertCard(
                    title = "Security update",
                    description = "Patch available",
                    time = "Today",
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFF102636),
                    iconColor = Cyan,
                    actionColor = Cyan,
                    onClick = { nav.navigate("device") }
                )

                // Alert 4: Evidence reminder
                FigmaAlertCard(
                    title = "Evidence reminder",
                    description = "Export not configured",
                    time = "Yesterday",
                    icon = Icons.Default.WarningAmber,
                    iconBg = Color(0xFF25122D),
                    iconColor = Color(0xFFA855F7),
                    actionColor = Color(0xFFA855F7),
                    onClick = { nav.navigate("history") }
                )
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
                        score = 82,
                        statusText = "LOW RISK",
                        statusColor = Color(0xFF00E676),
                        modifier = Modifier.size(84.dp)
                    )
                    Spacer(Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("30-day trend", color = Muted, fontSize = 11.sp)
                        Text("↓ 12%", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("2 risky apps", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                        Text("1 blocked URL", color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
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
                    RiskContributorRow("Apps", 0.31f, "31%", Color(0xFFC084FC))
                    RiskContributorRow("Phishing exposure", 0.22f, "22%", Color(0xFFF87171))
                    RiskContributorRow("Account hygiene", 0.15f, "15%", Color(0xFFFBBF24))
                    RiskContributorRow("Network", 0.08f, "8%", Color(0xFF38BDF8))
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
                    CheckshieldItem("Review app permissions")
                    CheckshieldItem("Enable security updates")
                    CheckshieldItem("Complete privacy checkup")
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
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Search scans, URLs, incidents...", color = Muted, fontSize = 13.sp)
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
                        "ALL",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = Color(0xFF2D1212),
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
            }
            Spacer(Modifier.height(18.dp))
        }

        // Recent activity section
        item {
            Text("Recent activity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Item 1: Message scan
                    HistoryItemRow(
                        title = "Message scan",
                        time = "09:12",
                        status = "HIGH RISK",
                        statusColor = Color(0xFFFF5252),
                        statusBg = Color(0xFF2D1212),
                        icon = Icons.Default.AccessTime,
                        onClick = { nav.navigate("message_result") }
                    )
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    // Item 2: Wi-Fi check
                    HistoryItemRow(
                        title = "Wi-Fi check",
                        time = "08:41",
                        status = "SAFE",
                        statusColor = Color(0xFF00E676),
                        statusBg = Color(0xFF0F2D1F),
                        icon = Icons.Default.Wifi,
                        onClick = { nav.navigate("network") }
                    )
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    // Item 3: APK analysis
                    HistoryItemRow(
                        title = "APK analysis",
                        time = "Yesterday",
                        status = "SUSPICIOUS",
                        statusColor = Color(0xFFFBBF24),
                        statusBg = Color(0xFF2C2010),
                        icon = Icons.Default.GridView,
                        onClick = { nav.navigate("apps") }
                    )
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    // Item 4: QR scan
                    HistoryItemRow(
                        title = "QR scan",
                        time = "Yesterday",
                        status = "SAFE",
                        statusColor = Color(0xFF00E676),
                        statusBg = Color(0xFF0F2D1F),
                        icon = Icons.Default.QrCodeScanner,
                        onClick = { nav.navigate("qr_payment") }
                    )
                    HorizontalDivider(color = Line.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    // Item 5: Payment screenshot
                    HistoryItemRow(
                        title = "Payment screenshot",
                        time = "Sep 30",
                        status = "HIGH RISK",
                        statusColor = Color(0xFFFF5252),
                        statusBg = Color(0xFF2D1212),
                        icon = Icons.Default.Payments,
                        onClick = { nav.navigate("qr_payment") }
                    )
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
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
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
                onClick = { nav.navigate("settings_detail/user_account") },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF122235), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Your account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Protected • app lock enabled", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // 8 Settings Menu items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsMenuItem(
                    title = "Protection",
                    subtitle = "Real-time + scan schedule",
                    icon = Icons.Default.Shield,
                    iconColor = Cyan,
                    onClick = { nav.navigate("settings_detail/realtime_protection_settings") }
                )
                SettingsMenuItem(
                    title = "Notifications",
                    subtitle = "Alerts + quiet hours",
                    icon = Icons.Default.NotificationsNone,
                    iconColor = Color(0xFFA855F7),
                    onClick = { nav.navigate("settings_detail/notification_preferences") }
                )
                SettingsMenuItem(
                    title = "Privacy",
                    subtitle = "Permissions + retention",
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFF00E676),
                    onClick = { nav.navigate("privacy") }
                )
                SettingsMenuItem(
                    title = "AI settings",
                    subtitle = "Models + explanations",
                    icon = Icons.Default.AutoAwesome,
                    iconColor = Cyan,
                    onClick = { nav.navigate("ai") }
                )
                SettingsMenuItem(
                    title = "Trusted devices",
                    subtitle = "Sessions + emergency",
                    icon = Icons.Default.PhoneAndroid,
                    iconColor = Color(0xFFFBBF24),
                    onClick = { nav.navigate("device") }
                )
                SettingsMenuItem(
                    title = "Data & storage",
                    subtitle = "Export + secure delete",
                    icon = Icons.Default.Folder,
                    iconColor = Color(0xFFFBBF24),
                    onClick = { nav.navigate("storage") }
                )
                SettingsMenuItem(
                    title = "Language",
                    subtitle = "English • Telugu • Hindi",
                    icon = Icons.Default.Language,
                    iconColor = Color(0xFFA855F7),
                    onClick = { nav.navigate("settings_detail/language_selector") }
                )
                SettingsMenuItem(
                    title = "Security logs",
                    subtitle = "Audit & access history",
                    icon = Icons.Default.AccessTime,
                    iconColor = Muted,
                    onClick = { nav.navigate("history") }
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "CyberShield • Android consumer app",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SettingsMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
