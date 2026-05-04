package com.habitquest.ui.screen.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.component.GlowIconBadge
import com.habitquest.ui.theme.AppTypography

@Composable
fun AchievementBadgeCard(
    achievement: ProfileAchievement,
    modifier: Modifier = Modifier
) {
    val unlocked = achievement.unlocked
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (unlocked && isDark) 12.dp else if (unlocked) 5.dp else 0.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            ),
        shape = RoundedCornerShape(22.dp),
        color = if (unlocked) colors.surface else colors.surface,
        contentColor = colors.onSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (unlocked) colors.primary else colors.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            GlowIconBadge(
                text = achievement.icon,
                tint = if (unlocked) colors.primary else colors.onSurfaceVariant,
                size = 44.dp,
                modifier = Modifier.padding(top = 1.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = AppTypography.titleMedium,
                    color = if (unlocked) colors.onSurface else colors.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = AppTypography.labelSmall,
                    color = colors.onSurfaceVariant,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                if (!achievement.progressText.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = achievement.progressText,
                        style = AppTypography.labelSmall,
                        color = if (unlocked) colors.primary else colors.onSurfaceVariant,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
