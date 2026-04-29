package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

data class PieSegment(val label: String, val value: Int, val color: Color)

@Composable
fun CategoryPieChart(
    segments: List<PieSegment>,
    centerText: String = "",
    modifier: Modifier = Modifier
) {
    if (segments.isEmpty()) return

    val total = segments.sumOf { it.value }.coerceAtLeast(1)

    var triggered by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "pieAnim"
    )
    LaunchedEffect(Unit) { triggered = true }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 32.dp.toPx()
                val padding = strokeWidth / 2f
                val arcTopLeft = Offset(padding, padding)
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                var startAngle = -90f
                segments.forEach { seg ->
                    val proportionAngle = (seg.value.toFloat() / total) * 360f
                    val sweepAngle = proportionAngle * animProgress
                    drawArc(
                        color = seg.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.coerceAtLeast(0f),
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += proportionAngle
                }

                // Background ring (drawn first but we've already drawn arcs, so re-draw outline)
                // No-op: arcs cover the full ring when data fills 100%
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = centerText,
                    style = AppTypography.headlineMedium,
                    color = TextPrimary,
                    fontSize = 24.sp
                )
                Text(
                    text = "total",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Legend grid (2 columns)
        val chunked = segments.chunked(2)
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            chunked.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { seg ->
                        val pct = ((seg.value.toFloat() / total) * 100).toInt()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(seg.color)
                            )
                            Text(
                                text = "${seg.label} · $pct%",
                                style = AppTypography.labelSmall,
                                color = TextDim,
                                fontSize = 10.sp
                            )
                        }
                    }
                    if (row.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
