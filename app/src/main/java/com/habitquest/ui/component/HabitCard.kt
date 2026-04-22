package com.habitquest.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Habit
import com.habitquest.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HabitCard(
    habit: Habit,
    xpGain: Int,
    onComplete: (Long) -> Unit,
    onUncomplete: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (habit.category) {
        "salud_mental"  -> Purple
        "salud_fisica"  -> Emerald
        "desarrollo"    -> Blue
        "productividad" -> Amber
        "vida_diaria"   -> Orange
        "gamificacion"  -> Rose
        else            -> Gray
    }

    val scope = rememberCoroutineScope()
    var showXP by remember { mutableStateOf(false) }

    val xpAlpha by animateFloatAsState(
        targetValue = if (showXP) 1f else 0f,
        animationSpec = tween(300),
        label = "xpAlpha"
    )
    val xpOffsetY by animateFloatAsState(
        targetValue = if (showXP) -28f else 0f,
        animationSpec = tween(if (showXP) 1200 else 100),
        label = "xpOffset"
    )
    val scale by animateFloatAsState(
        targetValue = if (habit.completedToday) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    if (habit.completedToday)
                        listOf(Color(0xFF365314), Color(0xFF3F6212))
                    else
                        listOf(CardBackground, CardBackground2)
                )
            )
            .border(
                1.dp,
                if (habit.completedToday) Emerald else DividerLight,
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                if (habit.completedToday) {
                    onUncomplete?.invoke(habit.id)
                } else {
                    onComplete(habit.id)
                    scope.launch {
                        showXP = true
                        delay(1200)
                        showXP = false
                    }
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (habit.completedToday) Color(0x3384CC16)
                        else categoryColor.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (habit.completedToday) Color(0x6684CC16)
                        else categoryColor.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Text(text = habit.icon, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = AppTypography.titleMedium,
                        color = if (habit.completedToday) Emerald else TextPrimary
                    )
                    Text(
                        text = habit.category.replace("_", " ").uppercase(),
                        style = AppTypography.labelSmall,
                        color = categoryColor,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.13f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🔥 ${habit.streakCount} días",
                        style = AppTypography.labelSmall,
                        color = if (habit.streakCount >= 3) Red else TextDim
                    )
                    Text(
                        text = "· ${habit.totalDays} total",
                        style = AppTypography.labelSmall,
                        color = TextDimmer
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (habit.completedToday) Emerald else Color.Transparent)
                    .border(2.dp, if (habit.completedToday) Emerald else DividerLight, CircleShape)
            ) {
                if (habit.completedToday) {
                    Text("✓", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        if (xpAlpha > 0f) {
            Text(
                text = "+$xpGain XP",
                style = AppTypography.labelLarge,
                color = Amber.copy(alpha = xpAlpha),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = xpOffsetY.dp)
                    .padding(end = 4.dp)
            )
        }
    }
}
