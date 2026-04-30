package com.habitquest.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.ui.component.*
import com.habitquest.ui.screen.home.categoryColor as appCategoryColor
import com.habitquest.ui.screen.home.categoryLabel as appCategoryLabel
import com.habitquest.ui.theme.*

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF5EDE6), Color(0xFFEFE3F5), Background)
                )
            ),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ───────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "TU PROGRESO",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Estadísticas",
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
                )
            }
        }

        // ── Summary cards ─────────────────────────────────────────
        item {
            val completionPct = (state.completionRateToday * 100).toInt()
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatSummaryCard(
                        icon       = "⚡",
                        value      = "${state.totalXP} XP",
                        label      = "Total ganado",
                        tintColor  = Amber,
                        modifier   = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        icon       = "🔥",
                        value      = "${state.maxStreak}d",
                        label      = "Racha máxima",
                        tintColor  = Red,
                        modifier   = Modifier.weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatSummaryCard(
                        icon       = "✅",
                        value      = "$completionPct%",
                        label      = "Completado hoy",
                        tintColor  = Emerald,
                        modifier   = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        icon       = "📋",
                        value      = "${state.totalTasksCompleted}",
                        label      = "Tareas hechas",
                        tintColor  = Blue,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Weekly habits bar chart ───────────────────────────────
        item {
            SectionCard(title = "HÁBITOS ESTA SEMANA") {
                if (state.weeklyHabits.isEmpty()) {
                    Text(
                        text = "Sin datos aún",
                        style = AppTypography.bodyMedium,
                        color = TextDimmer
                    )
                } else {
                    WeeklyBarChart(
                        data     = state.weeklyHabits.map { it.date to it.count },
                        barColor = Amber
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // ── Weekly tasks bar chart ────────────────────────────────
        item {
            SectionCard(title = "TAREAS CREADAS") {
                if (state.weeklyTasks.isEmpty()) {
                    Text(
                        text = "Sin datos aún",
                        style = AppTypography.bodyMedium,
                        color = TextDimmer
                    )
                } else {
                    WeeklyBarChart(
                        data     = state.weeklyTasks.map { it.date to it.count },
                        barColor = Blue
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // ── Category pie chart ────────────────────────────────────
        item {
            val segments = state.categoryBreakdown.entries.map { (cat, count) ->
                PieSegment(
                    label = categoryLabel(cat),
                    value = count,
                    color = categoryColor(cat)
                )
            }
            if (segments.isNotEmpty()) {
                SectionCard(title = "DISTRIBUCIÓN POR CATEGORÍA") {
                    CategoryPieChart(
                        segments   = segments,
                        centerText = "${state.totalHabits}"
                    )
                }
                Spacer(Modifier.height(14.dp))
            }
        }

        // ── Active streaks list ───────────────────────────────────
        item {
            if (state.streaks.isNotEmpty()) {
                SectionCard(title = "RACHAS ACTIVAS") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.streaks.take(5).forEach { streak ->
                            StreakRow(streak = streak)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.74f))
            .border(1.dp, Color.White.copy(alpha = 0.62f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = AppTypography.labelSmall,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
private fun StreakRow(streak: HabitStreak) {
    val color = categoryColor(streak.category)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = streak.icon, fontSize = 20.sp)
        Text(
            text = streak.name,
            style = AppTypography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🔥", fontSize = 12.sp)
            Text(
                text = "${streak.streak}d",
                style = AppTypography.labelLarge,
                color = color,
                fontSize = 12.sp
            )
        }
    }
}

private fun categoryLabel(category: String): String = when (category) {
    "salud_mental"  -> "Mente"
    "salud_fisica"  -> "Física"
    "desarrollo"    -> "Desarrollo"
    "productividad" -> "Productividad"
    "vida_diaria"   -> "Diario"
    "gamificacion"  -> "Juego"
    else            -> appCategoryLabel(category)
}

private fun categoryColor(category: String) = when (category) {
    "salud_mental"  -> Purple
    "salud_fisica"  -> Emerald
    "desarrollo"    -> Blue
    "productividad" -> Amber
    "vida_diaria"   -> Orange
    "gamificacion"  -> Rose
    else            -> appCategoryColor(category)
}
