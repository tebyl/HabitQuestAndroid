package com.habitquest.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.font.FontWeight
import com.habitquest.domain.model.Levels
import com.habitquest.ui.component.NeonProgressBar
import com.habitquest.ui.component.PremiumSurfaceCard
import com.habitquest.ui.theme.AppTypography

@Composable
fun WeeklyProgressCard(
    weeklyHabits: List<DayStats>,
    weeklyTasks: List<DayStats>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val labels = listOf("L", "M", "M", "J", "V", "S", "D")
    val dayData = weeklyHabits.zip(weeklyTasks)
    val habitTotal = weeklyHabits.sumOf { it.count }
    val taskTotal = weeklyTasks.sumOf { it.count }
    val maxValue = maxOf(dayData.maxOfOrNull { it.first.count + it.second.count } ?: 0, 1)

    PremiumSurfaceCard(
        modifier = modifier.fillMaxWidth(),
        radius = 28.dp,
        accentColor = colors.primary,
        glow = true
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "ESTA SEMANA",
                    style = AppTypography.labelSmall,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 1.4.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.primary.copy(alpha = if (isDark) 0.18f else 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$habitTotal hábitos completados",
                        style = AppTypography.labelSmall,
                        color = colors.primary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendPill(color = colors.primary, label = "Hábitos")
                LegendPill(color = colors.secondary, label = "Tareas")
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                drawWeeklyBars(
                    dayData = dayData,
                    maxValue = maxValue,
                    primary = colors.primary,
                    secondary = colors.secondary,
                    surfaceVariant = colors.surfaceVariant,
                    isDark = isDark
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = AppTypography.labelSmall,
                        color = if (index == labels.lastIndex) colors.onSurface else colors.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Text(
                text = if (taskTotal > 0) "$taskTotal tareas registradas esta semana" else "Sin tareas registradas esta semana",
                style = AppTypography.bodySmall,
                color = colors.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    categoryBreakdown: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val items = bucketCategories(categoryBreakdown, colors)
    val maxCount = maxOf(items.maxOfOrNull { it.count } ?: 0, 1)

    PremiumSurfaceCard(
        modifier = modifier.fillMaxWidth(),
        radius = 28.dp,
        accentColor = colors.secondary,
        glow = false
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "POR CATEGORÍA",
                style = AppTypography.labelSmall,
                color = colors.onSurfaceVariant,
                letterSpacing = 1.4.sp
            )

            if (items.isEmpty()) {
                EmptyStatsState(message = "Completa hábitos o tareas para ver tu distribución por categoría")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.forEach { item ->
                        CategoryRow(item = item, maxCount = maxCount)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCalendarCard(
    activity14Days: List<DayStats>,
    totalXP: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val level = Levels.getCurrentLevel(totalXP)
    val chunks = activity14Days.chunked(7)
    val hasActivity = activity14Days.any { it.count > 0 }

    PremiumSurfaceCard(
        modifier = modifier.fillMaxWidth(),
        radius = 28.dp,
        accentColor = colors.tertiary,
        glow = false
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "ÚLTIMOS 14 DÍAS",
                    style = AppTypography.labelSmall,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 1.4.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.primary.copy(alpha = if (isDark) 0.18f else 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Nivel actual  LVL ${level.level}",
                        style = AppTypography.labelSmall,
                        color = colors.primary,
                        fontSize = 10.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                chunks.forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { day ->
                            ActivityChip(
                                day = day,
                                modifier = Modifier.weight(1f),
                                colors = colors,
                                hasActivity = hasActivity
                            )
                        }
                    }
                }
            }

            if (!hasActivity) {
                EmptyStatsState(message = "Completa hábitos o tareas para construir tu progreso")
            }
        }
    }
}

@Composable
fun EmptyStatsState(
    message: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surfaceVariant.copy(alpha = if (colors.background.luminance() < 0.5f) 0.48f else 0.65f))
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = message,
            style = AppTypography.bodyMedium,
            color = colors.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

private data class CategoryBucket(
    val label: String,
    val count: Int,
    val color: Color
)

private fun bucketCategories(
    categoryBreakdown: Map<String, Int>,
    colors: androidx.compose.material3.ColorScheme
): List<CategoryBucket> {
    val counts = linkedMapOf(
        "Bienestar" to 0,
        "Productividad" to 0,
        "Aprendizaje" to 0,
        "Fitness" to 0,
        "Otros" to 0
    )

    categoryBreakdown.forEach { (category, count) ->
        when (category) {
            "salud_mental", "vida_diaria" -> counts["Bienestar"] = counts.getValue("Bienestar") + count
            "productividad" -> counts["Productividad"] = counts.getValue("Productividad") + count
            "desarrollo" -> counts["Aprendizaje"] = counts.getValue("Aprendizaje") + count
            "salud_fisica" -> counts["Fitness"] = counts.getValue("Fitness") + count
            else -> counts["Otros"] = counts.getValue("Otros") + count
        }
    }

    return counts.entries.mapIndexedNotNull { index, (label, count) ->
        if (count <= 0) null else CategoryBucket(label, count, bucketColor(index, colors))
    }.sortedByDescending { it.count }
}

@Composable
private fun CategoryRow(
    item: CategoryBucket,
    maxCount: Int
) {
    val colors = MaterialTheme.colorScheme
    val fraction = (item.count.toFloat() / maxCount).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.label,
                    style = AppTypography.bodyMedium,
                    color = colors.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = item.count.toString(),
                style = AppTypography.labelLarge,
                color = item.color,
                fontSize = 12.sp
            )
        }

        NeonProgressBar(
            progress = fraction,
            height = 10.dp,
            startColor = item.color,
            endColor = colors.primary
        )
    }
}

@Composable
private fun ActivityChip(
    day: DayStats,
    colors: androidx.compose.material3.ColorScheme,
    hasActivity: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = colors.background.luminance() < 0.5f
    val level = when {
        !hasActivity || day.count <= 0 -> 0
        day.count == 1 -> 1
        day.count == 2 -> 2
        else -> 3
    }
    val background = when (level) {
        0 -> colors.surfaceVariant.copy(alpha = if (isDark) 0.42f else 0.58f)
        1 -> colors.primary.copy(alpha = 0.35f)
        2 -> colors.primary.copy(alpha = 0.60f)
        else -> colors.primary.copy(alpha = 0.90f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .border(
                    1.dp,
                    if (day.date == java.time.LocalDate.now()) colors.primary.copy(alpha = 0.85f) else colors.outlineVariant.copy(alpha = 0.45f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = AppTypography.labelSmall,
                color = if (level == 0) colors.onSurfaceVariant else colors.onPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = day.date.dayOfWeek.name.take(1),
            style = AppTypography.labelSmall,
            color = colors.onSurfaceVariant,
            fontSize = 9.sp
        )
    }
}

private fun bucketColor(index: Int, colors: androidx.compose.material3.ColorScheme): Color {
    return when (index) {
        0 -> colors.primary
        1 -> colors.secondary
        2 -> colors.tertiary
        3 -> colors.error
        else -> colors.outline
    }
}

private fun DrawScope.drawWeeklyBars(
    dayData: List<Pair<DayStats, DayStats>>,
    maxValue: Int,
    primary: Color,
    secondary: Color,
    surfaceVariant: Color,
    isDark: Boolean
) {
    val slotW = size.width / 7f
    val barW = slotW * 0.46f
    val corner = CornerRadius(10.dp.toPx())
    val chartTop = 16.dp.toPx()
    val chartHeight = size.height - 22.dp.toPx()

    dayData.forEachIndexed { index, (habits, tasks) ->
        val total = habits.count + tasks.count
        val x = index * slotW + (slotW - barW) / 2f

        drawRoundRect(
            color = surfaceVariant.copy(alpha = if (isDark) 0.46f else 0.70f),
            topLeft = Offset(x, chartTop),
            size = Size(barW, chartHeight),
            cornerRadius = corner
        )

        if (total <= 0) return@forEachIndexed

        val totalHeight = (total.toFloat() / maxValue).coerceIn(0f, 1f) * chartHeight
        val habitHeight = totalHeight * (habits.count.toFloat() / total.toFloat())
        val taskHeight = totalHeight - habitHeight
        val yBottom = chartTop + chartHeight

        if (habitHeight > 0f) {
            drawRoundRect(
                color = primary,
                topLeft = Offset(x, yBottom - habitHeight),
                size = Size(barW, habitHeight),
                cornerRadius = corner
            )
        }

        if (taskHeight > 0f) {
            drawRoundRect(
                color = secondary.copy(alpha = 0.90f),
                topLeft = Offset(x, yBottom - totalHeight),
                size = Size(barW, taskHeight),
                cornerRadius = corner
            )
        }
    }
}

@Composable
private fun LegendPill(color: Color, label: String) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = label, style = AppTypography.labelSmall, color = color, fontSize = 10.sp)
    }
}
