package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Achievement
import com.habitquest.ui.theme.*

@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val borderColor = if (achievement.isUnlocked) Color(0xFF92400E) else Divider
    val bgBrush = if (achievement.isUnlocked)
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(Color(0xFF1C1917), Color(0xFF292524))
        )
    else
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(Color(0xFF0F172A), Color(0xFF0F172A))
        )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .then(if (!achievement.isUnlocked) Modifier.graphicsLayer(alpha = 0.6f) else Modifier)
            .padding(14.dp)
    ) {
        Text(text = achievement.icon, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = achievement.name,
            style = AppTypography.titleMedium,
            color = if (achievement.isUnlocked) Amber else TextDim
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = achievement.description,
            style = AppTypography.labelSmall,
            color = TextDim
        )
    }
}
