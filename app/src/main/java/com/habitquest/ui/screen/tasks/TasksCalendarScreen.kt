package com.habitquest.ui.screen.tasks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.domain.model.Task
import com.habitquest.ui.screen.home.categoryColor
import com.habitquest.ui.screen.home.categoryLabel
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Background
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.DividerLight
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextMuted
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal data class CalendarUiState(
    val currentMonth: YearMonth,
    val selectedDate: LocalDate,
    val tasksByDate: Map<LocalDate, List<Task>>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksCalendarScreen(
    viewModel: TasksCalendarViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val today = remember { LocalDate.now() }
    val zoneId = remember { ZoneId.systemDefault() }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }
    val tasksByDate = remember(state.tasks, zoneId) { tasksByDate(state.tasks, zoneId) }
    val calendarState = CalendarUiState(
        currentMonth = currentMonth,
        selectedDate = selectedDate,
        tasksByDate = tasksByDate
    )

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendario",
                        style = AppTypography.titleLarge,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5EDE6)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFF5EDE6), Color(0xFFEFE3F5), Background)
                    )
                )
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MonthHeader(
                month = currentMonth,
                onPrevious = {
                    val targetMonth = currentMonth.minusMonths(1)
                    currentMonth = targetMonth
                    selectedDate = keepSelectedDayInMonth(selectedDate, targetMonth)
                },
                onNext = {
                    val targetMonth = currentMonth.plusMonths(1)
                    currentMonth = targetMonth
                    selectedDate = keepSelectedDayInMonth(selectedDate, targetMonth)
                }
            )

            WeekdayHeader()

            AnimatedContent(
                targetState = currentMonth,
                transitionSpec = {
                    (fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 8 })
                        .togetherWith(fadeOut(tween(140)) + slideOutVertically(tween(140)) { -it / 8 })
                        .using(SizeTransform(clip = false))
                },
                label = "monthCalendar"
            ) { animatedMonth ->
                MonthGrid(
                    uiState = calendarState.copy(currentMonth = animatedMonth),
                    today = today,
                    onSelectDate = { selectedDate = it }
                )
            }

            SelectedDayTasks(
                date = selectedDate,
                tasks = tasksByDate[selectedDate].orEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

internal fun tasksByDate(tasks: List<Task>, zoneId: ZoneId): Map<LocalDate, List<Task>> =
    tasks
        .asSequence()
        .mapNotNull { task ->
            val date = task.calendarDate(zoneId)
            date?.let { it to task }
        }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, dayTasks) -> dayTasks.sortedWith(calendarTaskOrder) }

internal fun monthGridDates(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val sundayOffset = firstDay.dayOfWeek.value % 7
    val gridStart = firstDay.minusDays(sundayOffset.toLong())
    return List(42) { gridStart.plusDays(it.toLong()) }
}

private val calendarTaskOrder = compareBy<Task> { it.isCompleted }
    .thenByDescending { it.completedAt ?: it.createdAt }

private fun Task.calendarDate(zoneId: ZoneId): LocalDate? = when {
    isCompleted && completedAt != null -> Instant.ofEpochMilli(completedAt).atZone(zoneId).toLocalDate()
    !isCompleted && scheduledDate != null -> runCatching { LocalDate.parse(scheduledDate) }.getOrNull()
    else -> null
}

private fun keepSelectedDayInMonth(selectedDate: LocalDate, targetMonth: YearMonth): LocalDate {
    val clampedDay = selectedDate.dayOfMonth.coerceAtMost(targetMonth.lengthOfMonth())
    return targetMonth.atDay(clampedDay)
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.62f))
            .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(22.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Mes anterior", tint = Purple)
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es")))
                .replaceFirstChar { it.uppercase() },
            style = AppTypography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Mes siguiente", tint = Purple)
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val days = listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        days.forEach { day ->
            Text(
                text = day,
                style = AppTypography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MonthGrid(
    uiState: CalendarUiState,
    today: LocalDate,
    onSelectDate: (LocalDate) -> Unit
) {
    val dates = remember(uiState.currentMonth) { monthGridDates(uiState.currentMonth) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(372.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        userScrollEnabled = false
    ) {
        items(dates, key = { it.toString() }) { date ->
            DayCell(
                date = date,
                month = uiState.currentMonth,
                today = today,
                selected = date == uiState.selectedDate,
                tasks = uiState.tasksByDate[date].orEmpty(),
                onClick = { onSelectDate(date) }
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    month: YearMonth,
    today: LocalDate,
    selected: Boolean,
    tasks: List<Task>,
    onClick: () -> Unit
) {
    val isToday = date == today
    val inMonth = YearMonth.from(date) == month
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.035f else 1f,
        animationSpec = tween(150),
        label = "selectedDayScale"
    )
    val background = when {
        selected -> Purple.copy(alpha = 0.18f)
        isToday -> Purple.copy(alpha = 0.10f)
        else -> Color.White.copy(alpha = 0.58f)
    }

    Column(
        modifier = Modifier
            .aspectRatio(0.86f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(if (inMonth) 1f else 0.38f)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = when {
                    selected -> Purple.copy(alpha = 0.64f)
                    isToday -> Purple.copy(alpha = 0.20f)
                    else -> Color.White.copy(alpha = 0.62f)
                },
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = AppTypography.labelLarge,
            color = if (selected) Purple else TextSecondary,
            fontSize = 12.sp
        )

        TaskIndicator(tasks = tasks)
    }
}

@Composable
private fun TaskIndicator(tasks: List<Task>) {
    when {
        tasks.isEmpty() -> Spacer(Modifier.height(14.dp))
        tasks.size == 1 -> {
            val task = tasks.first()
            Text(
                text = task.name,
                style = AppTypography.labelSmall,
                color = if (task.isCompleted) Emerald else Purple,
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (task.isCompleted) Emerald.copy(alpha = 0.12f)
                        else Purple.copy(alpha = 0.12f)
                    )
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            )
        }
        else -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tasks.take(3).forEach { task ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (task.isCompleted) Emerald.copy(alpha = 0.62f) else Purple.copy(alpha = 0.66f))
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedDayTasks(
    date: LocalDate,
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White.copy(alpha = 0.68f))
            .border(1.dp, Color.White.copy(alpha = 0.76f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE d", Locale("es")))
                .replaceFirstChar { it.uppercase() },
            style = AppTypography.titleMedium,
            color = TextPrimary
        )

        AnimatedVisibility(
            visible = tasks.isNotEmpty(),
            enter = fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 4 },
            exit = fadeOut(tween(120)) + slideOutVertically(tween(120)) { it / 4 }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(tasks, key = { "${it.id}_${it.completedAt}_${it.scheduledDate}" }) { task ->
                    CalendarTaskRow(task)
                }
            }
        }

        if (tasks.isEmpty()) {
            Text(
                text = "Sin tareas para este dia",
                style = AppTypography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun CalendarTaskRow(task: Task) {
    val taskColor = if (task.isCompleted) Emerald else Purple
    val category = categoryColor(task.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground2.copy(alpha = 0.62f))
            .border(1.dp, DividerLight.copy(alpha = 0.34f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(7.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(category)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = categoryLabel(task.category),
                style = AppTypography.labelSmall,
                color = category.copy(alpha = 0.82f),
                fontSize = 10.sp
            )
        }
        Text(
            text = if (task.isCompleted) "Completada" else "Pendiente",
            style = AppTypography.labelSmall,
            color = taskColor,
            fontSize = 10.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(taskColor.copy(alpha = 0.10f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
