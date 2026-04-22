package com.habitquest.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

private val categories = listOf(
    "salud_mental"  to "Salud Mental",
    "salud_fisica"  to "Salud Física",
    "desarrollo"    to "Desarrollo",
    "productividad" to "Productividad",
    "vida_diaria"   to "Vida Diaria",
    "gamificacion"  to "Gamificación",
)

private val categoryColors = mapOf(
    "salud_mental"  to Purple,
    "salud_fisica"  to Emerald,
    "desarrollo"    to Blue,
    "productividad" to Amber,
    "vida_diaria"   to Orange,
    "gamificacion"  to Rose,
)

private val habitIcons = listOf(
    "🧘", "💪", "📖", "💧", "📋", "🏃",
    "🌅", "💻", "🎯", "🧠", "✅", "🎮",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onAddHabit: (name: String, icon: String, category: String) -> Unit,
    onAddTask: (name: String, category: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tabIndex      by remember { mutableIntStateOf(0) }
    var name          by remember { mutableStateOf("") }
    var selectedCat   by remember { mutableStateOf("salud_mental") }
    var selectedIcon  by remember { mutableStateOf("🧘") }
    val focusRequester = remember { FocusRequester() }

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
                text = "AGREGAR",
                style = AppTypography.labelSmall,
                color = TextDim,
                letterSpacing = 1.5.sp
            )

            // Tab selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground2),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                listOf("Hábito", "Tarea").forEachIndexed { i, label ->
                    val selected = tabIndex == i
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Amber else CardBackground2)
                            .clickable {
                                tabIndex = i
                                name = ""
                                selectedCat = "salud_mental"
                                selectedIcon = "🧘"
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = label,
                            style = AppTypography.labelLarge,
                            color = if (selected) Background else TextDim,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        if (tabIndex == 0) "Nombre del hábito" else "Nombre de la tarea",
                        color = TextDimmer, style = AppTypography.bodyLarge
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Amber,
                    unfocusedBorderColor = Divider,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextSecondary,
                    cursorColor          = Amber
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.isNotBlank()) {
                        if (tabIndex == 0) onAddHabit(name, selectedIcon, selectedCat)
                        else onAddTask(name, selectedCat)
                    }
                })
            )

            // Category chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Categoría", style = AppTypography.labelSmall, color = TextDim, fontSize = 10.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { (key, label) ->
                        val isSelected = selectedCat == key
                        val catColor   = categoryColors[key] ?: Amber
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) catColor.copy(alpha = 0.18f) else CardBackground2)
                                .border(
                                    1.dp,
                                    if (isSelected) catColor.copy(alpha = 0.5f) else Divider,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCat = key }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                style = AppTypography.labelSmall,
                                color = if (isSelected) catColor else TextDim,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Icon grid (habits only)
            if (tabIndex == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Icono", style = AppTypography.labelSmall, color = TextDim, fontSize = 10.sp)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(90.dp)
                    ) {
                        items(habitIcons) { icon ->
                            val isSelected = selectedIcon == icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Amber.copy(alpha = 0.18f) else CardBackground2)
                                    .border(
                                        1.dp,
                                        if (isSelected) Amber.copy(alpha = 0.6f) else Divider,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedIcon = icon }
                            ) {
                                Text(icon, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            // Action buttons
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
                    onClick = {
                        if (name.isNotBlank()) {
                            if (tabIndex == 0) onAddHabit(name, selectedIcon, selectedCat)
                            else onAddTask(name, selectedCat)
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber,
                        contentColor   = Background
                    )
                ) {
                    Text("Crear", style = AppTypography.labelLarge)
                }
            }
        }

        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
