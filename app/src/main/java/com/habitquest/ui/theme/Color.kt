package com.habitquest.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorPalette(
	val background: Color,
	val surface: Color,
	val cardBackground: Color,
	val cardBackground2: Color,
	val divider: Color,
	val dividerLight: Color,
	val textPrimary: Color,
	val textSecondary: Color,
	val textMuted: Color,
	val textDim: Color,
	val textDimmer: Color,
)

internal val DarkPalette = AppColorPalette(
	background = Color(0xFF211B1C),
	surface = Color(0xFF2C2426),
	cardBackground = Color(0xFF2C2426),
	cardBackground2 = Color(0xFF3B3134),
	divider = Color(0xFF332A2D),
	dividerLight = Color(0xFF4A3D41),
	textPrimary = Color(0xFFFFF7F3),
	textSecondary = Color(0xFFF0E4DF),
	textMuted = Color(0xFFB9AAA3),
	textDim = Color(0xFF927F78),
	textDimmer = Color(0xFF6F5E59),
)

internal val LightPalette = AppColorPalette(
	background = Color(0xFFFFFBF5),
	surface = Color(0xFFFFFFFF),
	cardBackground = Color(0xFFFFFFFF),
	cardBackground2 = Color(0xFFF2EDE5),
	divider = Color(0xFFE7DFD3),
	dividerLight = Color(0xFFD6CCBE),
	textPrimary = Color(0xFF1F1A16),
	textSecondary = Color(0xFF3B322A),
	textMuted = Color(0xFF6E6255),
	textDim = Color(0xFF8B7D6D),
	textDimmer = Color(0xFFA69584),
)

internal val LocalAppColors = staticCompositionLocalOf { DarkPalette }

val Background: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.background

val Surface: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.surface

val CardBackground: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.cardBackground

val CardBackground2: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.cardBackground2

val Divider: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.divider

val DividerLight: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.dividerLight

val Amber            = Color(0xFFF7C873)
val AmberDark        = Color(0xFFC98B3A)
val Red              = Color(0xFFFF8A7A)
val Emerald          = Color(0xFF7ED957)
val EmeraldDark      = Color(0xFF5FAE45)
val Orange           = Color(0xFFFF8A7A)
val Rose             = Color(0xFFF43F5E)
val Blue             = Color(0xFF7BA7F5)
val Purple           = Color(0xFFA78BFA)
val Gray             = Color(0xFF6B7280)
val WarmGray         = Color(0xFF78716C)

val TextPrimary: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.textPrimary

val TextSecondary: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.textSecondary

val TextMuted: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.textMuted

val TextDim: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.textDim

val TextDimmer: Color
	@Composable
	@ReadOnlyComposable
	get() = LocalAppColors.current.textDimmer

val CategoryMente    = Color(0xFFEC4899) // Warmer (Pink)
val CategoryCuerpo   = Color(0xFFEF4444) // Warm (Red)
val CategorySalud    = Color(0xFFEAB308) // Warmer (Yellow/Amber)
