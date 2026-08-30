package com.example.spendify.ui.screens.transactions

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.FilterBottomSheet
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.components.TransactionItemRow
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionHistoryViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit = onNavigateToSettings,
    onNavigateToAddEditTransaction: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "$"

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for floating bottom nav
        ) {
            SpendifyTopAppBar(
                title = "History",
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
                // 1. Search Bar & Filter Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glass Search Input
                        GlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = PillShape
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = OnSurfaceVariantDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = uiState.searchQuery,
                                    onValueChange = viewModel::onSearchQueryChanged,
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        color = OnSurfaceDark
                                    ),
                                    cursorBrush = SolidColor(PrimaryDark),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        if (uiState.searchQuery.isEmpty()) {
                                            Text(
                                                text = "Search transactions...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = OnSurfaceVariantDark.copy(alpha = 0.5f)
                                            )
                                        }
                                        innerTextField()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Filter Button
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerDark.copy(alpha = 0.7f))
                                .clickable { viewModel.openFilterSheet() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = PrimaryDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Quick CSV / PDF Export Button
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerDark.copy(alpha = 0.7f))
                                .clickable { viewModel.exportFilteredPdf() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Export PDF",
                                tint = PrimaryDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. Filter Status summary if active
                if (uiState.searchQuery.isNotBlank() || uiState.filterState.selectedType != null || uiState.filterState.selectedCategoryIds.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Found ${uiState.filteredTransactionsCount} transaction(s)",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryDark
                            )
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantDark,
                                modifier = Modifier.clickable { viewModel.resetFilter() }
                            )
                        }
                    }
                }

                // 3. Date Grouped Transactions
                if (uiState.groupedTransactions.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No matching transactions",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurfaceDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try adjusting your search or filters.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariantDark
                                )
                            }
                        }
                    }
                } else {
                    uiState.groupedTransactions.forEach { group ->
                        item {
                            Text(
                                text = group.header.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariantDark,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }

                        items(group.transactions, key = { it.id }) { tx ->
                            TransactionItemRow(
                                transaction = tx,
                                currencySymbol = currencySymbol,
                                onClick = { onNavigateToAddEditTransaction(tx.id) },
                                onDeleteClick = { viewModel.deleteTransaction(tx) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Filter Bottom Sheet
        if (uiState.isFilterSheetOpen) {
            FilterBottomSheet(
                filterState = uiState.filterState,
                categories = uiState.categories,
                onDismiss = viewModel::closeFilterSheet,
                onApplyFilter = viewModel::applyFilter,
                onResetFilter = viewModel::resetFilter
            )
        }
    }
}
