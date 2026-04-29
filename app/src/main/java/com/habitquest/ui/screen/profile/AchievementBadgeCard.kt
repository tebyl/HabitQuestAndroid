package com.habitquest.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

@Composable
fun AchievementBadgeCard(
    achievement: ProfileAchievement,
    modifier: Modifier = Modifier
) {
    val unlocked = achievement.unlocked

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (unlocked)
                    Brush.linearGradient(listOf(Color(0xFF1C1208), Color(0xFF2D1D04)))
                else
                    Brush.linearGradient(listOf(CardBackground, CardBackground))
            )
            .border(
                width = if (unlocked) 2.dp else 1.dp,
                color = if (unlocked) Amber.copy(alpha = 0.6f) else DividerLight,
                shape = RoundedCornerShape(14.dp)
            )
            .graphicsLayer { alpha = if (unlocked) 1f else 0.45f }
            .padding(14.dp)
    ) {
        // Icon row with lock overlay when locked
        Box {
            Text(
                text = achievement.icon,
                fontSize = 28.sp
            )
            if (!unlocked) {
                Text(
                    text = "🔒",
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = achievement.title,
            style = AppTypography.titleMedium,
            color = if (unlocked) Amber else TextDimmer,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = achievement.description,
            style = AppTypography.labelSmall,
            color = if (unlocked) TextDim else TextDimmer,
            fontSize = 10.sp
        )
    }
}
