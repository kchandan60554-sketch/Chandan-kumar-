package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.GlassCard
import com.example.ui.theme.GlassCardBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    borderColor: Color = GlassCardBorder,
    backgroundColor: Color = GlassCard.copy(alpha = 0.75f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        DarkNavy.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp),
        content = content
    )
}
