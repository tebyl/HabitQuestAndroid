package com.habitquest.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.habitquest.domain.gamification.PetEmotion
import com.habitquest.domain.gamification.PetReactionState
import com.habitquest.R
import com.habitquest.domain.gamification.PetStage

@DrawableRes
fun petImageResFor(stage: PetStage): Int = when (stage) {
    PetStage.EGG -> R.drawable.pet_seed
    PetStage.BABY -> R.drawable.pet_baby
    PetStage.EXPLORER -> R.drawable.pet_explorer
    PetStage.GUARDIAN -> R.drawable.pet_guardian
    PetStage.LEGEND -> R.drawable.pet_essence
}

private fun animationDurationFor(streak: Int): Int = when {
    streak >= 14 -> 1600
    streak >= 7 -> 1800
    streak >= 3 -> 2100
    else -> 2400
}

private fun stageDurationMultiplier(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 1.28f
    PetStage.BABY -> 0.92f
    PetStage.EXPLORER -> 0.74f
    PetStage.GUARDIAN -> 1.08f
    PetStage.LEGEND -> 0.82f
}

private fun stageScaleFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 0.90f
    PetStage.BABY -> 1.00f
    PetStage.EXPLORER -> 1.05f
    PetStage.GUARDIAN -> 1.10f
    PetStage.LEGEND -> 1.15f
}

private fun stageFloatDistanceFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> -2.5f
    PetStage.BABY -> -5.0f
    PetStage.EXPLORER -> -8.0f
    PetStage.GUARDIAN -> -3.0f
    PetStage.LEGEND -> -7.0f
}

private fun stageBreatheTargetFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 1.018f
    PetStage.BABY -> 1.045f
    PetStage.EXPLORER -> 1.055f
    PetStage.GUARDIAN -> 1.020f
    PetStage.LEGEND -> 1.060f
}

private fun haloScaleFor(stage: PetStage, streak: Int): Float = when {
    stage == PetStage.LEGEND -> 1.24f
    stage == PetStage.GUARDIAN -> 1.14f
    streak >= 14 -> 1.18f
    streak >= 7 -> 1.08f
    else -> 0.96f
}

@Composable
fun PetAnimation(
    stage: PetStage,
    streak: Int,
    emotion: PetEmotion = PetEmotion.IDLE,
    reactionState: PetReactionState = PetReactionState.IDLE,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val duration = when (emotion) {
        PetEmotion.HAPPY -> (animationDurationFor(streak) * 0.78f).toInt()
        PetEmotion.PROUD -> (animationDurationFor(streak) * 0.88f).toInt()
        PetEmotion.TIRED -> (animationDurationFor(streak) * 1.25f).toInt()
        PetEmotion.IDLE -> animationDurationFor(streak)
    }.let { base ->
        (base * stageDurationMultiplier(stage)).toInt()
    }.let { base ->
        when (reactionState) {
            PetReactionState.LEVEL_UP -> (base * 0.62f).toInt()
            PetReactionState.HAPPY -> (base * 0.72f).toInt()
            PetReactionState.PROUD -> (base * 0.78f).toInt()
            PetReactionState.STREAK -> (base * 0.86f).toInt()
            PetReactionState.IDLE -> base
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "petNativeAnimation")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = stageBreatheTargetFor(stage),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "petBreatheScale"
    )
    val animatedTranslationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = stageFloatDistanceFor(stage),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "petFloatTranslation"
    )

    val emotionScale = when (emotion) {
        PetEmotion.HAPPY -> 1.04f
        PetEmotion.PROUD -> 1.07f
        PetEmotion.TIRED -> 0.96f
        PetEmotion.IDLE -> 1.0f
    }
    val finalScale = stageScaleFor(stage) * emotionScale * breatheScale
    val stageAlpha = when {
        emotion == PetEmotion.TIRED -> 0.78f
        stage == PetStage.EGG -> 0.90f
        else -> 1.0f
    }
    val haloColor = when (emotion) {
        PetEmotion.PROUD -> colorScheme.tertiaryContainer
        PetEmotion.HAPPY -> colorScheme.secondaryContainer
        PetEmotion.TIRED -> colorScheme.surfaceVariant
        PetEmotion.IDLE -> when {
            stage == PetStage.LEGEND -> colorScheme.tertiaryContainer
            stage == PetStage.GUARDIAN -> colorScheme.primaryContainer
            streak >= 3 -> colorScheme.primaryContainer
            else -> colorScheme.surfaceVariant
        }
    }.let { emotionHalo ->
        when (reactionState) {
            PetReactionState.LEVEL_UP -> colorScheme.tertiaryContainer
            PetReactionState.PROUD -> colorScheme.tertiaryContainer
            PetReactionState.STREAK -> colorScheme.primaryContainer
            PetReactionState.HAPPY -> colorScheme.secondaryContainer
            PetReactionState.IDLE -> emotionHalo
        }
    }

    Box(
        modifier = modifier.defaultMinSize(minWidth = 140.dp, minHeight = 140.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = haloScaleFor(stage, streak)
                    scaleY = haloScaleFor(stage, streak)
                }
                .background(
                    color = haloColor,
                    shape = CircleShape
                )
            )

        Image(
            painter = painterResource(petImageResFor(stage)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = finalScale
                    scaleY = finalScale
                    translationY = animatedTranslationY
                    alpha = stageAlpha
                }
        )
    }
}
