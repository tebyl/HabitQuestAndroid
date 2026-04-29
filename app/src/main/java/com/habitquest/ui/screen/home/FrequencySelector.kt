package com.habitquest.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

enum class HabitFrequency(val key: String, val label: String) {
    DAILY("daily", "Diario"),
    SPECIFIC_DAYS("specific", "Días"),
    WEEKLY("weekly", "Semanal")
}

private val weekDays = listOf(
    "L" to "mon", "M" to "tue", "X" to "wed",
    "J" to "thu", "V" to "fri", "S" to "sat", "D" to "sun"
)

@Composable
fun FrequencySelector(
    selected: HabitFrequency,
    selectedDays: Set<String>,
    onSelectFrequency: (HabitFrequency) -> Unit,
    onToggleDay: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackground2)
        ) {
            HabitFrequency.entries.forEach { freq ->
                val isSelected = selected == freq
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Amber else CardBackground2)
                        .clickable { onSelectFrequency(freq) }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = freq.label,
                        style = AppTypography.labelLarge,
                        color = if (isSelected) Background else TextDim,
                        fontSize = 12.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selected == HabitFrequency.SPECIFIC_DAYS,
            enter = fadeIn(tween(200)) + expandVertically(tween(200))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDays.forEach { (label, key) ->
                    val isOn = key in selectedDays
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(if (isOn) Amber.copy(alpha = 0.2f) else CardBackground2)
                            .border(
                                1.dp,
                                if (isOn) Amber.copy(alpha = 0.6f) else Divider,
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
        }
    }
}

fun buildFrequencyString(frequency: HabitFrequency, selectedDays: Set<String>): String =
    when (frequency) {
        HabitFrequency.DAILY         -> "daily"
        HabitFrequency.WEEKLY        -> "weekly"
        HabitFrequency.SPECIFIC_DAYS ->
            "specific:${weekDays.filter { it.second in selectedDays }.joinToString(",") { it.second }}"
    }
