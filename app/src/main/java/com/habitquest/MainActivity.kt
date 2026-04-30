package com.habitquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.data.preferences.OnboardingPreferences
import com.habitquest.ui.navigation.NavGraph
import com.habitquest.ui.screen.onboarding.OnboardingScreen
import com.habitquest.ui.theme.HabitQuestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitQuestTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val onboardingPreferences = remember(context) {
                    OnboardingPreferences(context.applicationContext)
                }
                val hasSeenOnboarding by onboardingPreferences.hasSeenOnboarding
                    .collectAsStateWithLifecycle(initialValue = null)

                when (hasSeenOnboarding) {
                    true -> NavGraph()
                    false, null -> OnboardingScreen(
                        onFinish = {
                            scope.launch {
                                onboardingPreferences.setHasSeenOnboarding(true)
                            }
                        }
                    )
                }
            }
        }
    }
}
