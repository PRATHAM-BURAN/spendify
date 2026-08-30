package com.example.spendify.ui.screens.transactions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.domain.model.PaymentMethod
import com.example.spendify.domain.model.RecurrenceFrequency
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.CategoryIconHelper
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryContainerDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.OutlineVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SecondaryContainerDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark
import com.example.spendify.ui.theme.TertiaryDark
import com.example.spendify.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionScreen(
    viewModel: AddEditTransactionViewModel,
    transactionId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showFrequencyDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SpendifyTopAppBar(
                title = if (transactionId != null) "Edit Transaction" else if (uiState.type == TransactionType.EXPENSE) "Add Expense" else "Add Income",
                showBackButton = true,
                showSettingsButton = false,
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Type Switcher: Expense vs Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(PillShape)
                        .background(SurfaceContainerHighDark)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (uiState.type == TransactionType.EXPENSE) PrimaryDark else Color.Transparent)
                            .clickable { viewModel.onTypeChanged(TransactionType.EXPENSE) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.type == TransactionType.EXPENSE) OnPrimaryDark else OnSurfaceVariantDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (uiState.type == TransactionType.INCOME) PrimaryDark else Color.Transparent)
                            .clickable { viewModel.onTypeChanged(TransactionType.INCOME) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.type == TransactionType.INCOME) OnPrimaryDark else OnSurfaceVariantDark
                        )
                    }
                }

                // Large Amount Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (uiState.type == TransactionType.EXPENSE) "How much did you spend?" else "How much did you earn?",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurfaceVariantDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.currencySymbol,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        BasicTextField(
                            value = uiState.amountString,
                            onValueChange = viewModel::onAmountChanged,
                            textStyle = TextStyle(
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark,
                                textAlign = TextAlign.Center
                            ),
                            cursorBrush = SolidColor(PrimaryDark),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.amountString.isEmpty()) {
                                        Text(
                                            text = "0.00",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurfaceVariantDark.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .width(200.dp)
                                .border(
                                    width = 1.dp,
                                    brush = SolidColor(PrimaryDark.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = ErrorDark,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Categories Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceDark
                        )
                        Text(
                            text = "Manage",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryDark,
                            modifier = Modifier.clickable(onClick = onNavigateToCategories)
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        maxItemsInEachRow = 3
                    ) {
                        uiState.categories.forEach { category ->
                            val isSelected = uiState.selectedCategory?.id == category.id
                            val categoryColor = CategoryIconHelper.parseColorHex(category.colorHex)
                            val icon = CategoryIconHelper.getIconByName(category.iconName)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) PrimaryContainerDark else SurfaceContainerDark.copy(alpha = 0.6f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryDark else GlassBorderDark,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.onCategorySelected(category) }
                                    .padding(vertical = 14.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = category.name,
                                        tint = if (isSelected) OnPrimaryContainerDark else categoryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) OnPrimaryContainerDark else OnSurfaceDark,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Details Glass Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Payment Method Pills
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurfaceVariantDark
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PaymentMethod.values().forEach { method ->
                                    val isSelected = uiState.paymentMethod == method
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(PillShape)
                                            .background(
                                                if (isSelected) SecondaryContainerDark else Color.Transparent
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color.Transparent else OutlineVariantDark.copy(alpha = 0.5f),
                                                shape = PillShape
                                            )
                                            .clickable { viewModel.onPaymentMethodSelected(method) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = method.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else OnSurfaceVariantDark
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = OutlineVariantDark.copy(alpha = 0.3f))

                        // 2. Date & Time Row
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
                                        .background(SurfaceContainerHighDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = PrimaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = DateUtils.formatDisplayDate(uiState.dateMillis),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceDark
                                )
                            }

                            TextButton(
                                onClick = {
                                    val cal = Calendar.getInstance().apply { timeInMillis = uiState.dateMillis }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            cal.set(Calendar.YEAR, year)
                                            cal.set(Calendar.MONTH, month)
                                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            TimePickerDialog(
                                                context,
                                                { _, hourOfDay, minute ->
                                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                    cal.set(Calendar.MINUTE, minute)
                                                    viewModel.onDateChanged(cal.timeInMillis)
                                                },
                                                cal.get(Calendar.HOUR_OF_DAY),
                                                cal.get(Calendar.MINUTE),
                                                false
                                            ).show()
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Text("Edit", color = PrimaryDark)
                            }
                        }

                        Divider(color = OutlineVariantDark.copy(alpha = 0.3f))

                        // 3. Recurring Toggle & Frequency
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
                                        .background(SurfaceContainerHighDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Autorenew,
                                        contentDescription = null,
                                        tint = TertiaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Recurring Entry",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceDark
                                    )
                                    if (uiState.isRecurring) {
                                        Text(
                                            text = uiState.recurrenceFrequency.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TertiaryDark,
                                            modifier = Modifier.clickable { showFrequencyDropdown = true }
                                        )
                                    }
                                }
                            }

                            Switch(
                                checked = uiState.isRecurring,
                                onCheckedChange = viewModel::onRecurringToggled,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = OnPrimaryDark,
                                    checkedTrackColor = PrimaryDark,
                                    uncheckedTrackColor = SurfaceContainerHighDark
                                )
                            )

                            DropdownMenu(
                                expanded = showFrequencyDropdown,
                                onDismissRequest = { showFrequencyDropdown = false }
                            ) {
                                RecurrenceFrequency.values().forEach { freq ->
                                    DropdownMenuItem(
                                        text = { Text(freq.displayName) },
                                        onClick = {
                                            viewModel.onFrequencySelected(freq)
                                            showFrequencyDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Divider(color = OutlineVariantDark.copy(alpha = 0.3f))

                        // 4. Note Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = OnSurfaceVariantDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            BasicTextField(
                                value = uiState.note,
                                onValueChange = viewModel::onNoteChanged,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = OnSurfaceDark
                                ),
                                cursorBrush = SolidColor(PrimaryDark),
                                decorationBox = { innerTextField ->
                                    if (uiState.note.isEmpty()) {
                                        Text(
                                            text = "Add a note (e.g. Lunch with client, monthly rent...)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnSurfaceVariantDark.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Fixed Bottom Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = viewModel::saveTransaction,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDark,
                        contentColor = OnPrimaryDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = if (transactionId != null) "Update Transaction" else if (uiState.type == TransactionType.EXPENSE) "Save Expense" else "Save Income",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
