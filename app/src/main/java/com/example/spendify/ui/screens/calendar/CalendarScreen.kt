package com.example.spendify.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.components.TransactionItemRow
import com.example.spendify.ui.theme.ErrorDark
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
import com.example.spendify.util.CurrencyFormatter
import com.example.spendify.util.DateUtils

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit = onNavigateToSettings,
    onNavigateToAddEditTransaction: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "$"

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            SpendifyTopAppBar(
                title = "Calendar Tracker",
                userInitial = uiState.userProfile?.displayName?.take(1)?.uppercase() ?: "U",
                onSettingsClick = onNavigateToSettings,
                onProfileClick = onNavigateToProfile
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Calendar Month Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Month Navigator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = viewModel::previousMonth) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Month",
                                        tint = PrimaryDark
                                    )
                                }

                                Text(
                                    text = uiState.currentMonthYearString,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )

                                IconButton(onClick = viewModel::nextMonth) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Month",
                                        tint = PrimaryDark
                                    )
                                }
                            }

                            // Days of Week Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                daysOfWeek.forEach { dayName ->
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariantDark,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Month Days Grid (7 columns)
                            val chunks = uiState.calendarDays.chunked(7)
                            chunks.forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    week.forEach { day ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (day.isSelected) PrimaryDark
                                                    else if (day.isToday) SurfaceContainerHighDark
                                                    else Color.Transparent
                                                )
                                                .clickable { viewModel.onDateSelected(day.dateMillis) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = day.dayOfMonth.toString(),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (day.isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (day.isSelected) OnPrimaryDark
                                                    else if (day.isCurrentMonth) OnSurfaceDark
                                                    else OnSurfaceVariantDark.copy(alpha = 0.35f)
                                                )

                                                if (day.hasTransactions) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (day.isSelected) OnPrimaryDark
                                                                else if (day.totalExpense > 0) ErrorDark
                                                                else SecondaryDark
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Selected Day Header & Summary
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = DateUtils.formatDateOnly(uiState.selectedDateMillis),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                            if (uiState.selectedDateExpenseTotal > 0 || uiState.selectedDateIncomeTotal > 0) {
                                Text(
                                    text = "Expense: -${CurrencyFormatter.format(uiState.selectedDateExpenseTotal, currencySymbol)} | Income: +${CurrencyFormatter.format(uiState.selectedDateIncomeTotal, currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariantDark
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigateToAddEditTransaction(null) },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryContainerDark,
                                contentColor = OnPrimaryDark
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Add", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 3. Transactions for selected date
                if (uiState.selectedDateTransactions.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions on this date",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariantDark
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.selectedDateTransactions, key = { it.id }) { tx ->
                        TransactionItemRow(
                            transaction = tx,
                            currencySymbol = currencySymbol,
                            onClick = { onNavigateToAddEditTransaction(tx.id) },
                            onDeleteClick = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
