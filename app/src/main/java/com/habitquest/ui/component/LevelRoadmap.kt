package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import com.habitquest.ui.theme.*

@Composable
fun LevelRoadmap(
    currentLevel: Level,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, Divider, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "RUTA DE NIVELES",
            style = AppTypography.labelSmall,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(4.dp))

        Levels.all.forEach { level ->
            val isReached = level.level <= currentLevel.level
            val isCurrent = level.level == currentLevel.level

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.then(
                    if (!isReached) Modifier.then(androidx.compose.ui.Modifier) else Modifier
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isReached) level.color else CardBackground2)
                        .then(
                            if (isCurrent) Modifier.shadow(
                                elevation = 0.dp,
                                shape = CircleShape
                            ) else Modifier
                        )
                        .border(
                            if (isCurrent) 2.dp else 0.dp,
                            if (isCurrent) level.color.copy(alpha = 0.5f) else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Text(
                        text = level.level.toString(),
                        style = AppTypography.labelLarge,
                        color = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = level.name,
                            style = AppTypography.bodyLarge,
                            color = if (isCurrent) level.color else TextSecondary,
                            modifier = if (!isReached) Modifier else Modifier
                        )
                        if (isCurrent) {
                            Text(
                                text = "← TÚ",
                                style = AppTypography.labelSmall,
                                color = Amber,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = "${level.xpRequired} XP",
                        style = AppTypography.labelSmall,
                        color = TextDim
                    )
                }

                if (isReached) {
                    Text(
                        text = if (level.level < currentLevel.level) "✅" else "⭐",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
