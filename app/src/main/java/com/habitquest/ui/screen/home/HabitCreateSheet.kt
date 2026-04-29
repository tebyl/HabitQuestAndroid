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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCreateSheet(
    onDismiss: () -> Unit,
    onAddHabit: (name: String, icon: String, category: String, frequency: String) -> Unit,
    onAddTask: (name: String, category: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }

    var tabIndex by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var frequency by remember { mutableStateOf(HabitFrequency.DAILY) }
    var selectedDays by remember { mutableStateOf(emptySet<String>()) }

    val isHabit = tabIndex == 0
    val nameError = name.isBlank()
    val categoryError = selectedCategory == null
    val canSave = !nameError && !categoryError

    fun resetForm(nextTab: Int) {
        tabIndex = nextTab
        name = ""
        selectedCategory = null
        frequency = HabitFrequency.DAILY
        selectedDays = emptySet()
    }

    fun save() {
        val category = selectedCategory ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        if (isHabit) {
            val icon = AppCategories.firstOrNull { it.key == category }?.icon ?: ""
            onAddHabit(trimmedName, icon, category, buildFrequencyString(frequency, selectedDays))
        } else {
            onAddTask(trimmedName, category)
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
                text = "CREAR",
                style = AppTypography.labelSmall,
                color = TextDim,
                letterSpacing = 1.5.sp
            )

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
                keyboardActions = KeyboardActions(onDone = { if (canSave) save() })
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
                        onSelectFrequency = { frequency = it },
                        onToggleDay = { day ->
                            selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                        }
                    )
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
                    enabled = canSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber,
                        contentColor = Background,
                        disabledContainerColor = Divider,
                        disabledContentColor = TextDimmer
                    )
                ) {
                    Text("Guardar", style = AppTypography.labelLarge)
                }
            }
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}
