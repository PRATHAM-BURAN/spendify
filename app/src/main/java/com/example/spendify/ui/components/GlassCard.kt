package com.example.spendify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.spendify.ui.theme.BentoShape
import com.example.spendify.ui.theme.CardShape
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.GlassSurfaceDark
import com.example.spendify.ui.theme.GlassSurfaceHeavyDark

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    isHeavy: Boolean = false,
    borderColor: Color = GlassBorderDark,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundColor = if (isHeavy) GlassSurfaceHeavyDark else GlassSurfaceDark

    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .shadow(
                elevation = if (isHeavy) 12.dp else 4.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x33000000)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.35f),
                        borderColor.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            ),
        content = content
    )
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorderDark,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        shape = BentoShape,
        isHeavy = true,
        borderColor = borderColor,
        onClick = onClick,
        content = content
    )
}
