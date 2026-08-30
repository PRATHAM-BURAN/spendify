package com.example.spendify.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.InversePrimaryDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.SecondaryDark
import com.example.spendify.ui.theme.SurfaceContainerHighestDark
import com.example.spendify.ui.theme.TertiaryDark

@Composable
fun BudgetProgressBar(
    percentage: Float,
    status: BudgetStatus,
    height: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "budgetProgressAnim"
    )

    // Pulsing danger animation when over budget
    val infiniteTransition = rememberInfiniteTransition(label = "pulseDanger")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val fillBrush = when (status) {
        BudgetStatus.DANGER -> Brush.horizontalGradient(listOf(ErrorDark, Color(0xFFFF5252)))
        BudgetStatus.WARNING -> Brush.horizontalGradient(listOf(TertiaryDark, Color(0xFFCA8100)))
        BudgetStatus.SAFE -> Brush.horizontalGradient(listOf(PrimaryContainerDark, InversePrimaryDark))
    }

    val alphaModifier = if (status == BudgetStatus.DANGER) {
        Modifier.alpha(pulseAlpha)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(PillShape)
            .background(SurfaceContainerHighestDark.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedPercentage)
                .fillMaxHeight()
                .clip(PillShape)
                .then(alphaModifier)
                .background(fillBrush)
        )
    }
}
