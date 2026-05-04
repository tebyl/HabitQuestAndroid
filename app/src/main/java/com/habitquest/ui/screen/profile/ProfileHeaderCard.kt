package com.habitquest.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Level
import com.habitquest.ui.component.userAvatarResOrDefault
import com.habitquest.ui.theme.AppTypography

@Composable
fun ProfileHeaderCard(
    avatar: String,
    name: String,
    currentLevel: Level,
    totalXP: Int,
    currentStreak: Int,
    maxStreak: Int,
    rank: Rank,
    onEditName: () -> Unit,
    onEditAvatar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 16.dp else 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            )
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(24.dp))
            .padding(vertical = 24.dp, horizontal = 20.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(userAvatarResOrDefault(avatar)),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
                    .border(3.dp, colors.outlineVariant, CircleShape)
                    .clickable { onEditAvatar() }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.outlineVariant, CircleShape)
                    .clickable { onEditAvatar() }
            ) {
                Text("Edit", style = AppTypography.labelSmall, color = colors.onSurface, fontSize = 9.sp)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = name,
                    style = AppTypography.headlineMedium,
                    color = colors.onSurface
                )
                Text(
                    text = "Editar",
                    style = AppTypography.labelSmall,
                    color = colors.primary,
                    modifier = Modifier.clickable { onEditName() }
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Sigue creciendo, un habito a la vez.",
                style = AppTypography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileMetricChip("Nivel", "LVL ${currentLevel.level}", modifier = Modifier.weight(1f))
            ProfileMetricChip("XP total", totalXP.toString(), modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileMetricChip("Racha", "${currentStreak}d", modifier = Modifier.weight(1f))
            ProfileMetricChip("Mejor", "${maxStreak}d", modifier = Modifier.weight(1f))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surfaceVariant)
                .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(rank.icon, fontSize = 11.sp)
            Text(
                text = "${rank.title} - ${currentLevel.name}",
                style = AppTypography.labelSmall,
                color = colors.onSurface,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ProfileMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = AppTypography.labelSmall,
            color = colors.onSurfaceVariant,
            fontSize = 9.sp
        )
        Text(
            text = value,
            style = AppTypography.titleMedium,
            color = colors.onSurface
        )
    }
}
