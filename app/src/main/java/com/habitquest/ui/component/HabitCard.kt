package com.habitquest.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Habit
import com.habitquest.ui.screen.home.categoryColor
import com.habitquest.ui.screen.home.categoryIconFor
import com.habitquest.ui.screen.home.categoryLabel
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.CardBackground
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.DividerLight
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HabitCard(
    habit: Habit,
    xpGain: Int,
    onComplete: (Long) -> Unit,
    onUncomplete: ((Long) -> Unit)? = null,
    onEdit: (Habit) -> Unit = {},
    onDelete: (Habit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categoryColor = categoryColor(habit.category)
    val categoryIcon = categoryIconFor(habit.category)
    val scope = rememberCoroutineScope()
    var showXP by remember { mutableStateOf(false) }
    var pulseTarget by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

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
        targetValue = if (pulseTarget) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "cardScale"
    )
    val completionAlpha by animateFloatAsState(
        targetValue = if (habit.completedToday) 0.96f else 1f,
        animationSpec = tween(260),
        label = "habitCompletionFade"
    )

    LaunchedEffect(habit.completedToday) {
        if (habit.completedToday) {
            pulseTarget = true
            delay(120)
            pulseTarget = false
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(completionAlpha)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    if (habit.completedToday)
                        listOf(Emerald.copy(alpha = 0.22f), CardBackground.copy(alpha = 0.96f))
                    else
                        listOf(CardBackground.copy(alpha = 0.96f), CardBackground2.copy(alpha = 0.72f))
                )
            )
            .border(
                1.dp,
                if (habit.completedToday) Emerald.copy(alpha = 0.32f) else DividerLight.copy(alpha = 0.55f),
                RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (habit.completedToday) Emerald.copy(alpha = 0.16f)
                        else categoryColor.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (habit.completedToday) Emerald.copy(alpha = 0.28f)
                        else categoryColor.copy(alpha = 0.22f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = if (habit.completedToday) Emerald else categoryColor,
                    modifier = Modifier.size(24.dp)
                )
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
                        text = categoryLabel(habit.category).uppercase(),
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
                        text = "${habit.streakCount} dias de racha",
                        style = AppTypography.labelSmall,
                        color = if (habit.streakCount >= 3) Red else TextDim
                    )
                    Text(
                        text = "${habit.totalDays} total",
                        style = AppTypography.labelSmall,
                        color = TextDimmer
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Purple.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Opciones",
                            tint = TextDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar", style = AppTypography.bodyMedium, color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = Purple) },
                            onClick = {
                                menuExpanded = false
                                onEdit(habit)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", style = AppTypography.bodyMedium, color = Red) },
                            leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = Red) },
                            onClick = {
                                menuExpanded = false
                                onDelete(habit)
                            }
                        )
                    }
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
