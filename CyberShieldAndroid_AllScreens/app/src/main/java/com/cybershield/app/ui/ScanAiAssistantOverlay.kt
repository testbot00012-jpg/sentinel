package com.cybershield.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cybershield.app.core.model.ScanChatMessage
import com.cybershield.app.core.model.SecurityResult
import com.cybershield.app.core.model.SentinelRiskColors
import com.cybershield.app.ui.viewmodel.MainSecurityViewModel
import kotlinx.coroutines.launch

private val Cyan = Color(0xFF31D7FF)
private val DarkBg = Color(0xFF090D14)
private val CardBg = Color(0xFF111722)
private val Line = Color(0xFF222B38)
private val Muted = Color(0xFF8E99AA)

/**
 * Floating side option button to trigger the contextual AI assistant overlay.
 */
@Composable
fun ScanAiSideFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0B1320).copy(alpha = 0.95f),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Cyan, Color(0xFF00E676)))),
        shadowElevation = 10.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Ask AI",
                tint = Cyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Ask AI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

/**
 * Fullscreen translucent glassmorphic overlay for contextual scan chatbot.
 */
@Composable
fun ScanAiAssistantOverlay(
    scanResult: SecurityResult?,
    isOpen: Boolean,
    onClose: () -> Unit,
    vm: MainSecurityViewModel
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
    ) {
        val scanId = scanResult?.scanId ?: "active_scan"
        val chatThreads by vm.scanChatThreads.collectAsState()
        val isResponding by vm.isAssistantResponding.collectAsState()
        val messages = chatThreads[scanId] ?: emptyList()
        var inputText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        val score = scanResult?.securityScore ?: 85
        val scoreColor = SentinelRiskColors.getColorForScore(score)
        val scoreStatus = SentinelRiskColors.getStatusForScore(score)

        // Context-aware suggested prompts
        val suggestedPrompts = remember(scanResult) {
            val type = scanResult?.scannerType ?: "GENERAL"
            when (type) {
                "DEEPFAKE_DETECTOR" -> listOf(
                    "How did you know this is AI / Fake?",
                    "What artifacts were found in the photo?",
                    "Can I use this image for KYC or verification?",
                    "Is it safe to share or post?",
                    "Explain the forensics in simple words"
                )
                "PAYMENT_FRAUD", "QR_FRAUD" -> listOf(
                    "Is it safe to pay this recipient?",
                    "What is a UPI Collect scam?",
                    "Why was this payment flagged as dangerous?",
                    "What should I do right now?",
                    "How does entering UPI PIN work?"
                )
                "CALL_VERIFIER" -> listOf(
                    "Should I answer this phone number?",
                    "Is this number spoofed?",
                    "How do I block and report this caller?",
                    "Why did you give this risk score?",
                    "Can scammers steal money through calls?"
                )
                "SMS_MESSAGE_SCAM" -> listOf(
                    "Why is this message dangerous?",
                    "What happens if I click the link?",
                    "Can scammers steal my banking OTP?",
                    "What should I do right now?",
                    "Explain in simple terms"
                )
                "URL_PHISHING" -> listOf(
                    "Will this website steal my password?",
                    "How did you identify phishing?",
                    "What is the legitimate website URL?",
                    "Is it safe to open in incognito?"
                )
                else -> listOf(
                    "Why did you give this score?",
                    "Why is this dangerous?",
                    "What should I do now?",
                    "What should I avoid?",
                    "Explain in simple words"
                )
            }
        }

        // Auto-scroll when new messages arrive
        LaunchedEffect(messages.size, isResponding) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size)
            }
        }

        // Semi-transparent backdrop overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClose() },
            contentAlignment = Alignment.BottomCenter
        ) {
            // Main Glass Chat Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkBg.copy(alpha = 0.98f)),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, Cyan.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb inner clicks */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Cyan.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Sentinel AI Contextual Assistant",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Live scan analysis • Dynamic replies",
                                color = Cyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Active Scan Badge
                    Surface(
                        color = CardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    scanResult?.scannerType ?: "CURRENT SCAN",
                                    color = Cyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    scanResult?.rawInputReference ?: "Active Scan Item",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                            Surface(
                                color = scoreColor.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    "$score/100 • $scoreStatus",
                                    color = scoreColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Suggested Questions Chips Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestedPrompts) { prompt ->
                            Surface(
                                color = Color(0xFF151C28),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Cyan.copy(alpha = 0.3f)),
                                modifier = Modifier.clickable {
                                    vm.askScanAssistant(scanId, prompt)
                                }
                            ) {
                                Text(
                                    text = prompt,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Message Stream Area
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Welcome Assistant Greeting
                        item {
                            AiChatBubble(
                                text = "👋 I have thoroughly evaluated this **${scanResult?.scannerType ?: "Scan"}**. Security Score: **$score/100 ($scoreStatus)**. You can ask me anything about the detected risk factors, whether you can trust it, or what immediate protective steps you should take.",
                                acts = scanResult?.recommendedActions ?: emptyList(),
                                reasoning = "Active Telemetry Grounding"
                            )
                        }

                        // Message Thread
                        items(messages) { msg ->
                            if (msg.isFromUser) {
                                UserChatBubble(text = msg.text)
                            } else {
                                AiChatBubble(
                                    text = msg.text,
                                    acts = msg.recommendedActions,
                                    reasoning = msg.reasoningSummary
                                )
                            }
                        }

                        // Responding / Typing Indicator
                        if (isResponding) {
                            item {
                                Surface(
                                    color = CardBg,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Cyan.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Cyan,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Sentinel AI is analyzing threat signals...",
                                            color = Cyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Bottom Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask anything about this scan...", color = Muted, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan,
                                unfocusedBorderColor = Line,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = CardBg,
                                unfocusedContainerColor = CardBg
                            ),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isResponding) {
                                    val query = inputText.trim()
                                    inputText = ""
                                    vm.askScanAssistant(scanId, query)
                                }
                            },
                            enabled = inputText.isNotBlank() && !isResponding,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank() && !isResponding) Cyan else Line)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank() && !isResponding) Color.Black else Muted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserChatBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = Color(0xFF133E54),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun AiChatBubble(
    text: String,
    acts: List<String> = emptyList(),
    reasoning: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            border = BorderStroke(1.dp, Line),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = Cyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "SENTINEL AI",
                        color = Cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (!reasoning.isNullOrBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "• $reasoning",
                            color = Muted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = text,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                if (acts.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Recommended Next Steps:",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    acts.forEach { act ->
                        Text(
                            "✓ $act",
                            color = SentinelRiskColors.SAFE_GREEN,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
