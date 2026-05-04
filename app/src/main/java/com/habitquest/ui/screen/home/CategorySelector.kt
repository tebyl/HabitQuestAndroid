package com.habitquest.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Red
import java.text.Normalizer

private val CategoryFallbackColor = Color(0xFFD8C8FF)

data class CategoryItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val habitIcon: String = ""
)

val AppCategories = listOf(
    CategoryItem("salud_mental", "Salud Mental", Icons.Rounded.SelfImprovement, Color(0xFFC8B6FF)),
    CategoryItem("salud_fisica", "Salud Física", Icons.Rounded.FitnessCenter, Color(0xFFA8E6CF)),
    CategoryItem("desarrollo", "Desarrollo", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFFA7C7E7)),
    CategoryItem("productividad", "Productividad", Icons.Rounded.TrackChanges, Color(0xFFFFD6A5)),
    CategoryItem("vida_diaria", "Vida Diaria", Icons.Rounded.Checklist, Color(0xFFFFADAD)),
    CategoryItem("gamificacion", "Gamificación", Icons.Rounded.SportsEsports, Color(0xFFFFE7A0)),
    CategoryItem("espiritualidad", "Espiritualidad", Icons.Rounded.SelfImprovement, Color(0xFFD8C8FF)),
    CategoryItem("autocuidado", "Autocuidado", Icons.Rounded.Spa, Color(0xFFFFC8DD)),
    CategoryItem("familia_corazon", "Familia / Corazón", Icons.Rounded.Favorite, Color(0xFFFFB3C6)),
    CategoryItem("mama_colegio", "Mamá / Colegio", Icons.Rounded.School, Color(0xFFBDE0FE)),
    CategoryItem("salud", "Salud", Icons.Rounded.HealthAndSafety, Color(0xFFCDEAC0)),
)

private fun categoryToken(category: String): String {
    val withoutAccents = Normalizer.normalize(category.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return withoutAccents
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
}

fun categoryItem(category: String): CategoryItem? {
    val normalized = categoryToken(category)
    return AppCategories.firstOrNull {
        it.key == normalized || categoryToken(it.label) == normalized
    }
}

fun categoryIconFor(category: String): ImageVector =
    categoryItem(category)?.icon ?: Icons.Rounded.Favorite

fun categoryColorFor(category: String): Color =
    categoryItem(category)?.color ?: CategoryFallbackColor

fun categoryColor(category: String): Color = categoryColorFor(category)

fun categoryLabel(category: String): String = categoryItem(category)?.label ?: category.ifBlank { "Categoria" }

@Composable
fun CategorySelector(
    selected: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppCategories.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { cat ->
                    val isSelected = selected == cat.key
                    val scale = animateFloatAsState(
                        targetValue = if (isSelected) 1.02f else 1f,
                        animationSpec = tween(160),
                        label = "categoryCardScale"
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = scale.value
                                scaleY = scale.value
                            }
                            .shadow(
                                elevation = if (isSelected) 8.dp else 0.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = cat.color,
                                spotColor = cat.color
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) colors.primaryContainer else colors.surfaceVariant)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> cat.color
                                    else -> colors.outlineVariant
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelect(cat.key) }
                            .padding(vertical = 16.dp, horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.label,
                            tint = if (isSelected) cat.color else colors.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = cat.label,
                            style = AppTypography.labelSmall,
                            color = if (isSelected) colors.onSurface else colors.onSurfaceVariant,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        AnimatedVisibility(
            visible = isError,
            enter = fadeIn(tween(160))
        ) {
            Text(
                text = "Selecciona una categoria",
                style = AppTypography.labelSmall,
                color = Red,
                fontSize = 11.sp
            )
        }
    }
}
