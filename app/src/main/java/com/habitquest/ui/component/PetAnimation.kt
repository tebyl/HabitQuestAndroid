package com.habitquest.ui.component

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.habitquest.R
import com.habitquest.domain.gamification.PetStage

fun petAnimationResFor(stage: PetStage): Int = when (stage) {
    PetStage.EGG -> R.raw.pet_seed_idle
    PetStage.BABY -> R.raw.pet_baby_idle
    PetStage.EXPLORER -> R.raw.pet_explorer_idle
    PetStage.GUARDIAN -> R.raw.pet_guardian_idle
    PetStage.LEGEND -> R.raw.pet_essence_idle
}

fun petSpeedFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 0.8f
    PetStage.BABY -> 0.95f
    PetStage.EXPLORER -> 1.0f
    PetStage.GUARDIAN -> 1.15f
    PetStage.LEGEND -> 1.3f
}

fun petSizeMultiplierFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 0.85f
    PetStage.BABY -> 0.92f
    PetStage.EXPLORER -> 1.0f
    PetStage.GUARDIAN -> 1.08f
    PetStage.LEGEND -> 1.15f
}

enum class PetMood {
    IDLE, CALM, HAPPY
}

fun petMoodFor(streak: Int): PetMood = when {
    streak >= 7 -> PetMood.HAPPY
    streak >= 3 -> PetMood.CALM
    else -> PetMood.IDLE
}

fun speedByMood(baseSpeed: Float, mood: PetMood): Float = when (mood) {
    PetMood.IDLE -> baseSpeed * 0.9f
    PetMood.CALM -> baseSpeed
    PetMood.HAPPY -> baseSpeed * 1.1f
}

fun glowColorFor(mood: PetMood): Color? = when (mood) {
    PetMood.IDLE -> null
    PetMood.CALM -> Color(0xFFB8A1FF).copy(alpha = 0.3f)
    PetMood.HAPPY -> Color(0xFFB8A1FF).copy(alpha = 0.5f)
}

@Composable
fun PetAnimation(
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int = R.raw.pet_baby_idle,
    stage: PetStage = PetStage.BABY,
    streak: Int = 0
) {
    val isEgg = stage == PetStage.EGG
    val mood = petMoodFor(streak)
    val baseSpeed = petSpeedFor(stage)
    val rawSpeed = speedByMood(baseSpeed, mood)
    val finalSpeed = if (isEgg) rawSpeed.coerceAtLeast(0.9f) else rawSpeed
    val glowColor = if (isEgg) Color(0xFFCBB8FF).copy(alpha = 0.3f) else glowColorFor(mood)

    val stageScale = if (isEgg) 0.9f else petSizeMultiplierFor(stage)
    val totalScale = 2.0f * stageScale

    // Temporarily forced to pet_baby_idle to isolate resource vs layout issues.
    // Remove the override once the render problem is confirmed resolved.
    val debugRes = R.raw.pet_baby_idle

    val compositionResult = rememberLottieComposition(
        LottieCompositionSpec.RawRes(debugRes)
    )
    val composition = compositionResult.value
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        speed = finalSpeed,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(composition, compositionResult.isFailure) {
        if (compositionResult.isFailure) {
            Log.e("PetAnimation", "LOAD FAILED stage=$stage res=$debugRes (isFailure=true)")
        } else if (composition != null) {
            Log.d("PetAnimation", "LOADED stage=$stage res=$debugRes duration=${composition.duration}ms endFrame=${composition.endFrame}")
        } else {
            Log.d("PetAnimation", "LOADING stage=$stage res=$debugRes")
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.defaultMinSize(minWidth = 140.dp, minHeight = 140.dp)
    ) {
        if (composition == null) {
            Log.d("PetAnimation", "FALLBACK shown (composition null) stage=$stage")
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color(0xFFCBB8FF).copy(alpha = 0.18f), CircleShape)
            )
            return@Box
        }

        // NOTE: shadow(clip=true) with CircleShape clips the LottieAnimation content
        // BEFORE graphicsLayer applies scale, causing the scaled render to be invisible.
        // Replaced with a graphicsLayer-only glow approximation (no clip side-effect).
        val finalMod = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX        = totalScale,
                scaleY        = totalScale,
                alpha         = if (isEgg) 0.7f else 1f,
                shadowElevation = if (glowColor != null) {
                    if (isEgg) 4f else 12f
                } else 0f,
                shape         = CircleShape,
                clip          = false        // no clip — let the parent circle handle it
            )

        LottieAnimation(
            composition   = composition,
            progress      = { animationState.progress },
            contentScale  = ContentScale.FillBounds,
            modifier      = finalMod
        )
    }
}
