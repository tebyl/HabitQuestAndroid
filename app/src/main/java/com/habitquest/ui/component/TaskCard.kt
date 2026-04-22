package com.habitquest.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Task
import com.habitquest.ui.theme.*

private val taskBg     = Color(0xFF1E1C1A)
private val taskBgDone = Color(0xFF181614)

private val categoryColors = mapOf(
    "productividad"      to Color(0xFF3B82F6),
    "salud_fisica"       to Color(0xFF10B981),
    "salud_mental"       to Color(0xFF8B5CF6),
    "vida_diaria"        to Color(0xFFF59E0B),
    "desarrollo"         to Color(0xFFEC4899),
    "disciplina_digital" to Color(0xFFEF4444),
    "gamificacion"       to Color(0xFFF59E0B),
)

private val categoryLabels = mapOf(
    "productividad"      to "Productividad",
    "salud_fisica"       to "Salud Física",
    "salud_mental"       to "Salud Mental",
    "vida_diaria"        to "Vida Diaria",
    "desarrollo"         to "Desarrollo",
    "disciplina_digital" to "Digital",
    "gamificacion"       to "Gamificación",
)

@Composable
fun TaskCard(
    task: Task,
    onComplete: (Long) -> Unit,
    onUncomplete: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val catColor = categoryColors[task.category] ?: Amber
    val bgColor by animateColorAsState(
        targetValue = if (task.isCompleted) taskBgDone else taskBg,
        animationSpec = tween(300),
        label = "task_bg"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (task.isCompleted) Color(0xFF2A2826) else Color(0xFF302E2C),
                RoundedCornerShape(14.dp)
            )
            .clickable {
                if (task.isCompleted) onUncomplete?.invoke(task.id)
                else onComplete(task.id)
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Checkbox circle (visual only; row handles the click)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) Emerald else Color.Transparent)
                .border(2.dp, if (task.isCompleted) Emerald else TextDim, CircleShape)
        ) {
            if (task.isCompleted) {
                Text("✓", color = Color.White, fontSize = 14.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                style = AppTypography.bodyLarge,
                color = if (task.isCompleted) TextDim else TextSecondary,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = categoryLabels[task.category] ?: task.category,
                style = AppTypography.labelSmall,
                color = catColor.copy(alpha = if (task.isCompleted) 0.4f else 0.7f),
                fontSize = 10.sp
            )
        }

        if (!task.isCompleted) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1A10B981))
                    .border(1.dp, Color(0x2210B981), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("⚡", fontSize = 10.sp)
                Text("+30 XP", style = AppTypography.labelSmall, color = Emerald, fontSize = 10.sp)
            }
        } else {
            Text("↩", color = TextDimmer, fontSize = 14.sp, modifier = Modifier.alpha(0.6f))
        }
    }
}
