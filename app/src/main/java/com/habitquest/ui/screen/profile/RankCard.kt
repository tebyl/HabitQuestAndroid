package com.habitquest.ui.screen.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

@Composable
fun RankCard(
    currentRank: Rank,
    modifier: Modifier = Modifier
) {
    val allRanks = Rank.values()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, DividerLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "RANGO DE HÉROE",
            style = AppTypography.labelSmall,
            color = TextMuted,
            letterSpacing = 1.sp
        )

        allRanks.forEach { rank ->
            val isReached  = rank.ordinal <= currentRank.ordinal
            val isCurrent  = rank == currentRank

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rank circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isReached) rank.color.copy(alpha = 0.15f)
                            else CardBackground2
                        )
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) rank.color
                                    else if (isReached) rank.color.copy(alpha = 0.4f)
                                    else DividerLight,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = rank.icon,
                        fontSize = if (isCurrent) 18.sp else 15.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rank.title,
                        style = AppTypography.bodyMedium,
                        color = when {
                            isCurrent -> rank.color
                            isReached -> TextSecondary
                            else      -> TextDimmer
                        }
                    )
                    if (isCurrent) {
                        Text(
                            text = "← Rango actual",
                            style = AppTypography.labelSmall,
                            color = Amber,
                            fontSize = 9.sp
                        )
                    }
                }

                // Status indicator
                when {
                    isCurrent  -> Text("⭐", fontSize = 16.sp)
                    isReached  -> Text("✅", fontSize = 14.sp)
                    else       -> Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(CardBackground2)
                            .border(1.dp, DividerLight, CircleShape)
                    )
                }
            }

            // Connector line (skip after last)
            if (rank.ordinal < allRanks.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 19.dp)
                        .width(2.dp)
                        .height(10.dp)
                        .background(
                            if (rank.ordinal < currentRank.ordinal) rank.color.copy(alpha = 0.35f)
                            else DividerLight
                        )
                )
            }
        }
    }
}
