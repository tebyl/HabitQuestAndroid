package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.AppTypography

@Composable
fun StatSummaryCard(
    icon: String,
    value: String,
    label: String,
    tintColor: Color,
    unit: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val shape = RoundedCornerShape(22.dp)
    val containerColor = if (isDark) colors.surface else colors.surface
    val iconContainer = if (isDark) colors.surfaceVariant else colors.primaryContainer

    Surface(
        modifier = modifier
            .height(86.dp),
        shape = shape,
        color = containerColor,
        contentColor = colors.onSurface,
        tonalElevation = if (isDark) 3.dp else 0.dp,
        shadowElevation = if (isDark) 10.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(tintColor)
            )

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconContainer)
                    .border(1.dp, colors.outlineVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 19.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = AppTypography.labelSmall,
                    color = colors.onSurfaceVariant,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        style = AppTypography.headlineSmall,
                        color = colors.onSurface,
                        fontSize = 21.sp,
                        lineHeight = 22.sp
                    )
                    if (!unit.isNullOrBlank()) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = unit,
                            style = AppTypography.labelSmall,
                            color = tintColor,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}
