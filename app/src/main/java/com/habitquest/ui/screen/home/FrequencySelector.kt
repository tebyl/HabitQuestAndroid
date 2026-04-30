package com.habitquest.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Background
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.Divider
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextMuted

enum class HabitFrequency(val key: String, val label: String) {
    DAILY("daily", "Todos los dias"),
    SPECIFIC_DAYS("specific", "Dias especificos"),
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "¿Cuándo quieres hacerlo?",
            style = AppTypography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitFrequency.entries.forEach { freq ->
                val isSelected = selected == freq
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Purple.copy(alpha = 0.14f) else CardBackground2)
                        .border(
                            1.dp,
                            if (isSelected) Purple.copy(alpha = 0.42f) else Divider,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectFrequency(freq) }
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = freq.label,
                        style = AppTypography.labelLarge,
                        color = if (isSelected) Purple else TextDim,
                        fontSize = 12.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selected == HabitFrequency.SPECIFIC_DAYS,
            enter = fadeIn(tween(180)) + expandVertically(tween(180))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    weekDays.forEach { (label, key, _) ->
                        val isOn = key in selectedDays
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(if (isOn) Amber.copy(alpha = 0.20f) else CardBackground2)
                                .border(
                                    1.dp,
                                    if (isOn) Amber.copy(alpha = 0.62f) else Divider,
                                    CircleShape
                                )
                                .clickable { onToggleDay(key) }
                        ) {
                            Text(
                                text = label,
                                style = AppTypography.labelSmall,
                                color = if (isOn) Amber else TextDim,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                if (showValidationErrors && selectedDays.isEmpty()) {
                    Text(
                        text = "Elige al menos un dia",
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
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..7).forEach { count ->
                        val isSelected = timesPerWeek == count
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(if (isSelected) Amber.copy(alpha = 0.20f) else CardBackground2)
                                .border(
                                    1.dp,
                                    if (isSelected) Amber.copy(alpha = 0.62f) else Divider,
                                    CircleShape
                                )
                                .clickable { onSelectTimesPerWeek(count) }
                        ) {
                            Text(
                                text = count.toString(),
                                style = AppTypography.labelSmall,
                                color = if (isSelected) Amber else TextDim,
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
