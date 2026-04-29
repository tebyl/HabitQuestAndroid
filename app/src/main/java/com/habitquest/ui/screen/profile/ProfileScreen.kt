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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.setNotificationsEnabled(granted) }

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
            .background(Background),
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
                    color = TextDim,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Héroe",
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
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
                    subtitle = "Completa hábitos para desbloquearlos",
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
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
                HorizontalDivider(color = com.habitquest.ui.theme.Divider, thickness = 1.dp)
                SettingsRow(
                    icon  = if (themeController.isDarkTheme) "🌙" else "☀️",
                    label = "Tema",
                    trailing = if (themeController.isDarkTheme) "Dark" else "Light",
                    onClick = { themeController.setDarkTheme(!themeController.isDarkTheme) }
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
                                color = if (isSelected) Amber else com.habitquest.ui.theme.Divider,
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
