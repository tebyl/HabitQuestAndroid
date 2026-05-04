package com.habitquest.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.ui.component.PremiumSurfaceCard
import com.habitquest.ui.component.StatSummaryCard
import com.habitquest.ui.theme.AppTypography

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentStreak = state.streaks.firstOrNull()?.streak ?: 0
    val hasAnyData = state.totalXP > 0 || state.totalHabitsCompleted > 0 || state.totalTasksCompleted > 0 || state.categoryBreakdown.isNotEmpty() || state.activity14Days.any { it.count > 0 }
    val completedTotal = state.totalHabitsCompleted + state.totalTasksCompleted
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PremiumSurfaceCard(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 22.dp),
                radius = 28.dp,
                accentColor = MaterialTheme.colorScheme.primary,
                glow = true
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "TU PROGRESO",
                    style = AppTypography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Estadisticas",
                    style = AppTypography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
                }
            }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatSummaryCard(
                        icon = "🔥",
                        value = currentStreak.toString(),
                        unit = "d",
                        label = "Racha actual",
                        tintColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        icon = "⚡",
                        value = state.totalXP.toString(),
                        unit = "XP",
                        label = "XP total",
                        tintColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatSummaryCard(
                        icon = "🏁",
                        value = state.maxStreak.toString(),
                        unit = "d",
                        label = "Mejor racha",
                        tintColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        icon = "✅",
                        value = completedTotal.toString(),
                        label = "Completados",
                        tintColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (!hasAnyData) {
            item {
                EmptyStatsState(
                    message = "Completa hábitos o tareas para construir tu progreso",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            WeeklyProgressCard(
                weeklyHabits = state.weeklyHabits,
                weeklyTasks = state.weeklyTasks,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            CategoryBreakdownCard(
                categoryBreakdown = state.categoryBreakdown,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            ActivityCalendarCard(
                activity14Days = state.activity14Days,
                totalXP = state.totalXP,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(Modifier.height(6.dp))
        }
    }
}
