package com.example.spendify.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.OutlineVariantDark
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark

data class TrendPoint(
    val label: String,
    val value: Double
)

@Composable
fun CustomTrendChart(
    points: List<TrendPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = PrimaryDark
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "trendProgress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 4.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                if (points.isEmpty()) return@Canvas

                val width = size.width
                val height = size.height - 20.dp.toPx()
                val maxValue = (points.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(1.0)

                // Background horizontal grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i / gridLines.toFloat())
                    drawLine(
                        color = OutlineVariantDark.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Calculate Point Coordinates
                val stepX = width / (points.size - 1).coerceAtLeast(1)
                val coordinates = points.mapIndexed { index, point ->
                    val x = index * stepX
                    val normalizedY = (point.value / maxValue).toFloat()
                    val y = height - (normalizedY * height * progress)
                    Offset(x, y)
                }

                if (coordinates.size >= 2) {
                    // Build smooth cubic Bezier path
                    val path = Path().apply {
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }
                    }

                    // Fill Gradient Area
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(coordinates.last().x, height)
                        lineTo(coordinates.first().x, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.35f),
                                lineColor.copy(alpha = 0.0f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Stroke Line
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Point Dots
                    coordinates.forEach { offset ->
                        drawCircle(
                            color = PrimaryContainerDark,
                            radius = 4.dp.toPx(),
                            center = offset
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 2.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // X-Axis Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { point ->
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = OnSurfaceVariantDark
                )
            }
        }
    }
}
