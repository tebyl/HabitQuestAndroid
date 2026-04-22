package com.habitquest.ui.screen.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.ui.component.LevelRoadmap
import com.habitquest.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val themeController = LocalThemeController.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setNotificationsEnabled(granted)
    }

    val notificationsGranted = remember(state.notificationsEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true
    }

    LaunchedEffect(Unit) {
        viewModel.setNotificationsEnabled(notificationsGranted)
    }

    if (state.isEditingName) {
        EditNameDialog(
            draft = state.editNameDraft,
            onDraftChange = viewModel::updateNameDraft,
            onConfirm = viewModel::confirmEditName,
            onDismiss = viewModel::cancelEditName
        )
    }

    if (state.showAvatarPicker) {
        AvatarPickerDialog(
            currentAvatar = state.userAvatar,
            onSelect = viewModel::selectAvatar,
            onDismiss = viewModel::dismissAvatarPicker
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "TU PERFIL",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = state.userName,
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
                )
            }
        }

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                // Avatar with edit overlay
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Amber, Red)))
                            .clickable { viewModel.openAvatarPicker() }
                    ) {
                        Text(state.userAvatar, fontSize = 40.sp)
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .border(1.dp, Divider, CircleShape)
                            .clickable { viewModel.openAvatarPicker() }
                    ) {
                        Text("✏️", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.userName,
                        style = AppTypography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "✏️",
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { viewModel.startEditingName() }
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(state.currentLevel.color.copy(alpha = 0.13f))
                        .border(
                            1.dp,
                            state.currentLevel.color.copy(alpha = 0.27f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = state.currentLevel.name,
                        style = AppTypography.labelLarge,
                        color = state.currentLevel.color,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "LVL ${state.currentLevel.level}",
                        style = AppTypography.labelSmall,
                        color = TextDim,
                        fontSize = 10.sp
                    )
                }
            }
        }

        item {
            LevelRoadmap(
                currentLevel = state.currentLevel,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, com.habitquest.ui.theme.Divider, RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🔔", fontSize = 18.sp)
                    Text(
                        text = "Notificaciones",
                        style = AppTypography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (state.notificationsEnabled) "Activas" else "Inactivas",
                        style = AppTypography.labelSmall,
                        color = if (state.notificationsEnabled) Emerald else TextDim
                    )
                    Text(text = "›", color = DividerLight, fontSize = 16.sp)
                }

                HorizontalDivider(color = Divider, thickness = 1.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeController.setDarkTheme(!themeController.isDarkTheme) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (themeController.isDarkTheme) "🌙" else "☀️",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Tema",
                        style = AppTypography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (themeController.isDarkTheme) "Dark" else "Light",
                        style = AppTypography.labelSmall,
                        color = TextDim
                    )
                    Text(text = "›", color = DividerLight, fontSize = 16.sp)
                }

                HorizontalDivider(color = Divider, thickness = 1.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🗂️", fontSize = 18.sp)
                    Text(
                        text = "Categorías",
                        style = AppTypography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "›", color = DividerLight, fontSize = 16.sp)
                }

                HorizontalDivider(color = Divider, thickness = 1.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📤", fontSize = 18.sp)
                    Text(
                        text = "Exportar datos",
                        style = AppTypography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "›", color = DividerLight, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Text(
                text = "Hecho con Amor por Esteban R",
                style = AppTypography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun AvatarPickerDialog(
    currentAvatar: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(
                text = "Elige tu avatar",
                style = AppTypography.headlineSmall,
                color = TextPrimary
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(180.dp)
            ) {
                items(AVATAR_OPTIONS) { option ->
                    val isSelected = option.emoji == currentAvatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) Amber.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Amber else Divider,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(option.emoji) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        Text(option.emoji, fontSize = 28.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = option.label,
                            style = AppTypography.labelSmall,
                            color = if (isSelected) Amber else TextMuted,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TextDim, style = AppTypography.labelLarge)
            }
        }
    )
}

@Composable
private fun EditNameDialog(
    draft: String,
    onDraftChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(
                text = "Editar nombre",
                style = AppTypography.headlineSmall,
                color = TextPrimary
            )
        },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Amber,
                    unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary,
                    cursorColor = Amber
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm() })
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Guardar", color = Amber, style = AppTypography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextDim, style = AppTypography.labelLarge)
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
