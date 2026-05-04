package com.habitquest.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.draw.shadow

@Composable
fun RewardBanner(
    primary: String,
    secondary: String? = null,
    modifier: Modifier = Modifier
) {
    val maxWidth = 560.dp
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    Surface(
        modifier = modifier
            .widthIn(max = maxWidth)
            .shadow(
                elevation = if (isDark) 18.dp else 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            ),
        shape = RoundedCornerShape(22.dp),
        color = colors.surface,
        contentColor = colors.onSurface,
        tonalElevation = if (isDark) 6.dp else 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlowIconBadge(
                icon = Icons.Default.EmojiEvents,
                contentDescription = "reward",
                tint = colors.primary,
                size = 42.dp
            )

            Column {
                Text(
                    text = primary,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    fontSize = 14.sp
                )
                if (!secondary.isNullOrEmpty()) {
                    Text(
                        text = secondary,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RewardBannerPreview_Light() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        RewardBanner(primary = "+50 XP", secondary = "¡Logro desbloqueado! Primer paso", modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RewardBannerPreview_Dark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        RewardBanner(primary = "+50 XP", secondary = "¡Logro desbloqueado! Primer paso", modifier = Modifier.padding(8.dp))
    }
}
