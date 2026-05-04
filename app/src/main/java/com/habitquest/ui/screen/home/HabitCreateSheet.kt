package com.habitquest.ui.screen.home

import android.content.pm.ApplicationInfo
import android.util.Log
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import com.habitquest.notification.NotificationHelper
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Background
import com.habitquest.ui.theme.CardBackground
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.Divider
import com.habitquest.ui.theme.DividerLight
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextMuted
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private enum class SheetMode {
    HABIT,
    TASK
}

private enum class TaskDateQuickChoice(val label: String) {
    TODAY("Hoy"),
    TOMORROW("Mañana"),
    CUSTOM("Elegir fecha")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCreateSheet(
    onDismiss: () -> Unit,
    onAddHabit: (name: String, icon: String, category: String, frequency: String) -> Unit,
    onAddTask: (name: String, category: String, scheduledDate: String, reminderAtMillis: Long?) -> Unit,
    editingHabit: Habit? = null,
    editingTask: Task? = null,
    notificationPermissionGranted: Boolean = true,
    exactAlarmPermissionGranted: Boolean = true,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestExactAlarmPermission: () -> Unit = {},
    onUpdateHabit: (id: Long, name: String, category: String, frequency: String) -> Unit = { _, _, _, _ -> },
    onUpdateTask: (id: Long, name: String, category: String, scheduledDate: String, reminderAtMillis: Long?) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val debugBuild = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    val datePickerZone = remember { ZoneOffset.UTC }
    val reminderZone = remember { ZoneId.systemDefault() }
    val dateFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val displayDateFormatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale("es"))
    }

    val editMode = editingHabit != null || editingTask != null
    val initialFrequency = remember(editingHabit?.frequency) {
        parseFrequencyString(editingHabit?.frequency ?: "daily")
    }
    val initialScheduledDate = remember(editingTask?.scheduledDate) {
        editingTask?.scheduledDate
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?: LocalDate.now()
    }

    var mode by remember(editingHabit?.id, editingTask?.id) {
        mutableStateOf(if (editingTask != null) SheetMode.TASK else SheetMode.HABIT)
    }
    var name by remember(editingHabit?.id, editingTask?.id) {
        mutableStateOf(editingHabit?.name ?: editingTask?.name ?: "")
    }
    var selectedCategory by remember(editingHabit?.id, editingTask?.id) {
        mutableStateOf(editingHabit?.category ?: editingTask?.category)
    }
    var frequency by remember(editingHabit?.id) { mutableStateOf(initialFrequency.type) }
    var selectedDays by remember(editingHabit?.id) { mutableStateOf(initialFrequency.selectedDays) }
    var timesPerWeek by remember(editingHabit?.id) { mutableStateOf(initialFrequency.timesPerWeek) }
    var scheduledDate by remember(editingTask?.id) { mutableStateOf(initialScheduledDate) }
    var reminderTime by remember(editingTask?.id) {
        mutableStateOf(
            editingTask?.reminderAtMillis?.let {
                Instant.ofEpochMilli(it).atZone(reminderZone).toLocalTime()
            }
        )
    }
    var dateQuickChoice by remember(editingTask?.id) {
        mutableStateOf(
            when (initialScheduledDate) {
                LocalDate.now() -> TaskDateQuickChoice.TODAY
                LocalDate.now().plusDays(1) -> TaskDateQuickChoice.TOMORROW
                else -> TaskDateQuickChoice.CUSTOM
            }
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var debugReminderAtMillis by remember(editingTask?.id) { mutableStateOf<Long?>(null) }
    var showNotificationPermissionHint by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }
    var pendingTimePickerAfterPermission by remember { mutableStateOf(false) }
    var pendingDebugReminderAfterPermission by remember { mutableStateOf(false) }

    val isHabit = mode == SheetMode.HABIT
    val nameError = attemptedSave && name.isBlank()
    val categoryError = attemptedSave && selectedCategory == null
    fun hasFrequencyError(): Boolean = isHabit && when (frequency) {
        HabitFrequency.SPECIFIC_DAYS -> selectedDays.isEmpty()
        HabitFrequency.TIMES_PER_WEEK -> timesPerWeek !in 1..7
        HabitFrequency.DAILY -> false
    }

    fun resetForm(nextMode: SheetMode) {
        mode = nextMode
        name = ""
        selectedCategory = null
        frequency = HabitFrequency.DAILY
        selectedDays = emptySet()
        timesPerWeek = 1
        scheduledDate = LocalDate.now()
        reminderTime = null
        debugReminderAtMillis = null
        dateQuickChoice = TaskDateQuickChoice.TODAY
        attemptedSave = false
        pendingTimePickerAfterPermission = false
        pendingDebugReminderAfterPermission = false
    }

    fun save() {
        attemptedSave = true
        val category = selectedCategory ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        if (hasFrequencyError()) return

        when (mode) {
            SheetMode.HABIT -> {
                val icon = AppCategories.firstOrNull { it.key == category }?.habitIcon ?: ""
                val frequencyText = buildFrequencyString(frequency, selectedDays, timesPerWeek)
                if (editingHabit != null) {
                    onUpdateHabit(editingHabit.id, trimmedName, category, frequencyText)
                } else {
                    onAddHabit(trimmedName, icon, category, frequencyText)
                }
            }
            SheetMode.TASK -> {
                val requestedReminderAtMillis = debugReminderAtMillis
                    ?: reminderTime?.let {
                        calculateTaskReminderAtMillis(
                            scheduledDate = scheduledDate,
                            selectedTime = it,
                            nowMillis = System.currentTimeMillis(),
                            zone = reminderZone
                        )
                    }
                val resolvedScheduledDate = if (debugReminderAtMillis == null && requestedReminderAtMillis != null) {
                    Instant.ofEpochMilli(requestedReminderAtMillis).atZone(reminderZone).toLocalDate()
                } else {
                    scheduledDate
                }
                val scheduledDateText = resolvedScheduledDate.format(dateFormatter)
                val reminderAtMillis = requestedReminderAtMillis.takeIf { notificationPermissionGranted }
                if (requestedReminderAtMillis != null && !notificationPermissionGranted) {
                    showNotificationPermissionHint = true
                    Log.d(TAG, "Task reminder disabled because notification permission is missing")
                }
                if (reminderAtMillis != null && !exactAlarmPermissionGranted) {
                    Log.w(TAG, "Task reminder saved without exact alarm permission; Android may delay delivery")
                }
                Log.d(
                    TAG,
                    "Saving task scheduledDate=$scheduledDateText requestedReminderAtMillis=$requestedReminderAtMillis " +
                        "finalReminderAtMillis=$reminderAtMillis permissionGranted=$notificationPermissionGranted " +
                        "exactAlarmAllowed=$exactAlarmPermissionGranted"
                )
                if (editingTask != null) {
                    onUpdateTask(editingTask.id, trimmedName, category, scheduledDateText, reminderAtMillis)
                } else {
                    onAddTask(trimmedName, category, scheduledDateText, reminderAtMillis)
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 42.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DividerLight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SheetHeader(
                title = when {
                    editingHabit != null -> "Editar hábito"
                    editingTask != null -> "Editar tarea"
                    isHabit -> "Crear hábito"
                    else -> "Crear tarea"
                },
                subtitle = if (isHabit) {
                    "Diseña un pequeño ritual para tu día"
                } else {
                    "Organiza algo importante sin estrés"
                }
            )

            TypeTabs(
                mode = mode,
                editMode = editMode,
                onSelect = { resetForm(it) }
            )

            SectionCard {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    isError = nameError,
                    supportingText = {
                        AnimatedVisibility(visible = nameError, enter = fadeIn(tween(150))) {
                            Text("El nombre es obligatorio", color = Red, style = AppTypography.labelSmall)
                        }
                    },
                    placeholder = {
                        Text(
                            if (isHabit) "Ej: Meditar 5 minutos" else "Ej: Agendar control médico",
                            color = TextDimmer,
                            style = AppTypography.bodyLarge
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple.copy(alpha = 0.48f),
                        unfocusedBorderColor = DividerLight.copy(alpha = 0.70f),
                        errorBorderColor = Red.copy(alpha = 0.62f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextSecondary,
                        cursorColor = Purple
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { save() })
                )
            }

            SectionCard(
                title = "Categoría"
            ) {
                CategorySelector(
                    selected = selectedCategory.orEmpty(),
                    onSelect = { selectedCategory = it },
                    isError = categoryError
                )
            }

            AnimatedContent(
                targetState = isHabit,
                transitionSpec = {
                    (fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 8 })
                        .togetherWith(fadeOut(tween(140)) + slideOutVertically(tween(140)) { -it / 8 })
                        .using(SizeTransform(clip = false))
                },
                label = "habitTaskMode"
            ) { habitMode ->
                if (habitMode) {
                    SectionCard {
                        FrequencySelector(
                            selected = frequency,
                            selectedDays = selectedDays,
                            timesPerWeek = timesPerWeek,
                            showValidationErrors = attemptedSave,
                            onSelectFrequency = { frequency = it },
                            onToggleDay = { day: String ->
                                selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                            },
                            onSelectTimesPerWeek = { timesPerWeek = it }
                        )
                    }
                } else {
                    SectionCard(title = "¿Para cuándo?") {
                        TaskDateSelector(
                            selected = dateQuickChoice,
                            dateText = scheduledDate.format(displayDateFormatter)
                                .replaceFirstChar { it.uppercase() },
                            onSelect = { choice: TaskDateQuickChoice ->
                                dateQuickChoice = choice
                                when (choice) {
                                    TaskDateQuickChoice.TODAY -> scheduledDate = LocalDate.now()
                                    TaskDateQuickChoice.TOMORROW -> scheduledDate = LocalDate.now().plusDays(1)
                                    TaskDateQuickChoice.CUSTOM -> showDatePicker = true
                                }
                            }
                        )
                        TaskReminderSelector(
                            timeText = debugReminderAtMillis?.let { "Probar en 10s" }
                                ?: reminderTime?.format(timeFormatter),
                            permissionGranted = notificationPermissionGranted,
                            exactAlarmAllowed = exactAlarmPermissionGranted,
                            showPermissionHint = showNotificationPermissionHint,
                            showDebugTest = debugBuild,
                            onTestNow = if (debugBuild && notificationPermissionGranted) {
                                {
                                    Log.d(TAG, "Instant notification test fired")
                                    NotificationHelper.showTaskReminder(context, DEBUG_TEST_NOTIF_ID, "Prueba de notificación HabitQuest")
                                }
                            } else null,
                            onPickTime = {
                                if (!notificationPermissionGranted) {
                                    showNotificationPermissionHint = true
                                    pendingTimePickerAfterPermission = true
                                    onRequestNotificationPermission()
                                } else {
                                    showNotificationPermissionHint = false
                                    pendingTimePickerAfterPermission = false
                                    showTimePicker = true
                                }
                            },
                            onPickDebugReminder = {
                                if (!notificationPermissionGranted) {
                                    showNotificationPermissionHint = true
                                    pendingDebugReminderAfterPermission = true
                                    onRequestNotificationPermission()
                                } else {
                                    debugReminderAtMillis = System.currentTimeMillis() + 10_000L
                                    reminderTime = null
                                    showNotificationPermissionHint = false
                                    if (!exactAlarmPermissionGranted) {
                                        Log.w(TAG, "Debug reminder selected without exact alarm permission; fallback may be delayed")
                                    }
                                    Log.d(TAG, "Selected debug task reminder at $debugReminderAtMillis")
                                }
                            },
                            onRequestExactAlarmPermission = onRequestExactAlarmPermission,
                            onClearTime = {
                                reminderTime = null
                                debugReminderAtMillis = null
                            }
                        )
                    }
                }
            }

            FooterButtons(
                editMode = editMode,
                onDismiss = onDismiss,
                onSave = ::save
            )
        }
    }

    if (showDatePicker) {
        val initialMillis = scheduledDate.atStartOfDay(datePickerZone).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            scheduledDate = Instant.ofEpochMilli(millis).atZone(datePickerZone).toLocalDate()
                            dateQuickChoice = TaskDateQuickChoice.CUSTOM
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialTime = reminderTime ?: LocalTime.now().plusHours(1).withMinute(0)
        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val nowMillis = System.currentTimeMillis()
                        val reminderAtMillis = calculateTaskReminderAtMillis(
                            scheduledDate = scheduledDate,
                            selectedTime = selectedTime,
                            nowMillis = nowMillis,
                            zone = reminderZone
                        )
                        val adjustedDateTime = Instant.ofEpochMilli(reminderAtMillis).atZone(reminderZone)
                        scheduledDate = adjustedDateTime.toLocalDate()
                        dateQuickChoice = quickChoiceForDate(scheduledDate)
                        reminderTime = adjustedDateTime.toLocalTime()
                        debugReminderAtMillis = null
                        Log.d(
                            TAG,
                            "Selected task reminder time requested=$selectedTime scheduledDate=$scheduledDate " +
                                "reminderAtMillis=$reminderAtMillis now=$nowMillis"
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = { TimePicker(state = timePickerState) },
            containerColor = CardBackground
        )
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(notificationPermissionGranted) {
        if (notificationPermissionGranted) {
            if (pendingTimePickerAfterPermission) {
                pendingTimePickerAfterPermission = false
                showNotificationPermissionHint = false
                showTimePicker = true
            }
            if (pendingDebugReminderAfterPermission) {
                pendingDebugReminderAfterPermission = false
                showNotificationPermissionHint = false
                debugReminderAtMillis = System.currentTimeMillis() + 10_000L
            }
        }
    }
}

private const val TAG = "HabitCreateSheet"
private const val DEBUG_TEST_NOTIF_ID = 99_999L
internal const val TASK_REMINDER_MIN_SAFE_DELAY_MILLIS = 60_000L

internal fun calculateTaskReminderAtMillis(
    scheduledDate: LocalDate,
    selectedTime: LocalTime,
    nowMillis: Long,
    zone: ZoneId
): Long {
    val now = Instant.ofEpochMilli(nowMillis).atZone(zone)
    var candidate = scheduledDate.atTime(selectedTime).atZone(zone)
    if (scheduledDate == now.toLocalDate() && !candidate.isAfter(now)) {
        candidate = candidate.plusDays(1)
    }

    val minSafeMillis = nowMillis + TASK_REMINDER_MIN_SAFE_DELAY_MILLIS
    val candidateMillis = candidate.toInstant().toEpochMilli()
    return if (candidateMillis in (nowMillis + 1) until minSafeMillis) {
        minSafeMillis
    } else {
        candidateMillis
    }
}

private fun quickChoiceForDate(date: LocalDate): TaskDateQuickChoice {
    val today = LocalDate.now()
    return when (date) {
        today -> TaskDateQuickChoice.TODAY
        today.plusDays(1) -> TaskDateQuickChoice.TOMORROW
        else -> TaskDateQuickChoice.CUSTOM
    }
}

@Composable
private fun SheetHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = title,
            style = AppTypography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            style = AppTypography.bodyMedium,
            color = TextMuted
        )
    }
}

@Composable
private fun TypeTabs(
    mode: SheetMode,
    editMode: Boolean,
    onSelect: (SheetMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground2.copy(alpha = 0.86f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(SheetMode.HABIT to "Hábito", SheetMode.TASK to "Tarea").forEach { (tabMode, label) ->
            val selected = mode == tabMode
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.02f else 1f,
                animationSpec = tween(150),
                label = "typeTabScale"
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) Purple.copy(alpha = 0.16f) else Color.Transparent)
                    .clickable(enabled = !editMode) { onSelect(tabMode) }
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = label,
                        style = AppTypography.labelLarge,
                        color = if (selected) Purple else TextDim,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground2.copy(alpha = 0.58f))
            .border(1.dp, DividerLight.copy(alpha = 0.52f), RoundedCornerShape(24.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        title?.let {
            Text(
                text = it,
                style = AppTypography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
        content()
    }
}

@Composable
private fun TaskDateSelector(
    selected: TaskDateQuickChoice,
    dateText: String,
    onSelect: (TaskDateQuickChoice) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskDateQuickChoice.entries.forEach { choice ->
                val isSelected = selected == choice
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.03f else 1f,
                    animationSpec = tween(150),
                    label = "dateChipScale"
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) Amber.copy(alpha = 0.18f) else CardBackground.copy(alpha = 0.72f))
                        .border(
                            1.dp,
                            if (isSelected) Amber.copy(alpha = 0.54f) else Divider,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onSelect(choice) }
                        .padding(horizontal = 8.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = choice.label,
                        style = AppTypography.labelSmall,
                        color = if (isSelected) Amber else TextDim,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Purple.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = dateText,
                style = AppTypography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TaskReminderSelector(
    timeText: String?,
    permissionGranted: Boolean,
    exactAlarmAllowed: Boolean,
    showPermissionHint: Boolean,
    showDebugTest: Boolean,
    onTestNow: (() -> Unit)? = null,
    onPickTime: () -> Unit,
    onPickDebugReminder: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onClearTime: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(if (timeText == null) CardBackground.copy(alpha = 0.72f) else Purple.copy(alpha = 0.10f))
                .border(
                    1.dp,
                    if (timeText == null) Divider else Purple.copy(alpha = 0.34f),
                    RoundedCornerShape(18.dp)
                )
                .clickable(onClick = onPickTime)
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Schedule,
                contentDescription = null,
                tint = if (timeText == null) TextDim else Purple,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = timeText?.let { "Recordarme a las $it" } ?: "Recordarme a las...",
                style = AppTypography.labelSmall,
                color = if (timeText == null) TextDim else Purple,
                fontSize = 11.sp
            )
        }

        if (timeText != null) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground.copy(alpha = 0.72f))
                    .border(1.dp, Divider, RoundedCornerShape(16.dp))
                    .clickable(onClick = onClearTime),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsOff,
                    contentDescription = "Quitar recordatorio",
                    tint = TextDim,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
        if (showDebugTest) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Amber.copy(alpha = 0.12f))
                    .border(1.dp, Amber.copy(alpha = 0.34f), RoundedCornerShape(18.dp))
                    .clickable(onClick = onPickDebugReminder)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Probar recordatorio en 10 segundos",
                    style = AppTypography.labelSmall,
                    color = Amber,
                    fontSize = 11.sp
                )
            }
            if (onTestNow != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Emerald.copy(alpha = 0.12f))
                        .border(1.dp, Emerald.copy(alpha = 0.34f), RoundedCornerShape(18.dp))
                        .clickable(onClick = onTestNow)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Probar notificación AHORA (sin delay)",
                        style = AppTypography.labelSmall,
                        color = Emerald,
                        fontSize = 11.sp
                    )
                }
            }
        }
        if (!permissionGranted && showPermissionHint) {
            Text(
                text = "Activa las notificaciones para usar recordatorios. La tarea se puede crear igual.",
                style = AppTypography.labelSmall,
                color = Red,
                fontSize = 10.sp
            )
        }
        if (permissionGranted && !exactAlarmAllowed) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Android no permite alarmas exactas para HabitQuest. Sin ese permiso, los recordatorios pueden llegar con retrasos.",
                    style = AppTypography.labelSmall,
                    color = Amber,
                    fontSize = 10.sp
                )
                OutlinedButton(
                    onClick = onRequestExactAlarmPermission,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Permitir alarmas exactas")
                }
            }
        }
    }
}

@Composable
private fun FooterButtons(
    editMode: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, DividerLight),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Cancelar", color = TextDim, style = AppTypography.labelLarge)
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Amber,
                contentColor = Background
            )
        ) {
            Text(if (editMode) "Guardar cambios" else "Crear", style = AppTypography.labelLarge)
        }
    }
}
