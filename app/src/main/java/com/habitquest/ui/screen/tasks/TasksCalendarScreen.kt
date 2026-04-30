package com.habitquest.ui.screen.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.habitquest.domain.model.Task
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class CalendarRange(val label: String) {
    Week("Semana"),
    Month("Mes")
}

private data class TaskDayGroup(
    val date: LocalDate,
    val tasks: List<Task>
)

@Composable
fun TasksCalendarScreen(
    viewModel: TasksCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var range by remember { mutableStateOf(CalendarRange.Week) }
    val today = remember { LocalDate.now() }
    val zoneId = remember { ZoneId.systemDefault() }
    val groups = remember(state.tasks, range, today, zoneId) {
        completedTaskGroups(
            tasks = state.tasks,
            range = range,
            today = today,
            zoneId = zoneId
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF5EDE6), Color(0xFFEFE3F5), Background)
                )
            ),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Calendario de tareas",
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
                )
                CalendarRangeSelector(
                    selected = range,
                    onSelected = { range = it }
                )
            }
        }

        items(groups, key = { it.date.toString() }) { group ->
            TaskDaySection(
                group = group,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

private fun completedTaskGroups(
    tasks: List<Task>,
    range: CalendarRange,
    today: LocalDate,
    zoneId: ZoneId
): List<TaskDayGroup> {
    val dates = when (range) {
        CalendarRange.Week -> (6 downTo 0).map { today.minusDays(it.toLong()) }
        CalendarRange.Month -> (1..today.lengthOfMonth()).map { today.withDayOfMonth(it) }
    }

    val completedByDate = tasks
        .asSequence()
        .filter { it.isCompleted && it.completedAt != null }
        .groupBy { task ->
            Instant.ofEpochMilli(task.completedAt ?: 0L)
                .atZone(zoneId)
                .toLocalDate()
        }

    return dates.map { date ->
        TaskDayGroup(
            date = date,
            tasks = completedByDate[date].orEmpty().sortedByDescending { it.completedAt }
        )
    }
}

@Composable
private fun CalendarRangeSelector(
    selected: CalendarRange,
    onSelected: (CalendarRange) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.64f))
            .border(1.dp, Color.White.copy(alpha = 0.76f), RoundedCornerShape(18.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CalendarRange.entries.forEach { range ->
            val isSelected = selected == range
            val bg by animateColorAsState(
                targetValue = if (isSelected) Purple.copy(alpha = 0.18f) else Color.Transparent,
                animationSpec = tween(180),
                label = "tasksCalendarRange"
            )
            Text(
                text = range.label,
                style = AppTypography.labelSmall,
                color = if (isSelected) Purple else TextDim,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(bg)
                    .clickable { onSelected(range) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun TaskDaySection(
    group: TaskDayGroup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .border(1.dp, Color.White.copy(alpha = 0.74f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = group.date.format(DateTimeFormatter.ofPattern("EEEE d", Locale("es")))
                        .replaceFirstChar { it.uppercase() },
                    style = AppTypography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = group.date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale("es"))),
                    style = AppTypography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Text(
                text = "${group.tasks.size}",
                style = AppTypography.labelLarge,
                color = if (group.tasks.isEmpty()) TextDim else Emerald,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (group.tasks.isEmpty()) CardBackground2 else Emerald.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        if (group.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBackground2.copy(alpha = 0.62f))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin tareas completadas",
                    style = AppTypography.bodyMedium,
                    color = TextMuted
                )
            }
        } else {
            group.tasks.forEach { task ->
                CompletedTaskRow(task)
            }
        }
    }
}

@Composable
private fun CompletedTaskRow(task: Task) {
    val categoryColor = categoryColor(task.category)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground2.copy(alpha = 0.56f))
            .border(1.dp, DividerLight.copy(alpha = 0.36f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(categoryColor)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = categoryLabel(task.category),
                style = AppTypography.labelSmall,
                color = categoryColor.copy(alpha = 0.82f),
                fontSize = 10.sp
            )
        }
        Text(
            text = "Completada",
            style = AppTypography.labelSmall,
            color = Emerald,
            fontSize = 10.sp
        )
    }
}

private fun categoryColor(category: String): Color = when (category) {
    "productividad" -> Color(0xFF8FB8F6)
    "salud_fisica" -> Color(0xFF7ED957)
    "salud_mental" -> Color(0xFFB8A1FF)
    "vida_diaria" -> Color(0xFFFF8A7A)
    "desarrollo" -> Color(0xFFDDA6D8)
    "disciplina_digital" -> Color(0xFFFFA199)
    "gamificacion" -> Color(0xFFF4C766)
    else -> Amber
}

private fun categoryLabel(category: String): String = when (category) {
    "productividad" -> "Productividad"
    "salud_fisica" -> "Salud fisica"
    "salud_mental" -> "Salud mental"
    "vida_diaria" -> "Vida diaria"
    "desarrollo" -> "Desarrollo"
    "disciplina_digital" -> "Digital"
    "gamificacion" -> "Gamificacion"
    else -> category
}
