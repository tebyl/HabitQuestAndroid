package com.habitquest.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.domain.model.Task
import com.habitquest.ui.screen.home.categoryColor
import com.habitquest.ui.screen.home.categoryLabel
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.CardBackground
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.DividerLight
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim
import com.habitquest.ui.theme.TextDimmer
import com.habitquest.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val taskBg = Color(0xFFFFFFFF)
private val taskBgDone = Color(0xFFF7FBF3)

@Composable
fun TaskCard(
    task: Task,
    onComplete: (Long) -> Unit,
    onUncomplete: ((Long) -> Unit)? = null,
    onEdit: (Task) -> Unit = {},
    onDelete: (Task) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val catColor = categoryColor(task.category)
    val scheduledDate = remember(task.scheduledDate) {
        task.scheduledDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    }
    val today = remember { LocalDate.now() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM") }
    val dateLabel = when {
        scheduledDate == null -> null
        scheduledDate.isAfter(today) -> "Proximamente · ${scheduledDate.format(dateFormatter)}"
        else -> scheduledDate.format(dateFormatter)
    }
    var menuExpanded by remember { mutableStateOf(false) }
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
                if (task.isCompleted) onUncomplete?.invoke(task.id) else onComplete(task.id)
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                text = listOfNotNull(categoryLabel(task.category), dateLabel).joinToString(" · "),
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
                Text("+30 XP", style = AppTypography.labelSmall, color = Emerald, fontSize = 10.sp)
            }
        } else {
            Text("↩", color = TextDimmer, fontSize = 14.sp, modifier = Modifier.alpha(0.6f))
        }

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Purple.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Opciones",
                    tint = TextDim,
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(CardBackground)
            ) {
                DropdownMenuItem(
                    text = { Text("Editar", style = AppTypography.bodyMedium, color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = Purple) },
                    onClick = {
                        menuExpanded = false
                        onEdit(task)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Eliminar", style = AppTypography.bodyMedium, color = Red) },
                    leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = Red) },
                    onClick = {
                        menuExpanded = false
                        onDelete(task)
                    }
                )
            }
        }
    }
}
