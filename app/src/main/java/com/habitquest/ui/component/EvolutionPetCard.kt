package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.gamification.PetStage
import com.habitquest.domain.gamification.PetState
import com.habitquest.ui.theme.*

@Composable
fun EvolutionPetCard(
    petState: PetState,
    modifier: Modifier = Modifier
) {
    val stageColor = petState.stage.stageColor()
    val progress by animateFloatAsState(
        targetValue = petState.progressToNext,
        animationSpec = tween(800),
        label = "petEvolutionProgress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(CardBackground, stageColor.copy(alpha = 0.14f))))
            .border(1.dp, stageColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(stageColor.copy(alpha = 0.18f))
                    .border(2.dp, stageColor.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = petState.stage.icon, fontSize = 36.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMPANERO",
                        style = AppTypography.labelSmall,
                        color = stageColor,
                        letterSpacing = 1.sp,
                        fontSize = 9.sp
                    )
                    if (petState.streakBonusActive) {
                        Text(
                            text = "RACHA +",
                            style = AppTypography.labelSmall,
                            color = Amber,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Amber.copy(alpha = 0.12f))
                                .border(1.dp, Amber.copy(alpha = 0.28f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = petState.stage.label,
                    style = AppTypography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = petState.stage.phrase,
                    style = AppTypography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = petState.nextStage?.let { "Proxima evolucion: ${it.label}" } ?: "Evolucion completa",
                    style = AppTypography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = petState.nextStage?.let { "${petState.xpToNext} XP" } ?: "MAX",
                    style = AppTypography.labelSmall,
                    color = stageColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CardBackground2)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(stageColor, Amber)))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PetMetric("Racha", "${petState.streak}d", if (petState.streakBonusActive) Amber else TextDim, Modifier.weight(1f))
            PetMetric("Habitos", petState.habitsCompleted.toString(), Emerald, Modifier.weight(1f))
            PetMetric("Tareas", petState.tasksCompleted.toString(), Blue, Modifier.weight(1f))
            PetMetric("Poder", petState.companionPower.toString(), stageColor, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PetMetric(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackground2.copy(alpha = 0.62f))
            .border(1.dp, DividerLight, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, style = AppTypography.labelLarge, color = color, fontSize = 12.sp)
        Text(text = label, style = AppTypography.labelSmall, color = TextDimmer, fontSize = 8.sp)
    }
}

@Composable
private fun PetStage.stageColor(): Color = when (this) {
    PetStage.EGG -> Gray
    PetStage.BABY -> Emerald
    PetStage.EXPLORER -> Blue
    PetStage.GUARDIAN -> Purple
    PetStage.LEGEND -> Orange
}
