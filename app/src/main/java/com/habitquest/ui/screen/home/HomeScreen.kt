package com.habitquest.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.domain.gamification.resolvePetState
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Levels
import com.habitquest.domain.model.Task
import com.habitquest.ui.component.*
import com.habitquest.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenTasksCalendar: () -> Unit = {},
    notificationPermissionGranted: Boolean = true,
    exactAlarmPermissionGranted: Boolean = true,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestExactAlarmPermission: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val themeController = LocalThemeController.current
    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var habitPendingDelete by remember { mutableStateOf<Habit?>(null) }
    var taskPendingDelete by remember { mutableStateOf<Task?>(null) }

    val today   = LocalDate.now()
    val dateStr = today.format(
        DateTimeFormatter.ofPattern("EEEE, d MMM", Locale("es"))
    ).replaceFirstChar { it.uppercase() }

    // All lastCompletedDates from every habit (not just today's)
    val completedDates = state.habits
        .map { it.lastCompletedDate }
        .filter { it.isNotEmpty() }
        .toSet()

    val pendingTasks   = pendingTasksForHome(state.tasks)
    val completedTaskCount = state.tasks.count { it.isCompleted }
    val habitsAtRisk   = state.habits.filter { it.streakCount > 0 && !it.completedToday }
    val isHomeEmpty    = shouldShowHomeEmptyState(state.habits, state.tasks)
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
            .background(MaterialTheme.colorScheme.background)
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Hoy",
                            style = AppTypography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
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
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "LVL ${state.currentLevel.level}",
                                style = AppTypography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            )
                        }
                        ThemeToggleButton(
                            isDarkTheme = themeController.isDarkTheme,
                            onToggle = { themeController.setDarkTheme(!themeController.isDarkTheme) }
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(userAvatarResOrDefault(state.userAvatar)),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // ── Progress card ─────────────────────────────────────
            item {
                val completedCount = state.habits.count { it.completedToday }
                val isComplete = completedCount == state.habits.size && state.habits.isNotEmpty()

                PremiumSurfaceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    accentColor = MaterialTheme.colorScheme.primary,
                    glow = true,
                    radius = 28.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Pequenos pasos, grandes cambios",
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(144.dp)
                                .shadow(
                                    elevation = 14.dp,
                                    shape = CircleShape,
                                    ambientColor = MaterialTheme.colorScheme.primary,
                                    spotColor = MaterialTheme.colorScheme.primary
                                )
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        ) {
                            PetAnimation(
                                stage = petState.stage,
                                streak = petState.streak,
                                reactionState = state.petReactionState,
                                modifier = Modifier.size(140.dp)
                            )
                        }
                        HomeHeroMetric(
                            label = "dias",
                            value = "$currentStreak",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Hábitos section ────────────────────────────────────
            if (isHomeEmpty) {
                item {
                    EmptyStateCard(
                        icon = "+",
                        title = "Comienza tu rutina ✨",
                        subtitle = "Agrega tu primer hábito o tarea para empezar",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (state.habits.isNotEmpty()) {
                item {
                    Text(
                        text = "HÁBITOS",
                        style = AppTypography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                }
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
                            onEdit       = { editingHabit = it },
                            onDelete     = { habitPendingDelete = it },
                            modifier     = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Tareas section ─────────────────────────────────────
            if (!isHomeEmpty) {
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.tasks.isNotEmpty()) {
                            Text(
                                text = "$completedTaskCount/${state.tasks.size}",
                                style = AppTypography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                                .clickable(onClick = onOpenTasksCalendar)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Ver calendario",
                                style = AppTypography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Ver calendario",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            if (pendingTasks.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon     = "✓",
                        title    = "Todo listo por hoy",
                        subtitle = "Tus tareas completadas quedaron guardadas en el calendario",
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
                            onEdit       = { editingTask = it },
                            onDelete     = { taskPendingDelete = it },
                            modifier     = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
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
            contentColor = MaterialTheme.colorScheme.onPrimary
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
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = toast.message,
                        style = AppTypography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // RewardBanner (temporary, top-center)
        AnimatedVisibility(
            visible = state.rewardBanner != null,
            enter = fadeIn(tween(280)) + slideInVertically(tween(320)) { -it },
            exit = fadeOut(tween(220)) + slideOutVertically(tween(260)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
                .zIndex(20f)
        ) {
            state.rewardBanner?.let { b ->
                RewardBanner(primary = b.primary, secondary = b.secondary, modifier = Modifier.padding(horizontal = 12.dp))
            }
        }
    }

    // Quick-add bottom sheet
    if (state.showQuickAdd || editingHabit != null || editingTask != null) {
        HabitCreateSheet(
            onDismiss  = {
                viewModel.hideQuickAdd()
                editingHabit = null
                editingTask = null
            },
            onAddHabit = viewModel::createHabit,
            onAddTask  = viewModel::createTask,
            editingHabit = editingHabit,
            editingTask = editingTask,
            notificationPermissionGranted = notificationPermissionGranted,
            exactAlarmPermissionGranted = exactAlarmPermissionGranted,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestExactAlarmPermission = onRequestExactAlarmPermission,
            onUpdateHabit = { id, name, category, frequency ->
                viewModel.updateHabit(id, name, category, frequency)
                editingHabit = null
            },
            onUpdateTask = { id, name, category, scheduledDate, reminderAtMillis ->
                viewModel.updateTask(id, name, category, scheduledDate, reminderAtMillis)
                editingTask = null
            }
        )
    }

    habitPendingDelete?.let { habit ->
        DeleteConfirmDialog(
            title = "¿Eliminar este hábito?",
            onDismiss = { habitPendingDelete = null },
            onConfirm = {
                viewModel.deleteHabit(habit.id)
                habitPendingDelete = null
            }
        )
    }

    taskPendingDelete?.let { task ->
        DeleteConfirmDialog(
            title = "¿Eliminar esta tarea?",
            onDismiss = { taskPendingDelete = null },
            onConfirm = {
                viewModel.deleteTask(task.id)
                taskPendingDelete = null
            }
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(title, style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Text("Esta accion no se puede deshacer.", style = AppTypography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = Red, style = AppTypography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant, style = AppTypography.labelLarge)
            }
        }
    )
}

@Composable
private fun HomeHeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTypography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
        )
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun ThemeToggleButton(
    isDarkTheme: Boolean,
    onToggle: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val description = if (isDarkTheme) "Cambiar a tema claro" else "Cambiar a tema oscuro"
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outlineVariant, CircleShape)
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = description,
            tint = colors.onSurface,
            modifier = Modifier.size(19.dp)
        )
    }
}
