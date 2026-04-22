package com.habitquest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.habitquest.ui.theme.*

@Composable
fun XPBar(
    currentXP: Int,
    maxXP: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val rawPct = if (maxXP > 0) currentXP.toFloat() / maxXP else 0f
    val pct = rawPct.coerceIn(0f, 1f)
    val animatedPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(durationMillis = 800),
        label = "xpBar"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "LVL $level",
                style = AppTypography.labelSmall,
                color = TextMuted
            )
            Text(
                text = "$currentXP / $maxXP XP",
                style = AppTypography.labelSmall,
                color = TextMuted
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardBackground2)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedPct)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Amber, Red))
                    )
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(3.dp))
            )
        }
    }
}
