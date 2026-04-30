package com.habitquest.ui.screen.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.habitquest.R

sealed interface OnboardingVisual {
    data class Pet(val stage: com.habitquest.domain.gamification.PetStage, val streak: Int) : OnboardingVisual
    data class Image(@DrawableRes val resId: Int) : OnboardingVisual
    data class IconCluster(val icons: List<ImageVector>) : OnboardingVisual
}

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val visual: OnboardingVisual
)

fun onboardingPages(
    calendarIcon: ImageVector,
    achievementIcon: ImageVector,
    chartIcon: ImageVector
): List<OnboardingPage> = listOf(
    OnboardingPage(
        title = "Crea pequeños rituales",
        subtitle = "Organiza hábitos y tareas sin sentirlo como una lista pesada.",
        visual = OnboardingVisual.Image(R.drawable.pet_seed)
    ),
    OnboardingPage(
        title = "Tu compañera crece contigo",
        subtitle = "La mascota evoluciona con tu constancia, XP y rachas.",
        visual = OnboardingVisual.Pet(
            stage = com.habitquest.domain.gamification.PetStage.BABY,
            streak = 7
        )
    ),
    OnboardingPage(
        title = "Celebra tu progreso",
        subtitle = "Revisa estadísticas, logros y calendario de tareas completadas.",
        visual = OnboardingVisual.IconCluster(
            icons = listOf(calendarIcon, achievementIcon, chartIcon)
        )
    )
)
