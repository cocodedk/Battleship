package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun CountBadgeOverlay(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF5C2800), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFFF8C00), RoundedCornerShape(6.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "×$count",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFAA44),
            fontFamily = FontFamily.Monospace,
        )
    }
}
