package com.example.spendify.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerHighestDark
import com.example.spendify.ui.theme.TertiaryDark

@Composable
fun OnboardingScreen(
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        Pair(
            "Master Your Finances",
            "Track every dollar, manage budgets, and reach your financial goals with intelligent real-time insights."
        ),
        Pair(
            "Visual Spend Analytics",
            "Understand your cash flow through vibrant dynamic charts, category breakdowns, and monthly trends."
        ),
        Pair(
            "Stay on Budget Always",
            "Set custom category limits with automated threshold alerts before you overspend."
        )
    )

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Simulated Glassmorphic Fintech Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(32.dp),
                    isHeavy = true
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Glow spots
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PrimaryDark.copy(alpha = 0.3f))
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(TertiaryDark.copy(alpha = 0.25f))
                        )

                        // Central Graphic Icon
                        Icon(
                            imageVector = Icons.Default.DonutSmall,
                            contentDescription = null,
                            tint = PrimaryDark,
                            modifier = Modifier.size(110.dp)
                        )

                        // Floating Card Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .rotate(8f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryContainerDark.copy(alpha = 0.85f))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column {
                                Box(modifier = Modifier.size(width = 30.dp, height = 6.dp).clip(PillShape).background(Color.White.copy(alpha = 0.7f)))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.size(width = 50.dp, height = 4.dp).clip(PillShape).background(Color.White.copy(alpha = 0.4f)))
                            }
                        }

                        // Floating Currency Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 24.dp, end = 28.dp)
                                .rotate(-12f)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TertiaryDark)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Text Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pages[currentPage].first,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = pages[currentPage].second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariantDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // Bottom Action Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .size(width = if (isSelected) 28.dp else 8.dp, height = 8.dp)
                                .clip(PillShape)
                                .background(
                                    if (isSelected) PrimaryDark else SurfaceContainerHighestDark
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onNavigateToAuth) {
                        Text("Skip", color = OnSurfaceVariantDark, style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = {
                            if (currentPage < pages.size - 1) {
                                currentPage++
                            } else {
                                onNavigateToAuth()
                            }
                        },
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = OnPrimaryDark
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (currentPage == pages.size - 1) "Get Started" else "Next",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
