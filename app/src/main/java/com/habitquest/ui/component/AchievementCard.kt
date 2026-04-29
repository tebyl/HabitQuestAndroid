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
    val borderColor = if (achievement.isUnlocked) Amber.copy(alpha = 0.30f) else Divider.copy(alpha = 0.55f)
    val bgBrush = if (achievement.isUnlocked)
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.94f), Amber.copy(alpha = 0.16f))
        )
    else
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.72f), Divider.copy(alpha = 0.18f))
        )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(22.dp))
            .then(if (!achievement.isUnlocked) Modifier.graphicsLayer(alpha = 0.72f) else Modifier)
            .padding(14.dp)
    ) {
        Text(text = achievement.icon, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = achievement.name,
            style = AppTypography.titleMedium,
            color = if (achievement.isUnlocked) TextPrimary else TextDim
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = achievement.description,
            style = AppTypography.labelSmall,
            color = TextDim
        )
    }
}
