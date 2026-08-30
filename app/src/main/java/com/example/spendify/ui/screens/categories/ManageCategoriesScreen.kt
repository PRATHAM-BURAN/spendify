package com.example.spendify.ui.screens.categories

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.domain.model.Category
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.CategoryIconHelper
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.IconColorPicker
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark

@Composable
fun ManageCategoriesScreen(
    viewModel: CategoryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    AmbientGlowBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            SpendifyTopAppBar(
                title = "Categories",
                showBackButton = true,
                showSettingsButton = false,
                onBackClick = onNavigateBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Add Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Category Manager",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                            Text(
                                text = "Customize your spending buckets",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantDark
                            )
                        }

                        Button(
                            onClick = { viewModel.openAddDialog() },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryDark,
                                contentColor = OnPrimaryDark
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Category", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 1. Expense Categories Section
                item {
                    Text(
                        text = "EXPENSE CATEGORIES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariantDark,
                        letterSpacing = 1.sp
                    )
                }

                items(uiState.expenseCategories, key = { it.id }) { cat ->
                    CategoryTile(
                        category = cat,
                        onDelete = { viewModel.deleteCategory(cat) }
                    )
                }

                // 2. Income Categories Section
                item {
                    Text(
                        text = "INCOME CATEGORIES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariantDark,
                        letterSpacing = 1.sp
                    )
                }

                items(uiState.incomeCategories, key = { it.id }) { cat ->
                    CategoryTile(
                        category = cat,
                        onDelete = { viewModel.deleteCategory(cat) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Add Category Dialog
        if (uiState.isAddDialogOpen) {
            AddCategoryDialog(
                onDismiss = viewModel::closeAddDialog,
                onSave = viewModel::addCustomCategory
            )
        }
    }
}

@Composable
private fun CategoryTile(
    category: Category,
    onDelete: () -> Unit
) {
    val categoryColor = CategoryIconHelper.parseColorHex(category.colorHex)
    val icon = CategoryIconHelper.getIconByName(category.iconName)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = category.name,
                        tint = categoryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark
                    )
                    if (category.isCustom) {
                        Text(
                            text = "Custom Category",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryDark
                        )
                    }
                }
            }

            if (category.isCustom) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Category",
                        tint = ErrorDark.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(CategoryIconHelper.availableIcons.first()) }
    var selectedColor by remember { mutableStateOf(CategoryIconHelper.availableColors.first()) }
    var isIncome by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerDark,
        title = {
            Text(
                text = "New Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDark,
                        unfocusedBorderColor = GlassBorderDark,
                        focusedTextColor = OnSurfaceDark,
                        unfocusedTextColor = OnSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Type Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isIncome = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isIncome) PrimaryDark else SurfaceContainerDark
                        ),
                        shape = PillShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Expense", color = if (!isIncome) OnPrimaryDark else OnSurfaceVariantDark)
                    }

                    Button(
                        onClick = { isIncome = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isIncome) PrimaryDark else SurfaceContainerDark
                        ),
                        shape = PillShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Income", color = if (isIncome) OnPrimaryDark else OnSurfaceVariantDark)
                    }
                }

                IconColorPicker(
                    selectedIcon = selectedIcon,
                    selectedColorHex = selectedColor,
                    onIconSelected = { selectedIcon = it },
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSave(categoryName, selectedIcon, selectedColor, isIncome)
                    }
                },
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = OnPrimaryDark
                )
            ) {
                Text("Add Category")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariantDark)
            }
        }
    )
}
