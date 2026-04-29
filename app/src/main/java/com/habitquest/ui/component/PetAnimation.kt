package com.habitquest.ui.component

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.habitquest.R
import com.habitquest.domain.gamification.PetStage

/**
 * Mapea una etapa de mascota a su recurso de animación Lottie.
 * Si el recurso no existe, usa pet_baby_idle como fallback.
 */
fun petAnimationResFor(stage: PetStage): Int = when (stage) {
    PetStage.EGG -> R.raw.pet_seed_idle
    PetStage.BABY -> R.raw.pet_baby_idle
    PetStage.EXPLORER -> R.raw.pet_explorer_idle
    PetStage.GUARDIAN -> R.raw.pet_guardian_idle
    PetStage.LEGEND -> R.raw.pet_essence_idle
}

@Composable
fun PetAnimation(
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int = R.raw.pet_baby_idle
) {
    val compositionResult = rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationRes)
    )
    val composition = compositionResult.value
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { animationState.progress },
        modifier = modifier
    )
}
