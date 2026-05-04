package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.AppTypography

@Composable
fun EmptyStateCard(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 8.dp else 2.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            )
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(22.dp))
            .padding(vertical = 28.dp, horizontal = 20.dp)
    ) {
        Text(icon, fontSize = 36.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = AppTypography.titleMedium,
            color = colors.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = AppTypography.bodySmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
