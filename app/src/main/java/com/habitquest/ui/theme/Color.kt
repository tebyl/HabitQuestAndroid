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
	background = Color(0xFF111318),
	surface = Color(0xFF181B22),
	cardBackground = Color(0xFF1E222B),
	cardBackground2 = Color(0xFF282D38),
	divider = Color(0xFF3C4250),
	dividerLight = Color(0xFF535B6C),
	textPrimary = Color(0xFFF4F6FA),
	textSecondary = Color(0xFFD6DBE5),
	textMuted = Color(0xFFA8B0C0),
	textDim = Color(0xFF828B9D),
	textDimmer = Color(0xFF687284),
)

internal val LightPalette = AppColorPalette(
	background = Color(0xFFFAFAFB),
	surface = Color(0xFFFFFFFF),
	cardBackground = Color(0xFFFFFFFF),
	cardBackground2 = Color(0xFFF3F4F6),
	divider = Color(0xFFE1E4EA),
	dividerLight = Color(0xFFC9CED8),
	textPrimary = Color(0xFF20242A),
	textSecondary = Color(0xFF454B55),
	textMuted = Color(0xFF69717D),
	textDim = Color(0xFF8A929E),
	textDimmer = Color(0xFFA5ACB6),
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

val Amber            = Color(0xFFF4C766)
val AmberDark        = Color(0xFFC98B3A)
val Red              = Color(0xFFFF8A7A)
val Emerald          = Color(0xFF7ED957)
val EmeraldDark      = Color(0xFF5FAE45)
val Orange           = Color(0xFFFF8A7A)
val Rose             = Color(0xFFF43F5E)
val Blue             = Color(0xFF8FB8F6)
val Purple           = Color(0xFFB8A1FF)
val PurpleDark       = Color(0xFFC8B6FF)
val BlueDark         = Color(0xFF9CC6FF)
val EmeraldDarkTheme = Color(0xFF93E86E)
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

val CategoryMente    = Color(0xFFDDA6D8)
val CategoryCuerpo   = Color(0xFFFF8A7A)
val CategorySalud    = Color(0xFFF4C766)
