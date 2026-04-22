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
fun StatCard(
    icon: String,
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, Divider, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp)
    ) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.headlineMedium,
            color = valueColor,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextDim
        )
    }
}
