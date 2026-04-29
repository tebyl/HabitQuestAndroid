package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Blue
import com.habitquest.ui.theme.CardBackground
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.DividerLight
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Gray
import com.habitquest.ui.theme.Orange
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextMuted
import com.habitquest.ui.theme.TextPrimary

private data class StreakMilestone(
    val days: Int,
    val reward: String,
    val color: Color
)

private val streakMilestones = listOf(
    StreakMilestone(days = 3, reward = "Constancia", color = Orange),
    StreakMilestone(days = 7, reward = "Ritmo", color = Blue),
    StreakMilestone(days = 14, reward = "Flujo", color = Amber),
    StreakMilestone(days = 30, reward = "Plenitud", color = Purple)
)

@Composable
fun StreakRewardCard(
    currentStreakDays: Int,
    bestStreakDays: Int,
    modifier: Modifier = Modifier
) {
    val nextMilestone = streakMilestones.firstOrNull { it.days > currentStreakDays }
    val activeColor = nextMilestone?.color ?: Emerald
    val bestUnlocked = streakMilestones.lastOrNull { bestStreakDays >= it.days }
    val remainingDays = nextMilestone?.let { (it.days - currentStreakDays).coerceAtLeast(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(CardBackground.copy(alpha = 0.96f), activeColor.copy(alpha = 0.10f))))
            .border(1.dp, activeColor.copy(alpha = 0.22f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(activeColor.copy(alpha = 0.16f))
                    .border(1.dp, activeColor.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${currentStreakDays}d",
                    style = AppTypography.labelLarge,
                    color = activeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TU RITMO",
                    style = AppTypography.labelSmall,
                    color = activeColor,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = nextMilestone?.let { "Un paso suave hacia ${it.reward}" }
                        ?: "Tu constancia esta floreciendo",
                    style = AppTypography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = remainingDays?.let { "$it dias mas para tu proximo momento" }
                        ?: "Momento alcanzado: ${bestUnlocked?.reward ?: "Plenitud"}",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    fontSize = 11.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StreakStat(
                label = "Actual",
                value = "${currentStreakDays}d",
                color = activeColor,
                modifier = Modifier.weight(1f)
            )
            StreakStat(
                label = "Mejor",
                value = "${bestStreakDays}d",
                color = bestUnlocked?.color ?: Gray,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            streakMilestones.forEach { milestone ->
                StreakMilestoneChip(
                    milestone = milestone,
                    unlocked = bestStreakDays >= milestone.days,
                    isNext = nextMilestone?.days == milestone.days,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StreakStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground2.copy(alpha = 0.65f))
            .border(1.dp, DividerLight.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = AppTypography.titleMedium,
            color = color
        )
    }
}

@Composable
private fun StreakMilestoneChip(
    milestone: StreakMilestone,
    unlocked: Boolean,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (unlocked || isNext) milestone.color else Gray
    val alpha = when {
        unlocked -> 1f
        isNext -> 0.78f
        else -> 0.42f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = if (unlocked) 0.16f else 0.08f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (isNext) 0.42f else 0.18f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 9.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${milestone.days}",
                style = AppTypography.labelSmall,
                color = color.copy(alpha = alpha),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text = milestone.reward,
            style = AppTypography.labelSmall,
            color = if (unlocked || isNext) color.copy(alpha = alpha) else TextDimmer,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (unlocked) "Logrado" else "Por abrir",
            style = AppTypography.labelSmall,
            color = if (unlocked) Emerald else TextDimmer,
            fontSize = 8.sp,
            textAlign = TextAlign.Center
        )
    }
}
