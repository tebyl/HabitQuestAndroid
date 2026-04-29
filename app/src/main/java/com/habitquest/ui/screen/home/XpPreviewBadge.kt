package com.habitquest.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.ui.theme.*

@Composable
fun XpPreviewBadge(
    category: String,
    isTask: Boolean = false,
    modifier: Modifier = Modifier
) {
    val xp = if (isTask) HabitRepositoryImpl.XP_PER_TASK
              else HabitRepositoryImpl.xpForCategory(category)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Amber.copy(alpha = 0.12f))
            .border(1.dp, Amber.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("⚡ +$xp XP", style = AppTypography.labelLarge, color = Amber, fontSize = 13.sp)
    }
}
