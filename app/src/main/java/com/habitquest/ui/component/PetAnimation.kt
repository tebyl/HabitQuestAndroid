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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

private fun stageScaleFor(stage: PetStage): Float = when (stage) {
    PetStage.EGG -> 0.90f
    PetStage.BABY -> 1.00f
    PetStage.EXPLORER -> 1.05f
    PetStage.GUARDIAN -> 1.10f
    PetStage.LEGEND -> 1.15f
}

private fun haloColorFor(streak: Int): Color? = when {
    streak >= 14 -> Color(0xFFB8A1FF).copy(alpha = 0.34f)
    streak >= 7 -> Color(0xFFB8A1FF).copy(alpha = 0.24f)
    streak >= 3 -> Color(0xFFB8A1FF).copy(alpha = 0.14f)
    else -> null
}

private fun haloScaleFor(streak: Int): Float = when {
    streak >= 14 -> 1.18f
    streak >= 7 -> 1.08f
    else -> 0.96f
}

@Composable
fun PetAnimation(
    stage: PetStage,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val duration = animationDurationFor(streak)
    val infiniteTransition = rememberInfiniteTransition(label = "petNativeAnimation")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "petBreatheScale"
    )
    val animatedTranslationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "petFloatTranslation"
    )

    val finalScale = stageScaleFor(stage) * breatheScale
    val stageAlpha = if (stage == PetStage.EGG) 0.90f else 1.0f
    val haloColor = haloColorFor(streak)

    Box(
        modifier = modifier.defaultMinSize(minWidth = 140.dp, minHeight = 140.dp),
        contentAlignment = Alignment.Center
    ) {
        if (haloColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = haloScaleFor(streak)
                        scaleY = haloScaleFor(streak)
                    }
                    .background(
                        color = haloColor,
                        shape = CircleShape
                    )
            )
        }

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
