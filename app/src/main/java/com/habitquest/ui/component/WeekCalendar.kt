package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun WeekCalendar(
    completedDates: Set<String>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    var weekOffset by remember { mutableIntStateOf(0) }
    val referenceDay = today.plusWeeks(weekOffset.toLong())
    val startOfWeek  = referenceDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val shortFmt     = DateTimeFormatter.ofPattern("d MMM", Locale("es"))

    Column(modifier = modifier.fillMaxWidth()) {
        // Navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◂",
                color = TextDim,
                fontSize = 16.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { weekOffset-- }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                text = if (weekOffset == 0) "Esta semana"
                       else "${startOfWeek.format(shortFmt)} – ${startOfWeek.plusDays(6).format(shortFmt)}",
                style = AppTypography.labelSmall,
                color = if (weekOffset == 0) Amber else TextDim,
                fontSize = 10.sp
            )
            Text(
                text = "▸",
                color = if (weekOffset < 0) TextDim else TextDimmer,
                fontSize = 16.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(enabled = weekOffset < 0) { weekOffset++ }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Days grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackground)
                .border(1.dp, Divider, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { offset ->
                val day      = startOfWeek.plusDays(offset.toLong())
                val isToday  = day == today
                val isFuture = day.isAfter(today)
                val dateStr  = day.toString()
                val isActive = completedDates.contains(dateStr)
                val dayLabel = day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("es"))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = dayLabel,
                        style = AppTypography.labelSmall,
                        color = if (isToday) Amber else TextDim,
                        fontSize = 10.sp
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .then(
                                when {
                                    isActive -> Modifier.background(
                                        Brush.linearGradient(listOf(Amber, Red))
                                    )
                                    isToday  -> Modifier
                                        .background(CardBackground2)
                                        .border(2.dp, Amber, CircleShape)
                                    isFuture -> Modifier.background(CardBackground)
                                    else     -> Modifier.background(CardBackground2)
                                }
                            )
                    ) {
                        Text(
                            text = when {
                                isActive && !isFuture -> "✓"
                                isFuture              -> ""
                                else                  -> "${day.dayOfMonth}"
                            },
                            color = when {
                                isActive -> TextPrimary
                                isToday  -> Amber
                                else     -> TextDimmer
                            },
                            fontSize = if (isActive) 12.sp else 10.sp
                        )
                    }
                }
            }
        }
    }
}
