package com.habitquest.ui.theme

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")
private val DarkThemeKey = booleanPreferencesKey("dark_theme_enabled")

@Immutable
data class ThemeController(
    val isDarkTheme: Boolean,
    val setDarkTheme: (Boolean) -> Unit,
)

val LocalThemeController = staticCompositionLocalOf {
    ThemeController(
        isDarkTheme = true,
        setDarkTheme = {}
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = DarkPalette.background,
    primaryContainer = DarkPalette.cardBackground,
    onPrimaryContainer = DarkPalette.textPrimary,
    secondary = Orange,
    onSecondary = DarkPalette.background,
    tertiary = Rose,
    background = DarkPalette.background,
    onBackground = DarkPalette.textPrimary,
    surface = DarkPalette.cardBackground,
    onSurface = DarkPalette.textPrimary,
    surfaceVariant = DarkPalette.cardBackground2,
    onSurfaceVariant = DarkPalette.textMuted,
    outline = DarkPalette.divider,
    outlineVariant = DarkPalette.dividerLight,
    error = Red,
)

private val LightColorScheme = lightColorScheme(
    primary = Amber,
    onPrimary = LightPalette.textPrimary,
    primaryContainer = LightPalette.cardBackground2,
    onPrimaryContainer = LightPalette.textPrimary,
    secondary = Orange,
    onSecondary = LightPalette.textPrimary,
    tertiary = Rose,
    background = LightPalette.background,
    onBackground = LightPalette.textPrimary,
    surface = LightPalette.cardBackground,
    onSurface = LightPalette.textPrimary,
    surfaceVariant = LightPalette.cardBackground2,
    onSurfaceVariant = LightPalette.textMuted,
    outline = LightPalette.divider,
    outlineVariant = LightPalette.dividerLight,
    error = Red,
)

@Composable
fun HabitQuestTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDarkTheme by remember { mutableStateOf(true) }

    LaunchedEffect(context) {
        context.appSettingsDataStore.data
            .map { preferences -> preferences[DarkThemeKey] ?: true }
            .collect { storedIsDarkTheme ->
                isDarkTheme = storedIsDarkTheme
            }
    }

    val themeController = remember(context, scope, isDarkTheme) {
        ThemeController(
            isDarkTheme = isDarkTheme,
            setDarkTheme = { enabled ->
                scope.launch {
                    context.appSettingsDataStore.edit { preferences ->
                        preferences[DarkThemeKey] = enabled
                    }
                }
            }
        )
    }

    CompositionLocalProvider(
        LocalThemeController provides themeController,
        LocalAppColors provides if (isDarkTheme) DarkPalette else LightPalette
    ) {
        MaterialTheme(
            colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
