package com.habitquest.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

data class CategoryItem(val key: String, val label: String, val icon: String, val color: Color)

val AppCategories = listOf(
    CategoryItem("salud_mental",  "Salud Mental",  "🧘", Purple),
    CategoryItem("salud_fisica",  "Salud Física",  "💪", Emerald),
    CategoryItem("desarrollo",    "Desarrollo",    "📖", Blue),
    CategoryItem("productividad", "Productividad", "🎯", Amber),
    CategoryItem("vida_diaria",   "Vida Diaria",   "📋", Orange),
    CategoryItem("gamificacion",  "Gamificación",  "🎮", Rose),
)

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
                                if (isSelected) cat.color.copy(alpha = 0.15f) else CardBackground2
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> cat.color.copy(alpha = 0.7f)
                                    isError    -> Red.copy(alpha = 0.5f)
                                    else       -> Divider
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(cat.key) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Text(cat.icon, fontSize = 22.sp)
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
                text = "Selecciona una categoría",
                style = AppTypography.labelSmall,
                color = Red,
                fontSize = 11.sp
            )
        }
    }
}
