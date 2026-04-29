package com.habitquest.ui.screen.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import com.habitquest.ui.theme.*

@Composable
fun XpProgressCard(
    currentLevel: Level,
    xpInCurrentLevel: Int,
    xpToNextLevel: Int,
    totalXP: Int,
    modifier: Modifier = Modifier
) {
    val pct = if (xpToNextLevel > 0) {
        (xpInCurrentLevel.toFloat() / xpToNextLevel).coerceIn(0f, 1f)
    } else 1f
    val animPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(900),
        label = "xpProgressAnim"
    )
    val nextLevel = Levels.all.find { it.level == currentLevel.level + 1 }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, DividerLight, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EXPERIENCIA",
                style = AppTypography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Text(
                text = "$totalXP XP total",
                style = AppTypography.labelSmall,
                color = Amber,
                fontSize = 10.sp
            )
        }

        // Level names
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentLevel.name,
                style = AppTypography.labelLarge,
                color = currentLevel.color
            )
            if (nextLevel != null) {
                Text(
                    text = nextLevel.name,
                    style = AppTypography.labelSmall,
                    color = TextDimmer,
                    fontSize = 11.sp
                )
            } else {
                Text(
                    text = "MAX",
                    style = AppTypography.labelSmall,
                    color = Amber,
                    fontSize = 11.sp
                )
            }
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardBackground2)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animPct)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Amber, Red))
                    )
            )
        }

        // XP numbers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$xpInCurrentLevel XP",
                style = AppTypography.labelSmall,
                color = Amber,
                fontSize = 10.sp
            )
            Text(
                text = if (nextLevel != null) "$xpToNextLevel XP para subir"
                       else "Nivel máximo alcanzado",
                style = AppTypography.labelSmall,
                color = TextDimmer,
                fontSize = 10.sp
            )
        }
    }
}
