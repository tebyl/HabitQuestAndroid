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
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Amber)
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
                    text = "TU ESPACIO",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Perfil",
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Bienestar, ritmo y progreso.",
                    style = AppTypography.bodyLarge,
                    color = TextMuted
                )
            }
        }

        // ── Hero card ─────────────────────────────────────────────
        item {
            ProfileHeaderCard(
                avatar      = state.userAvatar,
                name        = state.userName,
                currentLevel = state.currentLevel,
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
        item {
            XpProgressCard(
                currentLevel      = state.currentLevel,
                xpInCurrentLevel  = state.xpInCurrentLevel,
                xpToNextLevel     = state.xpToNextLevel,
                totalXP           = state.totalXP,
                modifier          = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        // ── Stat summary row ──────────────────────────────────────
        item {
            StatSummaryRow(
                totalXP         = state.totalXP,
                currentLevelNum = state.currentLevel.level,
                maxStreak       = state.maxStreak,
                habitsCompleted = state.habits.count { it.streakCount > 0 },
                modifier        = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        // ── Rank progression ──────────────────────────────────────
        item {
            RankCard(
                currentRank = state.rank,
                modifier    = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        // ── Achievements ──────────────────────────────────────────
        item {
            Text(
                text = "LOGROS",
                style = AppTypography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, com.habitquest.ui.theme.Divider, RoundedCornerShape(16.dp))
            ) {
                SettingsRow(
                    icon  = "🔔",
                    label = "Notificaciones",
                    trailing = if (state.notificationsEnabled) "Activas" else "Inactivas",
                    trailingColor = if (state.notificationsEnabled) Emerald else TextDim,
                    onClick = {}
                )
                HorizontalDivider(color = com.habitquest.ui.theme.Divider, thickness = 1.dp)
                SettingsRow(
                    icon  = if (themeController.isDarkTheme) "🌙" else "☀️",
                    label = "Tema",
                    trailing = if (themeController.isDarkTheme) "Oscuro" else "Claro",
                    onClick = { themeController.setDarkTheme(!themeController.isDarkTheme) }
                )
                HorizontalDivider(color = com.habitquest.ui.theme.Divider, thickness = 1.dp)
                SettingsRow(
                    icon = "♪",
                    label = "Sonido ambiental",
                    trailing = if (state.ambientSoundEnabled) "ON" else "OFF",
                    trailingColor = if (state.ambientSoundEnabled) Emerald else TextDim,
                    onClick = { viewModel.setAmbientSoundEnabled(!state.ambientSoundEnabled) }
                )
                HorizontalDivider(color = com.habitquest.ui.theme.Divider, thickness = 1.dp)
                SettingsRow(icon = "🗂️", label = "Categorías",   onClick = {})
                HorizontalDivider(color = com.habitquest.ui.theme.Divider, thickness = 1.dp)
                SettingsRow(icon = "📤", label = "Exportar datos", onClick = {})
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Footer ────────────────────────────────────────────────
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
private fun SettingsRow(
    icon: String,
    label: String,
    trailing: String = "",
    trailingColor: androidx.compose.ui.graphics.Color = TextDim,
    onClick: () -> Unit
) {
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
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        if (trailing.isNotEmpty()) {
            Text(
                text = trailing,
                style = AppTypography.labelSmall,
                color = trailingColor
            )
        }
        Text(text = "›", color = DividerLight, fontSize = 16.sp)
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
                color = TextPrimary
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
                    focusedBorderColor   = Amber,
                    unfocusedBorderColor = com.habitquest.ui.theme.Divider,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextSecondary,
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
                Text("Cancelar", color = TextDim, style = AppTypography.labelLarge)
            }
        }
    )

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}
