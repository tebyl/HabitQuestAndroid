package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

@Composable
fun DailyMissionCard(
    completedHabits: Int,
    totalHabits: Int,
    modifier: Modifier = Modifier
) {
    val isAllDone = totalHabits > 0 && completedHabits == totalHabits
    val progress = if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "missionProgress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    if (isAllDone)
                        listOf(Color.White.copy(alpha = 0.92f), Emerald.copy(alpha = 0.12f))
                    else
                        listOf(Color.White.copy(alpha = 0.86f), Amber.copy(alpha = 0.08f))
                )
            )
            .border(
                1.dp,
                if (isAllDone) Emerald.copy(alpha = 0.24f) else Amber.copy(alpha = 0.18f),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isAllDone) "\uD83C\uDF3F" else "\uD83C\uDFAF",
            fontSize = 28.sp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "MISION DIARIA",
                style = AppTypography.labelSmall,
                color = if (isAllDone) Emerald else Amber,
                letterSpacing = 1.sp,
                fontSize = 9.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isAllDone) "Dia perfecto completado" else "Completa tu ritual de hoy",
                style = AppTypography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Emerald.copy(alpha = 0.14f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animProgress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (isAllDone) listOf(Emerald, Emerald)
                                else listOf(Amber, Orange)
                            )
                        )
                )
            }

            Spacer(Modifier.height(5.dp))
            Text(
                text = "$completedHabits / $totalHabits hábitos",
                style = AppTypography.labelSmall,
                color = TextDim,
                fontSize = 11.sp
            )
        }
    }
}
