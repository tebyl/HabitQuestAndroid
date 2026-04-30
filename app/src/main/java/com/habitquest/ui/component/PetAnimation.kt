package com.habitquest.ui.component

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.habitquest.R
import com.habitquest.domain.gamification.PetStage

private const val BASE_LOTTIE_SCALE = 2.0f

fun petAnimationResFor(stage: PetStage): Int = when (stage) {
    PetStage.EGG      -> R.raw.pet_seed_idle
    PetStage.BABY     -> R.raw.pet_baby_idle
    PetStage.EXPLORER -> R.raw.pet_explorer_idle
    PetStage.GUARDIAN -> R.raw.pet_guardian_idle
    PetStage.LEGEND   -> R.raw.pet_essence_idle
}

fun petSpeedFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG      -> 0.8f
    PetStage.BABY     -> 0.95f
    PetStage.EXPLORER -> 1.0f
    PetStage.GUARDIAN -> 1.15f
    PetStage.LEGEND   -> 1.3f
}

fun petSizeMultiplierFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG      -> 0.85f
    PetStage.BABY     -> 0.92f
    PetStage.EXPLORER -> 1.0f
    PetStage.GUARDIAN -> 1.08f
    PetStage.LEGEND   -> 1.15f
}

enum class PetMood { IDLE, CALM, HAPPY }

fun petMoodFor(streak: Int): PetMood = when {
    streak >= 7 -> PetMood.HAPPY
    streak >= 3 -> PetMood.CALM
    else        -> PetMood.IDLE
}

fun speedByMood(baseSpeed: Float, mood: PetMood): Float = when (mood) {
    PetMood.IDLE  -> baseSpeed * 0.9f
    PetMood.CALM  -> baseSpeed
    PetMood.HAPPY -> baseSpeed * 1.1f
}

fun glowColorFor(mood: PetMood): Color? = when (mood) {
    PetMood.IDLE  -> null
    PetMood.CALM  -> Color(0xFFB8A1FF).copy(alpha = 0.3f)
    PetMood.HAPPY -> Color(0xFFB8A1FF).copy(alpha = 0.5f)
}

@Composable
fun PetAnimation(
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int = R.raw.pet_baby_idle,
    stage: PetStage = PetStage.BABY,
    streak: Int = 0
) {
    val isEgg       = stage == PetStage.EGG
    val mood        = petMoodFor(streak)
    val finalSpeed  = speedByMood(petSpeedFor(stage), mood)
        .let { if (isEgg) it.coerceAtLeast(0.9f) else it }
    val glowColor   = if (isEgg) Color(0xFFCBB8FF).copy(alpha = 0.3f) else glowColorFor(mood)
    val stageScale  = if (isEgg) 0.9f else petSizeMultiplierFor(stage)
    val totalScale  = BASE_LOTTIE_SCALE * stageScale
    val glowElevation: Dp = when {
        glowColor == null -> 0.dp
        isEgg             -> 4.dp
        else              -> 12.dp
    }

    val compositionResult = rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationRes)
    )
    val composition = compositionResult.value
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        speed       = finalSpeed,
        iterations  = LottieConstants.IterateForever
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 140.dp, minHeight = 140.dp)
            .shadow(glowElevation, CircleShape, clip = false)
    ) {
        if (composition == null) {
            // Shown only while the composition is loading; disappears once ready.
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color(0xFFCBB8FF).copy(alpha = 0.18f), CircleShape)
            )
            return@Box
        }

        LottieAnimation(
            composition  = composition,
            progress     = { animationState.progress },
            contentScale = ContentScale.FillBounds,
            modifier     = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = totalScale
                    scaleY = totalScale
                    alpha  = if (isEgg) 0.7f else 1f
                }
        )
    }
}
