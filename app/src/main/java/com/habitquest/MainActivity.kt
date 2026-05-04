package com.habitquest

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        requestNotificationPermissionIfNeeded()
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

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_NOTIFICATIONS
            )
        }
    }

    private companion object {
        const val REQUEST_POST_NOTIFICATIONS = 1001
    }
}
