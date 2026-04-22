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
	background = Color(0xFF1C1917),
	surface = Color(0xFF292524),
	cardBackground = Color(0xFF292524),
	cardBackground2 = Color(0xFF44403C),
	divider = Color(0xFF292524),
	dividerLight = Color(0xFF44403C),
	textPrimary = Color(0xFFF9FAFB),
	textSecondary = Color(0xFFE5E7EB),
	textMuted = Color(0xFF9CA3AF),
	textDim = Color(0xFF6B7280),
	textDimmer = Color(0xFF4B5563),
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

val Amber            = Color(0xFFF59E0B)
val AmberDark        = Color(0xFFB45309)
val Red              = Color(0xFFEF4444)
val Emerald          = Color(0xFF84CC16) // Warmer Green (Lime)
val EmeraldDark      = Color(0xFF4D7C0F) // Warmer Dark Green
val Orange           = Color(0xFFF97316)
val Rose             = Color(0xFFF43F5E)
val Blue             = Color(0xFF3B82F6)
val Purple           = Color(0xFF8B5CF6)
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
