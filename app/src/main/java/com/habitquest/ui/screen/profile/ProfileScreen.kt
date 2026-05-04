package com.habitquest.ui.screen.profile

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.domain.gamification.resolvePetState
import com.habitquest.ui.component.EmptyStateCard
import com.habitquest.ui.component.EvolutionPetCard
import com.habitquest.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val themeController = LocalThemeController.current

    val notificationsGranted = remember(state.notificationsEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true
    }

    LaunchedEffect(Unit) { viewModel.setNotificationsEnabled(notificationsGranted) }

    // Dialogs (unchanged)
    if (state.isEditingName) {
        EditNameDialog(
            draft        = state.editNameDraft,
            onDraftChange = viewModel::updateNameDraft,
            onConfirm    = viewModel::confirmEditName,
            onDismiss    = viewModel::cancelEditName
        )
    }
    if (state.showAvatarPicker) {
        AvatarPickerDialog(
            currentAvatar = state.userAvatar,
            onSelect      = viewModel::selectAvatar,
            onDismiss     = viewModel::dismissAvatarPicker
        )
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Page header ───────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "TU PERFIL",
                    style = AppTypography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Perfil",
                    style = AppTypography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // ── Hero card ─────────────────────────────────────────────
        item {
            ProfileHeaderCard(
                avatar      = state.userAvatar,
                name        = state.userName,
                currentLevel = state.currentLevel,
                totalXP = state.totalXP,
                currentStreak = state.maxStreak,
                maxStreak = state.maxStreak,
                rank        = state.rank,
                onEditName  = viewModel::startEditingName,
                onEditAvatar = viewModel::openAvatarPicker,
                modifier    = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        item {
            val petState = resolvePetState(
                totalXP = state.totalXP,
                level = state.currentLevel.level,
                streak = state.maxStreak,
                habitsCompleted = state.habits.sumOf { it.totalDays },
                tasksCompleted = state.tasksCompleted
            )
            EvolutionPetCard(
                petState = petState,
                reactionState = state.petReactionState,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        // ── XP progress ───────────────────────────────────────────
        // ── Stat summary row ──────────────────────────────────────
        // ── Rank progression ──────────────────────────────────────
        // ── Achievements ──────────────────────────────────────────
        item {
            val unlockedCount = state.achievements.count { it.unlocked }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOGROS",
                    style = AppTypography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$unlockedCount/${state.achievements.size} desbloqueados",
                    style = AppTypography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
        }

        if (state.achievements.isEmpty()) {
            item {
                EmptyStateCard(
                    icon     = "🏅",
                    title    = "Sin logros aún",
                    subtitle = "Completa hábitos para descubrir nuevos logros",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(14.dp))
            }
        } else {
            // Achievement badges in pairs
            state.achievements.chunked(2).forEach { row ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { achievement ->
                            AchievementBadgeCard(
                                achievement = achievement,
                                modifier    = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            item { Spacer(Modifier.height(4.dp)) }
        }

        // ── Settings ─────────────────────────────────────────────
        item {
            Text(
                text = "AJUSTES",
                style = AppTypography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            ) {
                SettingsRow(
                    icon  = "🔔",
                    label = "Notificaciones",
                    trailing = if (state.notificationsEnabled) "Activas" else "Inactivas",
                    trailingColor = if (state.notificationsEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                SettingsRow(
                    icon  = if (themeController.isDarkTheme) "🌙" else "☀️",
                    label = "Tema",
                    trailing = if (themeController.isDarkTheme) "Oscuro" else "Claro",
                    onClick = { themeController.setDarkTheme(!themeController.isDarkTheme) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                SettingsRow(
                    icon = "♪",
                    label = "Sonido ambiental",
                    trailing = if (state.ambientSoundEnabled) "ON" else "OFF",
                    trailingColor = if (state.ambientSoundEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { viewModel.setAmbientSoundEnabled(!state.ambientSoundEnabled) }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Footer ────────────────────────────────────────────────
        item {
            Text(
                text = "Hecho con Amor por Esteban R",
                style = AppTypography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: String,
    label: String,
    trailing: String = "",
    trailingColor: androidx.compose.ui.graphics.Color? = null,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Text(
            text = label,
            style = AppTypography.bodyLarge,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (trailing.isNotEmpty()) {
            Text(
                text = trailing,
                style = AppTypography.labelSmall,
                color = trailingColor ?: colors.onSurfaceVariant
            )
        }
        Text(text = ">", color = colors.onSurfaceVariant, fontSize = 16.sp)
    }
}

@Composable
private fun AvatarPickerDialog(
    currentAvatar: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedAvatar = resolveSupportedAvatar(currentAvatar)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp)),
        title = {
            Text(
                text = "Elige tu avatar",
                style = AppTypography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(236.dp)
            ) {
                items(AVATAR_OPTIONS) { option ->
                    val isSelected = option.emoji == selectedAvatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable { onSelect(option.emoji) }
                            .padding(vertical = 12.dp, horizontal = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(option.imageRes),
                                contentDescription = option.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = option.label,
                            style = AppTypography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Cerrar", style = AppTypography.labelLarge)
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
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Editar nombre",
                style = AppTypography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
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
                    focusedBorderColor   = Amber,
                    unfocusedBorderColor = com.habitquest.ui.theme.Divider,
                    focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor   = MaterialTheme.colorScheme.onSurface,
                    cursorColor          = Amber
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
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant, style = AppTypography.labelLarge)
            }
        }
    )

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}
