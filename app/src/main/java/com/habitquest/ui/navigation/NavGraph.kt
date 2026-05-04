package com.habitquest.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.audio.AmbientSoundPlayer
import com.habitquest.data.preferences.AmbientSoundPreferences
import com.habitquest.ui.screen.home.HomeScreen
import com.habitquest.ui.screen.profile.ProfileScreen
import com.habitquest.ui.screen.statistics.StatisticsScreen
import com.habitquest.ui.screen.tasks.TasksCalendarScreen
import com.habitquest.ui.theme.*
import androidx.compose.ui.platform.LocalContext

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val navItems = listOf(
    NavItem(Screen.Home, "Hoy", Icons.Filled.Home),
    NavItem(Screen.Progress, "Stats", Icons.Filled.BarChart),
    NavItem(Screen.Profile, "Perfil", Icons.Filled.Person),
)

@Composable
fun NavGraph(
    notificationPermissionGranted: Boolean = true,
    exactAlarmPermissionGranted: Boolean = true,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestExactAlarmPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val ambientSoundPreferences = remember(context) {
        AmbientSoundPreferences(context.applicationContext)
    }
    val ambientSoundEnabled by ambientSoundPreferences.ambientSoundEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = navItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AmbientSoundPlayer(enabled = ambientSoundEnabled)

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenTasksCalendar = { navController.navigate(Screen.TasksCalendar.route) },
                    notificationPermissionGranted = notificationPermissionGranted,
                    exactAlarmPermissionGranted = exactAlarmPermissionGranted,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onRequestExactAlarmPermission = onRequestExactAlarmPermission
                )
            }
            composable(Screen.Progress.route) { StatisticsScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.TasksCalendar.route) {
                TasksCalendarScreen(onNavigateUp = { navController.navigateUp() })
            }
        }

        AnimatedVisibility(
            visible = showBottomBar,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val colors = MaterialTheme.colorScheme
            val isDark = colors.background.luminance() < 0.5f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .shadow(
                            elevation = if (isDark) 18.dp else 10.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = colors.primary,
                            spotColor = colors.primary
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(colors.surface)
                        .border(
                            width = 1.dp,
                            color = if (isDark) colors.primary else colors.outlineVariant,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) colors.surfaceVariant else Color.Transparent
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) colors.primary else colors.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                style = AppTypography.labelSmall,
                                color = if (isSelected) colors.primary else colors.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
