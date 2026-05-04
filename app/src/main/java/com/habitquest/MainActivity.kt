package com.habitquest

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.data.preferences.OnboardingPreferences
import com.habitquest.ui.navigation.NavGraph
import com.habitquest.ui.screen.onboarding.OnboardingScreen
import com.habitquest.ui.theme.HabitQuestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var notificationPermissionGranted by mutableStateOf(false)
    private var exactAlarmPermissionGranted by mutableStateOf(true)
    private var isOnboardingLoading = true
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isOnboardingLoading }
        notificationPermissionGranted = hasNotificationPermission()
        exactAlarmPermissionGranted = canScheduleExactAlarms()
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

                SideEffect { isOnboardingLoading = hasSeenOnboarding == null }

                when (hasSeenOnboarding) {
                    true -> NavGraph(
                        notificationPermissionGranted = notificationPermissionGranted,
                        exactAlarmPermissionGranted = exactAlarmPermissionGranted,
                        onRequestNotificationPermission = ::requestNotificationPermissionIfNeeded,
                        onRequestExactAlarmPermission = ::openExactAlarmSettings
                    )
                    false -> OnboardingScreen(
                        onFinish = {
                            scope.launch {
                                onboardingPreferences.setHasSeenOnboarding(true)
                            }
                        }
                    )
                    null -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        notificationPermissionGranted = hasNotificationPermission()
        exactAlarmPermissionGranted = canScheduleExactAlarms()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        if (!hasNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationPermissionGranted = true
        }
    }

    private fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:$packageName")
        )
        runCatching { startActivity(intent) }
            .onFailure {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
            }
    }
}
