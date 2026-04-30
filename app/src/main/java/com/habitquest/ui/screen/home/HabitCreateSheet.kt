package com.habitquest.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
    val datePickerZone = remember { ZoneOffset.UTC }
    val dateFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val displayDateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale("es")) }

    val editMode = editingHabit != null || editingTask != null
    val initialFrequency = remember(editingHabit?.frequency) {
        parseFrequencyString(editingHabit?.frequency ?: "daily")
    }
    val initialScheduledDate = remember(editingTask?.scheduledDate) {
        editingTask?.scheduledDate
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?: LocalDate.now()
    }

    var tabIndex by remember(editingHabit?.id, editingTask?.id) {
        mutableIntStateOf(if (editingTask != null) 1 else 0)
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
    var showDatePicker by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    val isHabit = tabIndex == 0
    val nameError = name.isBlank()
    val categoryError = selectedCategory == null
    val frequencyError = isHabit && when (frequency) {
        HabitFrequency.SPECIFIC_DAYS -> selectedDays.isEmpty()
        HabitFrequency.TIMES_PER_WEEK -> timesPerWeek < 1
        HabitFrequency.DAILY -> false
    }
    val canAttemptSave = !nameError && !categoryError

    fun resetForm(nextTab: Int) {
        tabIndex = nextTab
        name = ""
        selectedCategory = null
        frequency = HabitFrequency.DAILY
        selectedDays = emptySet()
        timesPerWeek = 1
        scheduledDate = LocalDate.now()
        attemptedSave = false
    }

    fun save() {
        attemptedSave = true
        val category = selectedCategory ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        if (frequencyError) return

        if (isHabit) {
            val icon = AppCategories.firstOrNull { it.key == category }?.habitIcon ?: ""
            val frequencyText = buildFrequencyString(frequency, selectedDays, timesPerWeek)
            if (editingHabit != null) {
                onUpdateHabit(editingHabit.id, trimmedName, category, frequencyText)
            } else {
                onAddHabit(trimmedName, icon, category, frequencyText)
            }
        } else {
            val scheduledDateText = scheduledDate.format(dateFormatter)
            if (editingTask != null) {
                onUpdateTask(editingTask.id, trimmedName, category, scheduledDateText)
            } else {
                onAddTask(trimmedName, category, scheduledDateText)
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
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DividerLight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = when {
                    editingHabit != null -> "Editar hábito"
                    editingTask != null -> "Editar tarea"
                    else -> "CREAR"
                },
                style = AppTypography.labelSmall,
                color = TextDim,
                letterSpacing = 1.5.sp
            )

            if (!editMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground2)
                ) {
                    listOf("Habito", "Tarea").forEachIndexed { index, label ->
                        val selected = tabIndex == index
                        TextButton(
                            onClick = { resetForm(index) },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Amber else CardBackground2),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (selected) Background else TextDim
                            )
                        ) {
                            Text(label, style = AppTypography.labelLarge, fontSize = 13.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("El nombre es obligatorio", color = Red, style = AppTypography.labelSmall)
                    }
                },
                placeholder = {
                    Text(
                        if (isHabit) "Nombre del habito" else "Nombre de la tarea",
                        color = TextDimmer,
                        style = AppTypography.bodyLarge
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Amber,
                    unfocusedBorderColor = Divider,
                    errorBorderColor = Red,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary,
                    cursorColor = Amber
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (canAttemptSave) save() })
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categoria", style = AppTypography.labelSmall, color = TextDim, fontSize = 10.sp)
                CategorySelector(
                    selected = selectedCategory.orEmpty(),
                    onSelect = { selectedCategory = it },
                    isError = categoryError
                )
            }

            selectedCategory?.let { category ->
                XpPreviewBadge(category = category, isTask = !isHabit)
            }

            if (isHabit) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Frecuencia", style = AppTypography.labelSmall, color = TextDim, fontSize = 10.sp)
                    FrequencySelector(
                        selected = frequency,
                        selectedDays = selectedDays,
                        timesPerWeek = timesPerWeek,
                        showValidationErrors = attemptedSave,
                        onSelectFrequency = { frequency = it },
                        onToggleDay = { day ->
                            selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                        },
                        onSelectTimesPerWeek = { timesPerWeek = it }
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fecha", style = AppTypography.labelSmall, color = TextDim, fontSize = 10.sp)
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Divider)
                    ) {
                        Text(
                            text = scheduledDate.format(displayDateFormatter),
                            color = TextSecondary,
                            style = AppTypography.labelLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Divider)
                ) {
                    Text("Cancelar", color = TextDim, style = AppTypography.labelLarge)
                }
                Button(
                    onClick = ::save,
                    enabled = canAttemptSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber,
                        contentColor = Background,
                        disabledContainerColor = Divider,
                        disabledContentColor = TextDimmer
                    )
                ) {
                    Text(if (editMode) "Guardar cambios" else "Guardar", style = AppTypography.labelLarge)
                }
            }
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
