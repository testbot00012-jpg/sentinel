package com.cybershield.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Line = Color(0xFF252D39)
private val Muted = Color(0xFF8E99AA)

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF10141B), RoundedCornerShape(20.dp))
            .border(1.dp, Line, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
}

@Composable
fun FeatureRow(title: String, value: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151A22)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(value, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF31D7FF))
        }
    }
}

@Composable
fun RiskChip(level: String) {
    val color = when(level.lowercase()) {
        "critical" -> Color(0xFFFF5C72)
        "high" -> Color(0xFFFF8A65)
        "medium", "suspicious" -> Color(0xFFFBBF24)
        else -> Color(0xFF34D399)
    }
    Surface(color = color.copy(alpha=.14f), shape = RoundedCornerShape(50)) {
        Text(level, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal=10.dp, vertical=6.dp))
    }
}
