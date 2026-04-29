package com.habitquest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.theme.*

@Composable
fun StatSummaryCard(
    icon: String,
    value: String,
    label: String,
    tintColor: Color = Amber,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, DividerLight, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = icon, fontSize = 24.sp)
        Column {
            Text(
                text = value,
                style = AppTypography.headlineSmall,
                color = tintColor,
                fontSize = 18.sp
            )
            Text(
                text = label,
                style = AppTypography.labelSmall,
                color = TextDim,
                fontSize = 10.sp
            )
        }
    }
}
