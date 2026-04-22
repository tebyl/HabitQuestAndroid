package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

@Composable
fun CircularProgressRing(
    completed: Int,
    total: Int,
    size: Dp = 72.dp,
    strokeWidth: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    val pct = if (total > 0) completed.toFloat() / total else 0f
    val animatedPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(durationMillis = 800),
        label = "ring"
    )

    val ringBaseColor = CardBackground2
    val textColor = TextPrimary

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            val arcSize = androidx.compose.ui.geometry.Size(
                this.size.width - strokePx,
                this.size.height - strokePx
            )

            drawArc(
                color = ringBaseColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            rotate(-90f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Amber, Red, Amber)
                    ),
                    startAngle = 0f,
                    sweepAngle = animatedPct * 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        Text(
            text = "$completed/$total",
            style = AppTypography.titleMedium,
            color = textColor,
            fontSize = if (size >= 72.dp) 16.sp else 12.sp
        )
    }
}
