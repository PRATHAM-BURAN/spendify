package com.example.spendify.ui.screens.budget

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.domain.model.Budget
import com.example.spendify.domain.model.BudgetPeriod
import com.example.spendify.domain.model.BudgetProgress
import com.example.spendify.domain.model.BudgetScope
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.domain.model.Category
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.BentoCard
import com.example.spendify.ui.components.BudgetProgressBar
import com.example.spendify.ui.components.CategoryIconHelper
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.NumericXl
import com.example.spendify.ui.theme.OnPrimaryContainerDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.OutlineVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SecondaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark
import com.example.spendify.ui.theme.TertiaryDark
import com.example.spendify.util.CurrencyFormatter

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
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
                title = "Budgets",
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
                // Header & Add Budget Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Monthly Limits",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                            Text(
                                text = "Track spending limits & thresholds",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantDark
                            )
                        }

                        Button(
                            onClick = { viewModel.openAddBudgetDialog() },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryContainerDark,
                                contentColor = OnPrimaryDark
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Budget", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 1. Overall Monthly Budget Bento Card
                item {
                    val overall = uiState.overallBudget
                    if (overall != null) {
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.openAddBudgetDialog(overall.budget) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Monthly Budget",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = OnSurfaceVariantDark
                                    )
                                    Text(
                                        text = when (overall.status) {
                                            BudgetStatus.SAFE -> "On Track"
                                            BudgetStatus.WARNING -> "Near Limit"
                                            BudgetStatus.DANGER -> "Over Budget"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (overall.status) {
                                            BudgetStatus.SAFE -> SecondaryDark
                                            BudgetStatus.WARNING -> TertiaryDark
                                            BudgetStatus.DANGER -> ErrorDark
                                        }
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = CurrencyFormatter.format(overall.spentAmount, currencySymbol),
                                        style = NumericXl,
                                        color = OnSurfaceDark
                                    )
                                    Text(
                                        text = "/ ${CurrencyFormatter.format(overall.budget.limitAmount, currencySymbol, includeDecimals = false)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = OnSurfaceVariantDark,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                BudgetProgressBar(
                                    percentage = overall.percentage,
                                    status = overall.status,
                                    height = 10.dp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${CurrencyFormatter.format(overall.remainingAmount, currencySymbol)} left",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariantDark
                                    )
                                    Text(
                                        text = "${(overall.percentage * 100).toInt()}% used",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariantDark
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Category Breakdown Section
                item {
                    Text(
                        text = "Category Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark
                    )
                }

                if (uiState.categoryBudgets.isEmpty()) {
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
                                    text = "No category budgets configured yet. Tap 'Set Budget' above to add one.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariantDark,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.categoryBudgets, key = { it.budget.id }) { item ->
                        val categoryColor = when (item.status) {
                            BudgetStatus.SAFE -> SecondaryDark
                            BudgetStatus.WARNING -> TertiaryDark
                            BudgetStatus.DANGER -> ErrorDark
                        }

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            onClick = { viewModel.openAddBudgetDialog(item.budget) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(categoryColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getIconByName(item.budget.categoryName ?: "category"),
                                                contentDescription = null,
                                                tint = categoryColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = item.budget.categoryName ?: "Category",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = OnSurfaceDark
                                            )
                                            Text(
                                                text = when (item.status) {
                                                    BudgetStatus.SAFE -> "Safe"
                                                    BudgetStatus.WARNING -> "Near limit"
                                                    BudgetStatus.DANGER -> "Over budget"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = categoryColor
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = CurrencyFormatter.format(item.spentAmount, currencySymbol),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.status == BudgetStatus.DANGER) ErrorDark else OnSurfaceDark
                                        )
                                        Text(
                                            text = "of ${CurrencyFormatter.format(item.budget.limitAmount, currencySymbol, includeDecimals = false)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariantDark
                                        )
                                    }
                                }

                                BudgetProgressBar(
                                    percentage = item.percentage,
                                    status = item.status,
                                    height = 8.dp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Add / Edit Budget Dialog
        if (uiState.isAddBudgetDialogOpen) {
            AddBudgetDialog(
                editingBudget = uiState.editingBudget,
                categories = uiState.availableCategories,
                currencySymbol = currencySymbol,
                onDismiss = viewModel::closeAddBudgetDialog,
                onSave = viewModel::saveBudget,
                onDelete = { budget -> viewModel.deleteBudget(budget) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    editingBudget: Budget?,
    categories: List<Category>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (BudgetScope, Category?, Double, BudgetPeriod) -> Unit,
    onDelete: (Budget) -> Unit
) {
    var selectedScope by remember { mutableStateOf(editingBudget?.scope ?: BudgetScope.OVERALL) }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.id == editingBudget?.categoryId } ?: categories.firstOrNull())
    }
    var limitAmountString by remember {
        mutableStateOf(if (editingBudget != null) String.format("%.2f", editingBudget.limitAmount) else "500.00")
    }
    var selectedPeriod by remember { mutableStateOf(editingBudget?.period ?: BudgetPeriod.MONTHLY) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerDark,
        title = {
            Text(
                text = if (editingBudget != null) "Edit Budget" else "Set New Budget",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Scope selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(PillShape)
                        .background(SurfaceContainerHighDark)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (selectedScope == BudgetScope.OVERALL) PrimaryDark else Color.Transparent)
                            .clickable { selectedScope = BudgetScope.OVERALL }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Overall",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedScope == BudgetScope.OVERALL) OnPrimaryDark else OnSurfaceVariantDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (selectedScope == BudgetScope.CATEGORY) PrimaryDark else Color.Transparent)
                            .clickable { selectedScope = BudgetScope.CATEGORY }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedScope == BudgetScope.CATEGORY) OnPrimaryDark else OnSurfaceVariantDark
                        )
                    }
                }

                // Category dropdown if scope == CATEGORY
                if (selectedScope == BudgetScope.CATEGORY) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Select Category",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDark,
                                unfocusedBorderColor = GlassBorderDark,
                                focusedTextColor = OnSurfaceDark,
                                unfocusedTextColor = OnSurfaceDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Limit Amount Field
                OutlinedTextField(
                    value = limitAmountString,
                    onValueChange = { limitAmountString = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Budget Limit ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDark,
                        unfocusedBorderColor = GlassBorderDark,
                        focusedTextColor = OnSurfaceDark,
                        unfocusedTextColor = OnSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = limitAmountString.toDoubleOrNull() ?: 0.0
                    if (amount > 0.0) {
                        onSave(selectedScope, selectedCategory, amount, selectedPeriod)
                    }
                },
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = OnPrimaryDark
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (editingBudget != null) {
                    TextButton(onClick = { onDelete(editingBudget); onDismiss() }) {
                        Text("Delete", color = ErrorDark)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = OnSurfaceVariantDark)
                }
            }
        }
    )
}
