package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.gamification.PetEmotion
import com.habitquest.domain.gamification.PetReactionPolicy
import com.habitquest.domain.gamification.PetReactionState
import com.habitquest.domain.gamification.PetStage
import com.habitquest.domain.gamification.PetState
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Orange
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextMuted
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary

@Composable
fun EvolutionPetCard(
    petState: PetState,
    reactionState: PetReactionState = PetReactionState.IDLE,
    modifier: Modifier = Modifier
) {
    val stageColor = petState.stage.stageColor()
    val glowAlpha by remember(petState.stage, petState.emotion, reactionState) {
        derivedStateOf {
            maxOf(
                petState.stage.stageGlowAlpha(),
                petState.emotion.glowAlpha(),
                reactionState.glowAlpha()
            )
        }
    }
    val reactionScale by animateFloatAsState(
        targetValue = reactionState.scaleTarget(),
        animationSpec = tween(260),
        label = "petReactionScale"
    )
    val progress by animateFloatAsState(
        targetValue = petState.progressToNext,
        animationSpec = tween(800),
        label = "petEvolutionProgress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tu companera de viaje",
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
                    .graphicsLayer {
                        shadowElevation = 10f + (glowAlpha * 18f)
                        shape = CircleShape
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                PetAnimation(
                    stage = petState.stage,
                    streak = petState.streak,
                    emotion = petState.emotion,
                    reactionState = reactionState,
                    modifier = Modifier.size(160.dp)
                        .graphicsLayer {
                            scaleX = reactionScale
                            scaleY = reactionScale
                        }
                )
                StageUnlockOverlay(
                    stage = petState.stage,
                    color = stageColor,
                    modifier = Modifier.align(Alignment.BottomEnd)
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = petState.stage.stageTitle(),
                    style = AppTypography.headlineMedium,
                    color = TextPrimary
                )

                Text(
                    text = PetReactionPolicy.messageFor(reactionState)
                        ?: petState.stage.stageNarrative(),
                    style = AppTypography.bodyMedium,
                    color = if (reactionState == PetReactionState.IDLE) TextSecondary else stageColor
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
                    text = petState.nextStage?.let { "Proxima evolucion: ${it.label}" }
                        ?: "Evolucion completa",
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

            Text(
                text = petState.nextStageRequirementText,
                style = AppTypography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        StageUnlocks(
            currentStage = petState.stage,
            stageColor = stageColor
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PetMetric(
                label = "Racha",
                value = "${petState.streak}d",
                color = Purple,
                modifier = Modifier.weight(1f)
            )
            PetMetric(
                label = "Nivel",
                value = petState.level.toString(),
                color = Orange,
                modifier = Modifier.weight(1f)
            )
            PetMetric(
                label = "Tareas",
                value = petState.tasksCompleted.toString(),
                color = Emerald,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StageUnlockOverlay(
    stage: PetStage,
    color: Color,
    modifier: Modifier = Modifier
) {
    when (stage) {
        PetStage.EGG -> Box(
            modifier = modifier
                .size(width = 72.dp, height = 16.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        PetStage.BABY,
        PetStage.EXPLORER,
        PetStage.GUARDIAN,
        PetStage.LEGEND -> Box(
            modifier = modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = stage.unlockIcon(),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StageUnlocks(
    currentStage: PetStage,
    stageColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Desbloqueado en esta etapa",
            style = AppTypography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PetStage.entries.forEach { stage ->
                val unlocked = stage.ordinal <= currentStage.ordinal
                val current = stage == currentStage
                UnlockChip(
                    stage = stage,
                    unlocked = unlocked,
                    current = current,
                    color = if (current) stageColor else stage.stageColor(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun UnlockChip(
    stage: PetStage,
    unlocked: Boolean,
    current: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    val alpha = if (unlocked) 1f else 0.42f
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (current) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(vertical = 7.dp, horizontal = 4.dp)
            .graphicsLayer { this.alpha = alpha },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (unlocked) stage.unlockIcon() else Icons.Rounded.Lock,
            contentDescription = null,
            tint = if (unlocked) color else TextDimmer,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stage.unlockShortLabel(),
            style = AppTypography.labelSmall,
            color = if (unlocked) TextSecondary else TextDimmer,
            fontSize = 8.sp
        )
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
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
    PetStage.EGG -> MaterialTheme.colorScheme.tertiary
    PetStage.BABY -> MaterialTheme.colorScheme.secondary
    PetStage.EXPLORER -> MaterialTheme.colorScheme.primary
    PetStage.GUARDIAN -> MaterialTheme.colorScheme.primary
    PetStage.LEGEND -> MaterialTheme.colorScheme.tertiary
}

private fun PetEmotion.glowAlpha(): Float = when (this) {
    PetEmotion.IDLE -> 0.02f
    PetEmotion.HAPPY -> 0.10f
    PetEmotion.PROUD -> 0.16f
    PetEmotion.TIRED -> 0.0f
}

private fun PetReactionState.glowAlpha(): Float = when (this) {
    PetReactionState.IDLE -> 0.0f
    PetReactionState.HAPPY -> 0.10f
    PetReactionState.PROUD -> 0.18f
    PetReactionState.STREAK -> 0.16f
    PetReactionState.LEVEL_UP -> 0.22f
}

private fun PetReactionState.scaleTarget(): Float = when (this) {
    PetReactionState.IDLE -> 1.0f
    PetReactionState.HAPPY -> 1.05f
    PetReactionState.PROUD -> 1.08f
    PetReactionState.STREAK -> 1.03f
    PetReactionState.LEVEL_UP -> 1.12f
}

private fun PetStage.stageTitle(): String = when (this) {
    PetStage.EGG -> "Egg"
    PetStage.BABY -> "Baby"
    PetStage.EXPLORER -> "Explorer"
    PetStage.GUARDIAN -> "Guardian"
    PetStage.LEGEND -> "Legend"
}

private fun PetStage.stageNarrative(): String = when (this) {
    PetStage.EGG -> "Tu companero esta despertando."
    PetStage.BABY -> "Esta aprendiendo contigo."
    PetStage.EXPLORER -> "Ya explora nuevas rutinas."
    PetStage.GUARDIAN -> "Protege tu constancia diaria."
    PetStage.LEGEND -> "Tu disciplina ya es legendaria."
}

private fun PetStage.stageGlowAlpha(): Float = when (this) {
    PetStage.EGG -> 0.03f
    PetStage.BABY -> 0.07f
    PetStage.EXPLORER -> 0.10f
    PetStage.GUARDIAN -> 0.14f
    PetStage.LEGEND -> 0.20f
}

private fun PetStage.unlockIcon() = when (this) {
    PetStage.EGG -> Icons.Rounded.CheckCircle
    PetStage.BABY -> Icons.Rounded.Favorite
    PetStage.EXPLORER -> Icons.Rounded.Explore
    PetStage.GUARDIAN -> Icons.Rounded.Shield
    PetStage.LEGEND -> Icons.Rounded.Stars
}

private fun PetStage.unlockShortLabel(): String = when (this) {
    PetStage.EGG -> "Base"
    PetStage.BABY -> "Acc."
    PetStage.EXPLORER -> "Kit"
    PetStage.GUARDIAN -> "Aura"
    PetStage.LEGEND -> "Premium"
}
