package com.habitquest.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Level
import com.habitquest.ui.component.userAvatarResOrDefault
import com.habitquest.ui.theme.*

@Composable
fun ProfileHeaderCard(
    avatar: String,
    name: String,
    currentLevel: Level,
    rank: Rank,
    onEditName: () -> Unit,
    onEditAvatar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.78f), Purple.copy(alpha = 0.10f)))
            )
            .border(1.dp, Color.White.copy(alpha = 0.68f), RoundedCornerShape(24.dp))
            .padding(vertical = 24.dp, horizontal = 20.dp)
    ) {
        // Avatar with edit overlay
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(userAvatarResOrDefault(avatar)),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF3F0FF), CircleShape)
                    .clip(CircleShape)
                    .border(3.dp, Color.White.copy(alpha = 0.82f), CircleShape)
                    .clickable { onEditAvatar() }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, DividerLight, CircleShape)
                    .clickable { onEditAvatar() }
            ) {
                Text("✏️", fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Hola, $name 💜",
                style = AppTypography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "✏️",
                fontSize = 14.sp,
                modifier = Modifier.clickable { onEditName() }
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sigue creciendo, un hábito a la vez.",
            style = AppTypography.bodyMedium,
            color = TextMuted
        )

        Spacer(Modifier.height(10.dp))

        // Level + Rank row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(currentLevel.color.copy(alpha = 0.13f))
                    .border(1.dp, currentLevel.color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "LVL ${currentLevel.level} · ${currentLevel.name}",
                    style = AppTypography.labelSmall,
                    color = currentLevel.color,
                    fontSize = 11.sp
                )
            }

            // Rank badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(rank.color.copy(alpha = 0.13f))
                    .border(1.dp, rank.color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(rank.icon, fontSize = 11.sp)
                Text(
                    text = rank.title,
                    style = AppTypography.labelSmall,
                    color = rank.color,
                    fontSize = 11.sp
                )
            }
        }
    }
}
