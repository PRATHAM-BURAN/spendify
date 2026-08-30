package com.example.spendify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.spendify.domain.model.PaymentMethod
import com.example.spendify.domain.model.PeriodFilter
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryContainerDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

data class TransactionFilterState(
    val selectedType: TransactionType? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
    val selectedPaymentMethods: Set<PaymentMethod> = emptySet(),
    val selectedPeriod: PeriodFilter = PeriodFilter.MONTHLY
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: TransactionFilterState,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onApplyFilter: (TransactionFilterState) -> Unit,
    onResetFilter: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentType by remember { mutableStateOf(filterState.selectedType) }
    var currentCategories by remember { mutableStateOf(filterState.selectedCategoryIds) }
    var currentPaymentMethods by remember { mutableStateOf(filterState.selectedPaymentMethods) }
    var currentPeriod by remember { mutableStateOf(filterState.selectedPeriod) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainerDark,
        contentColor = OnSurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark
                )
                Text(
                    text = "Reset All",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryDark,
                    modifier = Modifier.clickable {
                        currentType = null
                        currentCategories = emptySet()
                        currentPaymentMethods = emptySet()
                        currentPeriod = PeriodFilter.MONTHLY
                        onResetFilter()
                    }
                )
            }

            // 1. Transaction Type
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariantDark
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        label = "All",
                        isSelected = currentType == null,
                        onClick = { currentType = null }
                    )
                    FilterChip(
                        label = "Expense",
                        isSelected = currentType == TransactionType.EXPENSE,
                        onClick = { currentType = TransactionType.EXPENSE }
                    )
                    FilterChip(
                        label = "Income",
                        isSelected = currentType == TransactionType.INCOME,
                        onClick = { currentType = TransactionType.INCOME }
                    )
                }
            }

            // 2. Date Period
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Time Period",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariantDark
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodFilter.values().take(3).forEach { period ->
                        FilterChip(
                            label = period.displayName,
                            isSelected = currentPeriod == period,
                            onClick = { currentPeriod = period }
                        )
                    }
                }
            }

            // 3. Payment Method
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariantDark
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        val isSelected = currentPaymentMethods.contains(method)
                        FilterChip(
                            label = method.displayName,
                            isSelected = isSelected,
                            onClick = {
                                currentPaymentMethods = if (isSelected) {
                                    currentPaymentMethods - method
                                } else {
                                    currentPaymentMethods + method
                                }
                            }
                        )
                    }
                }
            }

            // 4. Categories
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariantDark
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = currentCategories.contains(category.id)
                        FilterChip(
                            label = category.name,
                            isSelected = isSelected,
                            onClick = {
                                currentCategories = if (isSelected) {
                                    currentCategories - category.id
                                } else {
                                    currentCategories + category.id
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = PillShape,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GlassBorderDark))
                ) {
                    Text("Cancel", color = OnSurfaceVariantDark)
                }

                Button(
                    onClick = {
                        onApplyFilter(
                            TransactionFilterState(
                                selectedType = currentType,
                                selectedCategoryIds = currentCategories,
                                selectedPaymentMethods = currentPaymentMethods,
                                selectedPeriod = currentPeriod
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark, contentColor = OnPrimaryDark)
                ) {
                    Text("Apply Filter", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) PrimaryContainerDark else SurfaceContainerHighDark.copy(alpha = 0.6f)
    val textColor = if (isSelected) OnPrimaryContainerDark else OnSurfaceDark
    val borderBrush = if (isSelected) null else androidx.compose.ui.graphics.SolidColor(GlassBorderDark)

    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(backgroundColor)
            .then(
                if (borderBrush != null) Modifier.border(1.dp, borderBrush, PillShape) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}
