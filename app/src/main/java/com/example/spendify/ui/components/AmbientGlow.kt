package com.example.spendify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SecondaryContainerDark

@Composable
fun AmbientGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Top right ambient indigo glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryContainerDark.copy(alpha = 0.20f),
                            PrimaryDark.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .blur(50.dp)
        )

        // Bottom left ambient emerald glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 40.dp)
                .size(260.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SecondaryContainerDark.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .blur(60.dp)
        )

        content()
    }
}
