package com.habitquest.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitquest.ui.screen.home.HomeScreen
import com.habitquest.ui.screen.profile.ProfileScreen
import com.habitquest.ui.screen.statistics.StatisticsScreen
import com.habitquest.ui.screen.tasks.TasksCalendarScreen
import com.habitquest.ui.theme.*

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
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenTasksCalendar = { navController.navigate(Screen.TasksCalendar.route) }
                )
            }
            composable(Screen.Progress.route) { StatisticsScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.TasksCalendar.route) { TasksCalendarScreen() }
        }

        // Bottom navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Background.copy(alpha = 0.95f))
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                                if (isSelected) Purple.copy(alpha = 0.14f) else Color.Transparent
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
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Amber else TextDimmer,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            style = AppTypography.labelSmall,
                            color = if (isSelected) Amber else TextDimmer,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
