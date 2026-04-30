package com.habitquest.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.CardBackground2
import com.habitquest.ui.theme.Divider
import com.habitquest.ui.theme.Red
import com.habitquest.ui.theme.TextDim

data class CategoryItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val habitIcon: String = ""
)

val AppCategories = listOf(
    CategoryItem("salud_mental", "Salud Mental", Icons.Rounded.SelfImprovement, Color(0xFFC8B6FF)),
    CategoryItem("salud_fisica", "Salud Fisica", Icons.Rounded.FitnessCenter, Color(0xFFA8E6CF)),
    CategoryItem("desarrollo", "Desarrollo", Icons.Rounded.MenuBook, Color(0xFFA7C7E7)),
    CategoryItem("productividad", "Productividad", Icons.Rounded.TrackChanges, Color(0xFFFFD6A5)),
    CategoryItem("vida_diaria", "Vida Diaria", Icons.Rounded.Checklist, Color(0xFFFFADAD)),
    CategoryItem("gamificacion", "Gamificacion", Icons.Rounded.SportsEsports, Color(0xFFFFE7A0)),
    CategoryItem("espiritualidad", "Espiritualidad", Icons.Rounded.SelfImprovement, Color(0xFFD8C8FF)),
    CategoryItem("autocuidado", "Autocuidado", Icons.Rounded.Spa, Color(0xFFFFC8DD)),
    CategoryItem("familia_corazon", "Familia / Corazon", Icons.Rounded.Favorite, Color(0xFFFFB3C6)),
    CategoryItem("mama_colegio", "Mama / Colegio", Icons.Rounded.School, Color(0xFFBDE0FE)),
    CategoryItem("salud", "Salud", Icons.Rounded.HealthAndSafety, Color(0xFFCDEAC0)),
)

fun categoryItem(category: String): CategoryItem? = AppCategories.firstOrNull { it.key == category }

fun categoryColor(category: String): Color = categoryItem(category)?.color ?: Amber

fun categoryLabel(category: String): String = categoryItem(category)?.label ?: category

@Composable
fun CategorySelector(
    selected: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppCategories.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { cat ->
                    val isSelected = selected == cat.key
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) cat.color.copy(alpha = 0.20f) else CardBackground2
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> cat.color.copy(alpha = 0.75f)
                                    isError -> Red.copy(alpha = 0.5f)
                                    else -> Divider
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(cat.key) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.label,
                            tint = if (isSelected) cat.color else TextDim,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = cat.label,
                            style = AppTypography.labelSmall,
                            color = if (isSelected) cat.color else TextDim,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (isError) {
            Text(
                text = "Selecciona una categoria",
                style = AppTypography.labelSmall,
                color = Red,
                fontSize = 11.sp
            )
        }
    }
}
