package com.example.spendify.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
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

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    val currencySymbol = profile?.currencySymbol ?: "$"

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }

    AmbientGlowBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Top Bar with prominent Back Navigation
            SpendifyTopAppBar(
                title = "My Profile",
                showBackButton = true,
                showSettingsButton = true,
                onBackClick = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. User Header Bento Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Large Avatar
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainerDark)
                                    .border(2.dp, PrimaryDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile?.displayName?.take(1)?.uppercase() ?: "U",
                                    color = OnPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = profile?.displayName?.ifBlank { "Spendify User" } ?: "Guest User",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )

                                IconButton(
                                    onClick = {
                                        editNameInput = profile?.displayName ?: ""
                                        showEditNameDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Name",
                                        tint = PrimaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = profile?.email?.ifBlank { "Not logged in" } ?: "guest@spendify.app",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariantDark
                            )
                        }
                    }
                }

                // 2. Spending Stats Bento Row
                item {
                    Text(
                        text = "ACCOUNT SUMMARY",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariantDark,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatCard(
                            title = "Transactions",
                            value = "${uiState.totalTransactionsCount}",
                            icon = Icons.Default.ReceiptLong,
                            iconColor = PrimaryDark,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatCard(
                            title = "Total Spent",
                            value = CurrencyFormatter.format(uiState.totalSpent, currencySymbol),
                            icon = Icons.Default.TrendingDown,
                            iconColor = ErrorDark,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatCard(
                            title = "Total Income",
                            value = CurrencyFormatter.format(uiState.totalIncome, currencySymbol),
                            icon = Icons.Default.TrendingUp,
                            iconColor = SecondaryDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. Quick Actions
                item {
                    Text(
                        text = "PREFERENCES & TOOLS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariantDark,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )
                }

                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            ProfileOptionItem(
                                icon = Icons.Default.Settings,
                                title = "App Settings & Currency",
                                subtitle = "Currency: ${profile?.currencyCode ?: "USD"}",
                                onClick = onNavigateToSettings
                            )
                            HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(horizontal = 12.dp))
                            ProfileOptionItem(
                                icon = Icons.Default.Category,
                                title = "Manage Categories",
                                subtitle = "Custom expense & income categories",
                                onClick = onNavigateToCategories
                            )
                        }
                    }
                }

                // 4. Log Out Button (Prominent & Clear)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorDark.copy(alpha = 0.15f),
                            contentColor = ErrorDark
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Edit Name Dialog
        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text("Edit Display Name", color = OnSurfaceDark) },
                text = {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editNameInput.isNotBlank()) {
                                viewModel.updateDisplayName(editNameInput)
                            }
                            showEditNameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerDark)
                    ) {
                        Text("Save", color = OnPrimaryDark)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("Cancel", color = OnSurfaceVariantDark)
                    }
                },
                containerColor = SurfaceContainerDark
            )
        }

        // Log Out Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out from Spendify?", color = OnSurfaceDark, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Are you sure you want to log out? Any offline changes will be preserved locally.",
                        color = OnSurfaceVariantDark
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.signOut(onLogoutSuccess)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorDark)
                    ) {
                        Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = OnSurfaceVariantDark)
                    }
                },
                containerColor = SurfaceContainerDark
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark,
                maxLines = 1
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariantDark,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHighDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryDark,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceDark
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantDark
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = OnSurfaceVariantDark.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}
