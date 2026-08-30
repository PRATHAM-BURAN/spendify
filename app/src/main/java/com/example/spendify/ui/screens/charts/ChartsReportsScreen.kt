package com.example.spendify.ui.screens.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.domain.model.PeriodFilter
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.BentoCard
import com.example.spendify.ui.components.CustomDonutChart
import com.example.spendify.ui.components.CustomTrendChart
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SecondaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

@Composable
fun ChartsReportsScreen(
    viewModel: ChartsViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit = onNavigateToSettings,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "$"

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            SpendifyTopAppBar(
                title = "Reports",
                userInitial = uiState.userProfile?.displayName?.take(1)?.uppercase() ?: "U",
                onSettingsClick = onNavigateToSettings,
                onProfileClick = onNavigateToProfile
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header & Time Period Selector
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text(
                                text = "Analytics & Reports",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                            Text(
                                text = "Insights and cash flow breakdown",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantDark
                            )
                        }

                        // Segmented Period Pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(PillShape)
                                .background(SurfaceContainerHighDark)
                                .padding(4.dp)
                        ) {
                            listOf(PeriodFilter.WEEKLY, PeriodFilter.MONTHLY, PeriodFilter.YEARLY).forEach { period ->
                                val isSelected = uiState.selectedPeriod == period
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(PillShape)
                                        .background(if (isSelected) PrimaryDark else Color.Transparent)
                                        .clickable { viewModel.onPeriodSelected(period) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = period.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) OnPrimaryDark else OnSurfaceVariantDark
                                    )
                                }
                            }
                        }
                    }
                }

                // 1. Spending by Category Donut Chart
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "SPENDING BY CATEGORY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariantDark,
                                letterSpacing = 1.sp
                            )

                            if (uiState.pieChartData.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No expenses recorded for this period",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariantDark
                                    )
                                }
                            } else {
                                CustomDonutChart(
                                    data = uiState.pieChartData,
                                    totalAmount = uiState.totalSpent,
                                    currencySymbol = currencySymbol
                                )
                            }
                        }
                    }
                }

                // 2. 6-Month Trend Line Chart
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "6 MONTH SPENDING TREND",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariantDark,
                                    letterSpacing = 1.sp
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = SecondaryDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SecondaryDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            CustomTrendChart(points = uiState.trendPoints)
                        }
                    }
                }

                // 3. Export Actions Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Export & Reports",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceDark
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = viewModel::exportCsv,
                                    shape = PillShape,
                                    modifier = Modifier.weight(1f),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(GlassBorderDark)
                                    )
                                ) {
                                    Icon(Icons.Default.TableChart, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CSV File", color = OnSurfaceDark, style = MaterialTheme.typography.labelMedium)
                                }

                                Button(
                                    onClick = viewModel::exportPdf,
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryDark,
                                        contentColor = OnPrimaryDark
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PDF Report", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
