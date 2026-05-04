package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.AppTypography

@Composable
fun PremiumSurfaceCard(
    modifier: Modifier = Modifier,
    radius: Dp = 24.dp,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    glow: Boolean = false,
    gradient: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val shape = RoundedCornerShape(radius)
    val shadow = when {
        glow && isDark -> 18.dp
        glow -> 8.dp
        isDark -> 8.dp
        else -> 2.dp
    }

    Surface(
        modifier = modifier.shadow(
            elevation = shadow,
            shape = shape,
            ambientColor = if (glow) accentColor else Color.Transparent,
            spotColor = if (glow) accentColor else Color.Transparent
        ),
        shape = shape,
        color = colors.surface,
        contentColor = colors.onSurface,
        tonalElevation = if (isDark) 2.dp else 0.dp,
        border = BorderStroke(1.dp, if (glow && isDark) accentColor else colors.outlineVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (gradient) {
                        Modifier.background(
                            Brush.linearGradient(
                                listOf(colors.surface, colors.surfaceVariant)
                            )
                        )
                    } else {
                        Modifier.background(colors.surface)
                    }
                ),
            content = content
        )
    }
}

@Composable
fun GlowIconBadge(
    icon: ImageVector? = null,
    text: String? = null,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isDark) 10.dp else 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = tint,
                spotColor = tint
            )
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size * 0.54f)
            )
        } else {
            Text(text = text.orEmpty(), fontSize = 20.sp)
        }
    }
}

@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 9.dp,
    startColor: Color = MaterialTheme.colorScheme.primary,
    endColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val colors = MaterialTheme.colorScheme
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(850),
        label = "neonProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .background(colors.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(999.dp))
                .background(Brush.horizontalGradient(listOf(startColor, endColor)))
        )
    }
}

@Composable
fun SectionTitle(
    eyebrow: String,
    trailing: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = eyebrow,
            style = AppTypography.labelSmall,
            color = colors.onSurfaceVariant,
            letterSpacing = 1.4.sp
        )
        if (!trailing.isNullOrBlank()) {
            Text(
                text = trailing,
                style = AppTypography.labelSmall,
                color = colors.primary,
                fontSize = 10.sp
            )
        }
    }
}
