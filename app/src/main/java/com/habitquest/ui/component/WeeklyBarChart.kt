package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyBarChart(
    data: List<Pair<LocalDate, Int>>,
    barColor: Color = Amber,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val maxVal = data.maxOfOrNull { it.second }.takeIf { it != null && it > 0 } ?: 1

    var triggered by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "barChartAnim"
    )
    LaunchedEffect(Unit) { triggered = true }

    val dayLabels = data.map { (date, _) ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).take(1).uppercase()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Count labels above bars
        Row(modifier = Modifier.fillMaxWidth()) {
            data.forEach { (_, count) ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (count > 0) {
                        Text(
                            text = "$count",
                            style = AppTypography.labelSmall,
                            color = barColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(2.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val slotW = size.width / data.size
            val barW = slotW * 0.55f
            val corner = CornerRadius(6.dp.toPx())

            data.forEachIndexed { i, (_, count) ->
                val frac = (count.toFloat() / maxVal) * animProgress
                val barH = frac * size.height
                val x = i * slotW + (slotW - barW) / 2f

                drawRoundRect(
                    color = barColor.copy(alpha = 0.08f),
                    topLeft = Offset(x, 0f),
                    size = Size(barW, size.height),
                    cornerRadius = corner
                )
                if (barH > 0f) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, size.height - barH),
                        size = Size(barW, barH),
                        cornerRadius = corner
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            data.forEachIndexed { i, (date, _) ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = dayLabels[i],
                        style = AppTypography.labelSmall,
                        color = if (date == today) barColor else TextDimmer,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
