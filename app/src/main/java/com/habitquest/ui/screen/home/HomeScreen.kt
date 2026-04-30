package com.habitquest.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.habitquest.domain.gamification.resolvePetState
import com.habitquest.domain.model.Levels
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
    val currentStreak  = state.habits.maxOfOrNull { it.streakCount } ?: 0
    
    val petState = resolvePetState(
        totalXP = state.totalXP,
        level = state.currentLevel.level,
        streak = currentStreak,
        habitsCompleted = state.habits.sumOf { it.totalDays },
        tasksCompleted = state.tasks.count { it.isCompleted }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF5EDE6), Color(0xFFEFE3F5), Background)
                )
            )
    ) {
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Purple.copy(alpha = 0.13f))
                                .border(
                                    1.dp,
                                    Purple.copy(alpha = 0.24f),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "LVL ${state.currentLevel.level}",
                                style = AppTypography.labelSmall,
                                color = Purple,
                                fontSize = 10.sp
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Purple.copy(alpha = 0.42f), Orange.copy(alpha = 0.34f)))
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.74f), CircleShape)
                        ) {
                            Text(state.userAvatar, fontSize = 18.sp)
                        }
                    }
                }
            }

            // ── Progress card ─────────────────────────────────────
            item {
                val completedCount = state.habits.count { it.completedToday }
                val isComplete = completedCount == state.habits.size && state.habits.isNotEmpty()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFF7F1).copy(alpha = 0.92f),
                                    Purple.copy(alpha = 0.16f),
                                    Orange.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.68f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when {
                                isComplete     -> "Vas increible hoy \u2728"
                                completedCount == 0 -> "Un paso suave para empezar"
                                else           -> "Tu dia va tomando forma"
                            },
                            style = AppTypography.titleLarge,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Pequenos pasos, grandes cambios",
                            style = AppTypography.bodyMedium,
                            color = TextMuted
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HomeHeroMetric(
                            label = state.currentLevel.name,
                            value = "${state.currentLevel.level}",
                            color = Purple,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(144.dp)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.White.copy(alpha = 0.98f), Purple.copy(alpha = 0.18f))
                                    ),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.72f), CircleShape)
                        ) {
                            PetAnimation(
                                modifier = Modifier.size(136.dp),
                                animationRes = petAnimationResFor(petState.stage),
                                stage = petState.stage,
                                streak = petState.streak
                            )
                        }
                        HomeHeroMetric(
                            label = "dias",
                            value = "$currentStreak",
                            color = Orange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.58f))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        XPBar(
                            currentXP = state.xpInCurrentLevel,
                            maxXP     = state.xpToNext,
                            level     = state.currentLevel.level
                        )
                        Spacer(Modifier.height(6.dp))
                        val nextLevel = Levels.all
                            .find { it.level == state.currentLevel.level + 1 }
                        Text(
                            text = nextLevel?.let { "${(state.xpToNext - state.xpInCurrentLevel).coerceAtLeast(0)} XP para nivel ${it.level}" }
                                ?: "Nivel maximo alcanzado",
                            style = AppTypography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
            }

            // ── Week calendar ──────────────────────────────────────
            item {
                WeekCalendar(
                    completedDates = completedDates,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(22.dp))
            }

            // ── Daily mission card ─────────────────────────────────
            item {
                val completedCount = state.habits.count { it.completedToday }
                DailyMissionCard(
                    completedHabits = completedCount,
                    totalHabits     = state.habits.size,
                    modifier        = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(22.dp))
            }

            // Daily streak reward
            item {
                val bestStreak = state.habits.maxOfOrNull { it.streakCount } ?: 0
                StreakRewardCard(
                    currentStreakDays = currentStreak,
                    bestStreakDays = bestStreak,
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
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.74f))
                            .border(1.dp, Orange.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 14.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RACHAS EN RIESGO",
                                style = AppTypography.labelSmall,
                                color = Orange,
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
                    EmptyStateCard(
                        icon     = "🌱",
                        title    = "Sin hábitos aún",
                        subtitle = "Toca + para agregar tu primer hábito",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                items(state.habits, key = { it.id }) { habit ->
                    val xpGain = HabitRepositoryImpl.xpForCategory(habit.category) +
                        if (habit.streakCount >= HabitRepositoryImpl.STREAK_BONUS_THRESHOLD)
                            HabitRepositoryImpl.STREAK_BONUS else 0
                    var visible by remember(habit.id) { mutableStateOf(false) }
                    LaunchedEffect(habit.id) { visible = true }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(220)) + expandVertically(tween(220))
                    ) {
                        HabitCard(
                            habit        = habit,
                            xpGain       = xpGain,
                            onComplete   = viewModel::completeHabit,
                            onUncomplete = viewModel::uncompleteHabit,
                            modifier     = Modifier.padding(horizontal = 20.dp)
                        )
                    }
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
                    EmptyStateCard(
                        icon     = "📋",
                        title    = "Sin tareas aún",
                        subtitle = "Toca + para agregar tu primera tarea",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                items(pendingTasks, key = { "task_${it.id}" }) { task ->
                    var visible by remember(task.id) { mutableStateOf(false) }
                    LaunchedEffect(task.id) { visible = true }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(220)) + expandVertically(tween(220))
                    ) {
                        TaskCard(
                            task         = task,
                            onComplete   = viewModel::completeTask,
                            onUncomplete = viewModel::uncompleteTask,
                            modifier     = Modifier.padding(horizontal = 20.dp)
                        )
                    }
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
            containerColor = Purple,
            contentColor = Color.White
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
                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.96f), Amber.copy(alpha = 0.18f)))
                            else if (toast.message.startsWith("-"))
                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.96f), Orange.copy(alpha = 0.18f)))
                            else
                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.96f), Emerald.copy(alpha = 0.18f)))
                        )
                        .border(
                            1.dp,
                            if (toast.isLevelUp) Amber.copy(alpha = 0.34f)
                            else if (toast.message.startsWith("-")) Orange.copy(alpha = 0.34f)
                            else Emerald.copy(alpha = 0.34f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = toast.message,
                        style = AppTypography.labelLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }

    // Quick-add bottom sheet
    if (state.showQuickAdd) {
        HabitCreateSheet(
            onDismiss  = viewModel::hideQuickAdd,
            onAddHabit = viewModel::createHabit,
            onAddTask  = viewModel::createTask
        )
    }
}

@Composable
private fun HomeHeroMetric(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.58f))
            .border(1.dp, color.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTypography.labelLarge,
            color = color,
            fontSize = 11.sp
        )
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextDim,
            fontSize = 8.sp
        )
    }
}
