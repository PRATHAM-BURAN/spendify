package com.example.spendify.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.domain.model.Transaction
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.BentoCard
import com.example.spendify.ui.components.BudgetProgressBar
import com.example.spendify.ui.components.CategoryIconHelper
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.components.TransactionItemRow
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.NumericXl
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SecondaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.TertiaryDark
import com.example.spendify.util.CurrencyFormatter

import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Person

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToCharts: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToAddEditTransaction: (String?) -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "$"

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom nav
        ) {
            // Sticky Top Bar with profile avatar click
            SpendifyTopAppBar(
                title = "Spendify",
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
                // 1. Total Balance Bento Card
                item {
                    BentoCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurfaceVariantDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = CurrencyFormatter.format(uiState.totalBalance, currencySymbol),
                                style = NumericXl,
                                color = OnSurfaceDark
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Income & Expense Split
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Income Column
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = SecondaryDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Income",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = OnSurfaceVariantDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+${CurrencyFormatter.format(uiState.monthlyIncome, currencySymbol)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SecondaryDark
                                    )
                                }

                                // Expense Column
                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = ErrorDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Expense",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = OnSurfaceVariantDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "-${CurrencyFormatter.format(uiState.monthlyExpense, currencySymbol)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ErrorDark
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Navigation Row: Calendar Tracking & Analytics
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onNavigateToCalendar
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryDark.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Calendar",
                                        tint = PrimaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Calendar",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                    Text(
                                        text = "Daily Tracker",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariantDark
                                    )
                                }
                            }
                        }

                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onNavigateToCharts
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SecondaryDark.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PieChart,
                                        contentDescription = "Analytics",
                                        tint = SecondaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Reports",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                    Text(
                                        text = "Insights & Charts",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariantDark
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Budget Progress Section
                item {
                    val budget = uiState.overallBudget
                    val usedPercent = if (budget != null) (budget.percentage * 100).toInt() else 0
                    val status = budget?.status ?: BudgetStatus.SAFE

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        onClick = onNavigateToBudget
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monthly Budget",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurfaceDark
                                )
                                Text(
                                    text = "$usedPercent% Used",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (status) {
                                        BudgetStatus.DANGER -> ErrorDark
                                        BudgetStatus.WARNING -> TertiaryDark
                                        BudgetStatus.SAFE -> SecondaryDark
                                    }
                                )
                            }

                            BudgetProgressBar(
                                percentage = budget?.percentage ?: 0f,
                                status = status,
                                height = 10.dp
                            )

                            Text(
                                text = if (budget != null) {
                                    "${CurrencyFormatter.format(budget.remainingAmount, currencySymbol)} remaining of ${CurrencyFormatter.format(budget.budget.limitAmount, currencySymbol)}"
                                } else {
                                    "Tap to configure your monthly budget"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantDark,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }

                // 3. Top Spending Categories Slider
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Top Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceDark
                            )
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryDark,
                                modifier = Modifier.clickable(onClick = onNavigateToCategories)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            uiState.topCategories.forEach { item ->
                                val categoryColor = CategoryIconHelper.parseColorHex(item.category.colorHex)
                                val icon = CategoryIconHelper.getIconByName(item.category.iconName)

                                Box(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .background(SurfaceContainerDark.copy(alpha = 0.7f))
                                        .border(1.dp, GlassBorderDark, PillShape)
                                        .clickable { onNavigateToCategories() }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = item.category.name,
                                            tint = categoryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.category.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = OnSurfaceDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Recent Transactions Header & Items
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceDark
                        )
                        Text(
                            text = "See History",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryDark,
                            modifier = Modifier.clickable(onClick = onNavigateToHistory)
                        )
                    }
                }

                if (uiState.recentTransactions.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { onNavigateToAddEditTransaction(null) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No transactions yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceVariantDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap the + button below to record your first expense or income.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariantDark
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.recentTransactions, key = { it.id }) { tx ->
                        TransactionItemRow(
                            transaction = tx,
                            currencySymbol = currencySymbol,
                            onClick = { onNavigateToAddEditTransaction(tx.id) },
                            onDeleteClick = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
