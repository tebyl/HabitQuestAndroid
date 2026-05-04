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
	background = Color(0xFF090B12),
	surface = Color(0xFF131722),
	cardBackground = Color(0xFF171B28),
	cardBackground2 = Color(0xFF202638),
	divider = Color(0xFF343B50),
	dividerLight = Color(0xFF59627C),
	textPrimary = Color(0xFFF7F8FF),
	textSecondary = Color(0xFFDDE3F3),
	textMuted = Color(0xFFAEB8D0),
	textDim = Color(0xFF8B95AD),
	textDimmer = Color(0xFF707A92),
)

internal val LightPalette = AppColorPalette(
	background = Color(0xFFF7F7FB),
	surface = Color(0xFFFFFFFF),
	cardBackground = Color(0xFFFFFFFF),
	cardBackground2 = Color(0xFFF0F2F8),
	divider = Color(0xFFDDE2ED),
	dividerLight = Color(0xFFC6CDDB),
	textPrimary = Color(0xFF151821),
	textSecondary = Color(0xFF3F4654),
	textMuted = Color(0xFF687284),
	textDim = Color(0xFF8791A1),
	textDimmer = Color(0xFFA1AABC),
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
val Red              = Color(0xFFFF6F91)
val Emerald          = Color(0xFF55E6A5)
val EmeraldDark      = Color(0xFF5FAE45)
val Orange           = Color(0xFFFF9F5A)
val Rose             = Color(0xFFF43F5E)
val Blue             = Color(0xFF6EA8FF)
val Purple           = Color(0xFF9B7CFF)
val PurpleDark       = Color(0xFFC2A7FF)
val BlueDark         = Color(0xFF78D7FF)
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
