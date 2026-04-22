package com.habitquest.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.ui.component.*
import com.habitquest.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val today   = LocalDate.now()
    val dateStr = today.format(
        DateTimeFormatter.ofPattern("EEEE, d MMM", Locale("es"))
    ).replaceFirstChar { it.uppercase() }

    // All lastCompletedDates from every habit (not just today's)
    val completedDates = state.habits
        .map { it.lastCompletedDate }
        .filter { it.isNotEmpty() }
        .toSet()

    val pendingTasks   = state.tasks.filter { !it.isCompleted }
    val completedTasks = state.tasks.filter { it.isCompleted }
    val habitsAtRisk   = state.habits.filter { it.streakCount > 0 && !it.completedToday }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Header ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dateStr.uppercase(),
                            style = AppTypography.labelSmall,
                            color = TextDim,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Hoy",
                            style = AppTypography.headlineLarge,
                            color = TextPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1AF59E0B))
                            .border(1.dp, Color(0x4DF59E0B), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("⚡", fontSize = 14.sp)
                        Text(
                            text = "${state.totalXP}",
                            style = AppTypography.labelLarge,
                            color = Amber
                        )
                    }
                }
            }

            // ── Progress card ─────────────────────────────────────
            item {
                val completedCount = state.habits.count { it.completedToday }
                val isComplete = completedCount == state.habits.size && state.habits.isNotEmpty()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(CardBackground, CardBackground2)))
                        .border(1.dp, DividerLight, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CircularProgressRing(
                        completed = completedCount,
                        total = state.habits.size.coerceAtLeast(1)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                isComplete     -> "¡Día perfecto! 🎉"
                                completedCount == 0 -> "¡Empieza hoy!"
                                else           -> "En progreso..."
                            },
                            style = AppTypography.titleLarge,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        XPBar(
                            currentXP = state.xpInCurrentLevel,
                            maxXP     = state.xpToNext,
                            level     = state.currentLevel.level
                        )
                        Spacer(Modifier.height(6.dp))
                        val nextLevel = com.habitquest.domain.model.Levels.all
                            .find { it.level == state.currentLevel.level + 1 }
                        Text(
                            text = "${state.currentLevel.name}${nextLevel?.let { " → ${it.name}" } ?: ""}",
                            style = AppTypography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Week calendar ──────────────────────────────────────
            item {
                WeekCalendar(
                    completedDates = completedDates,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Streak at-risk alert ───────────────────────────────
            if (habitsAtRisk.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Red.copy(alpha = 0.08f))
                            .border(1.dp, Red.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 14.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RACHAS EN RIESGO",
                                style = AppTypography.labelSmall,
                                color = Red,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            )
                            Text(
                                text = habitsAtRisk.joinToString("  ") {
                                    "${it.icon} 🔥${it.streakCount}"
                                },
                                style = AppTypography.labelSmall,
                                color = TextDim,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Hábitos section ────────────────────────────────────
            item {
                Text(
                    text = "HÁBITOS",
                    style = AppTypography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))
            }

            if (state.habits.isEmpty()) {
                item {
                    Text(
                        text = "Sin hábitos · toca + para agregar",
                        style = AppTypography.bodyMedium,
                        color = TextDim,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                items(state.habits, key = { it.id }) { habit ->
                    val xpGain = HabitRepositoryImpl.xpForCategory(habit.category) +
                        if (habit.streakCount >= HabitRepositoryImpl.STREAK_BONUS_THRESHOLD)
                            HabitRepositoryImpl.STREAK_BONUS else 0
                    HabitCard(
                        habit        = habit,
                        xpGain       = xpGain,
                        onComplete   = viewModel::completeHabit,
                        onUncomplete = viewModel::uncompleteHabit,
                        modifier     = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Tareas section ─────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAREAS",
                        style = AppTypography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 1.5.sp
                    )
                    if (state.tasks.isNotEmpty()) {
                        Text(
                            text = "${completedTasks.size}/${state.tasks.size}",
                            style = AppTypography.labelSmall,
                            color = TextDim,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            if (state.tasks.isEmpty()) {
                item {
                    Text(
                        text = "Sin tareas · toca + para agregar",
                        style = AppTypography.bodyMedium,
                        color = TextDim,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                items(pendingTasks, key = { "task_${it.id}" }) { task ->
                    TaskCard(
                        task         = task,
                        onComplete   = viewModel::completeTask,
                        onUncomplete = viewModel::uncompleteTask,
                        modifier     = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(completedTasks, key = { "done_${it.id}" }) { task ->
                    TaskCard(
                        task         = task,
                        onComplete   = viewModel::completeTask,
                        onUncomplete = viewModel::uncompleteTask,
                        modifier     = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = viewModel::showQuickAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 88.dp),
            shape = CircleShape,
            containerColor = Amber,
            contentColor = Background
        ) {
            Text("+", fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
        }

        // Toast
        AnimatedVisibility(
            visible = state.toast != null,
            enter = slideInVertically(tween(300)) { it },
            exit  = slideOutVertically(tween(300)) { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 88.dp)
        ) {
            state.toast?.let { toast ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (toast.isLevelUp)
                                Brush.linearGradient(listOf(Color(0xFF92400E), Color(0xFFB45309)))
                            else if (toast.message.startsWith("-"))
                                Brush.linearGradient(listOf(Color(0xFF7F1D1D), Color(0xFF991B1B)))
                            else
                                Brush.linearGradient(listOf(EmeraldDark, Color(0xFF65A30D)))
                        )
                        .border(
                            1.dp,
                            if (toast.isLevelUp) Amber
                            else if (toast.message.startsWith("-")) Red
                            else Emerald,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = toast.message,
                        style = AppTypography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Quick-add bottom sheet
    if (state.showQuickAdd) {
        QuickAddSheet(
            onDismiss  = viewModel::hideQuickAdd,
            onAddHabit = viewModel::createHabit,
            onAddTask  = viewModel::createTask
        )
    }
}
