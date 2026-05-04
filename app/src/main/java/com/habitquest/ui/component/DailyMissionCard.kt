package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    val colors = MaterialTheme.colorScheme
    val accentColor = if (isAllDone) colors.secondary else colors.primary
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "missionProgress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = accentColor,
                spotColor = accentColor
            )
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(22.dp))
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
                color = accentColor,
                letterSpacing = 1.sp,
                fontSize = 9.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isAllDone) "Dia perfecto completado" else "Completa tu ritual de hoy",
                style = AppTypography.titleMedium,
                color = colors.onSurface
            )
            Spacer(Modifier.height(8.dp))

            NeonProgressBar(
                progress = animProgress,
                height = 6.dp,
                startColor = accentColor,
                endColor = if (isAllDone) colors.tertiary else colors.secondary
            )

            Spacer(Modifier.height(5.dp))
            Text(
                text = "$completedHabits / $totalHabits hábitos",
                style = AppTypography.labelSmall,
                color = colors.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
