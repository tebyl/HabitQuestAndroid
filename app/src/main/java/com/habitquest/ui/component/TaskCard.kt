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

private val taskBg     = Color(0xFFFFFFFF)
private val taskBgDone = Color(0xFFF7FBF3)

private val categoryColors = mapOf(
    "productividad"      to Color(0xFF8FB8F6),
    "salud_fisica"       to Color(0xFF7ED957),
    "salud_mental"       to Color(0xFFB8A1FF),
    "vida_diaria"        to Color(0xFFFF8A7A),
    "desarrollo"         to Color(0xFFDDA6D8),
    "disciplina_digital" to Color(0xFFFFA199),
    "gamificacion"       to Color(0xFFF4C766),
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
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor.copy(alpha = 0.72f))
            .border(
                1.dp,
                if (task.isCompleted) Emerald.copy(alpha = 0.22f) else DividerLight.copy(alpha = 0.32f),
                RoundedCornerShape(20.dp)
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
                .background(if (task.isCompleted) Emerald else Purple.copy(alpha = 0.10f))
                .border(1.dp, if (task.isCompleted) Emerald else Purple.copy(alpha = 0.22f), CircleShape)
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
                .background(Emerald.copy(alpha = 0.10f))
                .border(1.dp, Emerald.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
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
