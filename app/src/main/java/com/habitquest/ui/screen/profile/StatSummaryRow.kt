package com.habitquest.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitquest.ui.component.StatCard
import com.habitquest.ui.theme.*

@Composable
fun StatSummaryRow(
    totalXP: Int,
    currentLevelNum: Int,
    maxStreak: Int,
    habitsCompleted: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                icon       = "⚡",
                value      = "$totalXP",
                label      = "XP Total",
                valueColor = Amber,
                modifier   = Modifier.weight(1f)
            )
            StatCard(
                icon       = "🎖️",
                value      = "LVL $currentLevelNum",
                label      = "Nivel actual",
                valueColor = Purple,
                modifier   = Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                icon       = "🔥",
                value      = "${maxStreak}d",
                label      = "Racha máx.",
                valueColor = Red,
                modifier   = Modifier.weight(1f)
            )
            StatCard(
                icon       = "✅",
                value      = "$habitsCompleted",
                label      = "Hab. activos",
                valueColor = Emerald,
                modifier   = Modifier.weight(1f)
            )
        }
    }
}
