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
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.82f),
                        Purple.copy(alpha = 0.14f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.72f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tu compañera de viaje",
            style = AppTypography.titleMedium,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                Color.White,
                                stageColor.copy(alpha = 0.20f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.82f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                PetAnimation(
                    modifier = Modifier.size(128.dp),
                    animationRes = petAnimationResFor(petState.stage),
                    stage = petState.stage,
                    streak = petState.streak
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ETAPA ${petState.stage.ordinal + 1} DE ${PetStage.entries.size}",
                        style = AppTypography.labelSmall,
                        color = stageColor,
                        letterSpacing = 1.sp,
                        fontSize = 9.sp
                    )

                    if (petState.streakBonusActive) {
                        Text(
                            text = "RITMO +",
                            style = AppTypography.labelSmall,
                            color = Amber,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Amber.copy(alpha = 0.12f))
                                .border(
                                    width = 1.dp,
                                    color = Amber.copy(alpha = 0.28f),
                                    shape = RoundedCornerShape(8.dp)
                                )
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
                    text = petState.nextStage?.let { "Próxima evolución: ${it.label}" }
                        ?: "Evolución completa",
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
                        .background(
                            Brush.horizontalGradient(
                                listOf(stageColor, Amber)
                            )
                        )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PetMetric(
                label = "XP total",
                value = "${petState.totalXP}",
                color = Purple,
                modifier = Modifier.weight(1f)
            )
            PetMetric(
                label = "Mejor ritmo",
                value = "${petState.streak}d",
                color = Orange,
                modifier = Modifier.weight(1f)
            )
            PetMetric(
                label = "Hábitos",
                value = petState.habitsCompleted.toString(),
                color = Emerald,
                modifier = Modifier.weight(1f)
            )
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
            .border(
                width = 1.dp,
                color = DividerLight,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTypography.labelLarge,
            color = color,
            fontSize = 12.sp
        )
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextDimmer,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun PetStage.stageColor(): Color = when (this) {
    PetStage.EGG      -> Color(0xFFCBB8FF)
    PetStage.BABY     -> Emerald
    PetStage.EXPLORER -> Blue
    PetStage.GUARDIAN -> Purple
    PetStage.LEGEND   -> Orange
}
