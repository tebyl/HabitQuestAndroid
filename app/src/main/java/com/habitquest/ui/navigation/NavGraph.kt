package com.habitquest.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.habitquest.ui.screen.progress.ProgressScreen
import com.habitquest.ui.theme.*

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: String
)

private val navItems = listOf(
    NavItem(Screen.Home, "Hoy", "🏠"),
    NavItem(Screen.Progress, "Stats", "📊"),
    NavItem(Screen.Profile, "Perfil", "🤖"),
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
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
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
                                if (isSelected) Color(0x1AF59E0B) else Color.Transparent
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
                        Text(
                            text = item.icon,
                            fontSize = 20.sp
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
