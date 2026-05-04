package com.habitquest.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Red

enum class HabitFrequency(val key: String, val label: String) {
    DAILY("daily", "Todos los días"),
    SPECIFIC_DAYS("specific", "Días específicos"),
    TIMES_PER_WEEK("times", "X veces por semana")
}

data class FrequencySelection(
    val type: HabitFrequency,
    val selectedDays: Set<String> = emptySet(),
    val timesPerWeek: Int = 1
)

private val weekDays = listOf(
    Triple("L", "mon", 1),
    Triple("M", "tue", 2),
    Triple("M", "wed", 3),
    Triple("J", "thu", 4),
    Triple("V", "fri", 5),
    Triple("S", "sat", 6),
    Triple("D", "sun", 7)
)

@Composable
fun FrequencySelector(
    selected: HabitFrequency,
    selectedDays: Set<String>,
    timesPerWeek: Int,
    showValidationErrors: Boolean,
    onSelectFrequency: (HabitFrequency) -> Unit,
    onToggleDay: (String) -> Unit,
    onSelectTimesPerWeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "¿Cuándo quieres hacerlo?",
            style = AppTypography.labelSmall,
            color = colors.onSurfaceVariant,
            fontSize = 10.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HabitFrequency.entries.forEach { freq ->
                val isSelected = selected == freq
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 1f,
                    animationSpec = tween(150),
                    label = "frequencyOptionScale"
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) colors.primaryContainer else colors.surfaceVariant)
                        .clickable { onSelectFrequency(freq) }
                        .padding(horizontal = 8.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = freq.label,
                        style = AppTypography.labelLarge,
                        color = if (isSelected) colors.primary else colors.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selected == HabitFrequency.SPECIFIC_DAYS,
            enter = fadeIn(tween(180)) + expandVertically(tween(180))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    weekDays.forEach { (label, key, _) ->
                        val isOn = key in selectedDays
                        val scale by animateFloatAsState(
                            targetValue = if (isOn) 1.08f else 1f,
                            animationSpec = tween(140),
                            label = "dayChipScale"
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(CircleShape)
                                .background(if (isOn) colors.primaryContainer else colors.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isOn) colors.primary else colors.outlineVariant,
                                    CircleShape
                                )
                                .clickable { onToggleDay(key) }
                        ) {
                            Text(
                                text = label,
                                style = AppTypography.labelSmall,
                                color = if (isOn) colors.primary else colors.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                if (showValidationErrors && selectedDays.isEmpty()) {
                    Text(
                        text = "Elige al menos un día",
                        style = AppTypography.labelSmall,
                        color = Red,
                        fontSize = 11.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selected == HabitFrequency.TIMES_PER_WEEK,
            enter = fadeIn(tween(180)) + expandVertically(tween(180))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..7).forEach { count ->
                        val isSelected = timesPerWeek == count
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1f,
                            animationSpec = tween(140),
                            label = "timesChipScale"
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(CircleShape)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) colors.primary else colors.outlineVariant,
                                    CircleShape
                                )
                                .clickable { onSelectTimesPerWeek(count) }
                        ) {
                            Text(
                                text = count.toString(),
                                style = AppTypography.labelSmall,
                                color = if (isSelected) colors.primary else colors.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                if (showValidationErrors && timesPerWeek < 1) {
                    Text(
                        text = "Elige al menos 1 vez por semana",
                        style = AppTypography.labelSmall,
                        color = Red,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

fun buildFrequencyString(
    frequency: HabitFrequency,
    selectedDays: Set<String>,
    timesPerWeek: Int
): String =
    when (frequency) {
        HabitFrequency.DAILY -> "daily"
        HabitFrequency.SPECIFIC_DAYS ->
            "specific:${weekDays.filter { it.second in selectedDays }.joinToString(",") { it.second }}"
        HabitFrequency.TIMES_PER_WEEK -> "times:${timesPerWeek.coerceIn(1, 7)}"
    }

fun parseFrequencyString(value: String): FrequencySelection =
    when {
        value.startsWith("times:") -> {
            val count = value.substringAfter("times:").toIntOrNull()?.coerceIn(1, 7) ?: 1
            FrequencySelection(HabitFrequency.TIMES_PER_WEEK, timesPerWeek = count)
        }
        value == "weekly" -> FrequencySelection(HabitFrequency.TIMES_PER_WEEK, timesPerWeek = 1)
        value.startsWith("specific:") -> {
            val days = value.substringAfter("specific:")
                .split(",")
                .filter { it.isNotBlank() }
                .toSet()
            FrequencySelection(HabitFrequency.SPECIFIC_DAYS, selectedDays = days)
        }
        else -> FrequencySelection(HabitFrequency.DAILY)
    }
