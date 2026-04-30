package com.habitquest.ui.screen.home

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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import com.habitquest.ui.theme.Amber
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
    onAddTask: (name: String, category: String, scheduledDate: String) -> Unit,
    editingHabit: Habit? = null,
    editingTask: Task? = null,
    onUpdateHabit: (id: Long, name: String, category: String, frequency: String) -> Unit = { _, _, _, _ -> },
    onUpdateTask: (id: Long, name: String, category: String, scheduledDate: String) -> Unit = { _, _, _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    val datePickerZone = remember { ZoneOffset.UTC }
    val dateFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
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
    var attemptedSave by remember { mutableStateOf(false) }

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
        dateQuickChoice = TaskDateQuickChoice.TODAY
        attemptedSave = false
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
                val scheduledDateText = scheduledDate.format(dateFormatter)
                if (editingTask != null) {
                    onUpdateTask(editingTask.id, trimmedName, category, scheduledDateText)
                } else {
                    onAddTask(trimmedName, category, scheduledDateText)
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

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
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
