package com.example.spendify.ui.screens.settings

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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendify.domain.model.CurrencyOption
import com.example.spendify.domain.model.ThemeMode
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.components.SpendifyTopAppBar
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.OutlineVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    AmbientGlowBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            SpendifyTopAppBar(
                title = "Settings",
                showBackButton = true,
                showSettingsButton = false,
                onBackClick = onNavigateBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // User Profile Header Card (Opens Profile)
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        onClick = onNavigateToProfile
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PrimaryDark.copy(alpha = 0.2f))
                                .border(1.dp, PrimaryDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile?.displayName?.take(1)?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.displayName?.ifBlank { "Spendify User" } ?: "Guest User",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )
                                Text(
                                    text = profile?.email?.ifBlank { "guest@spendify.app" } ?: "guest@spendify.app",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariantDark
                                )
                                Text(
                                    text = "Tap to view profile & metrics",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryDark,
                                    fontSize = 11.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Profile",
                                tint = OnSurfaceVariantDark
                            )
                        }
                    }
                }

                // App Preferences Group
                item {
                    Text(
                        text = "PREFERENCES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariantDark,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1. Manage Categories
                            SettingsRow(
                                icon = Icons.Default.Category,
                                title = "Manage Categories",
                                onClick = onNavigateToCategories
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = OnSurfaceVariantDark
                                )
                            }

                            Divider(color = OutlineVariantDark.copy(alpha = 0.2f))

                            // 2. Dark Mode Toggle
                            val isDarkMode = profile?.themeMode != ThemeMode.LIGHT
                            SettingsRow(
                                icon = Icons.Default.DarkMode,
                                title = "Dark Mode"
                            ) {
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { checked ->
                                        viewModel.onThemeChanged(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = OnPrimaryDark,
                                        checkedTrackColor = PrimaryDark,
                                        uncheckedTrackColor = SurfaceContainerHighDark
                                    )
                                )
                            }

                            Divider(color = OutlineVariantDark.copy(alpha = 0.2f))

                            // 3. Currency Selector
                            SettingsRow(
                                icon = Icons.Default.Payments,
                                title = "Currency",
                                onClick = { showCurrencyDropdown = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showCurrencyDropdown = true }
                                ) {
                                    Text(
                                        text = "${profile?.currencyCode ?: "USD"} (${profile?.currencySymbol ?: "$"})",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PrimaryDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = OnSurfaceVariantDark
                                    )

                                    DropdownMenu(
                                        expanded = showCurrencyDropdown,
                                        onDismissRequest = { showCurrencyDropdown = false }
                                    ) {
                                        CurrencyOption.values().forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option.displayName) },
                                                onClick = {
                                                    viewModel.onCurrencyChanged(option)
                                                    showCurrencyDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Divider(color = OutlineVariantDark.copy(alpha = 0.2f))

                            // 4. Budget Alert Notifications
                            SettingsRow(
                                icon = Icons.Default.Notifications,
                                title = "Budget Alerts (80% & 100%)"
                            ) {
                                Switch(
                                    checked = profile?.budgetAlertEnabled ?: true,
                                    onCheckedChange = viewModel::onBudgetAlertsToggled,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = OnPrimaryDark,
                                        checkedTrackColor = PrimaryDark,
                                        uncheckedTrackColor = SurfaceContainerHighDark
                                    )
                                )
                            }

                            Divider(color = OutlineVariantDark.copy(alpha = 0.2f))

                            // 5. Export All Data
                            SettingsRow(
                                icon = Icons.Default.FileDownload,
                                title = "Export All Transactions (CSV)",
                                onClick = viewModel::exportAllDataCsv
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = OnSurfaceVariantDark
                                )
                            }
                        }
                    }
                }

                // Log Out Action Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            viewModel.signOut()
                            onLogoutSuccess()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = ErrorDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log Out",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = ErrorDark
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    }

    Row(
        modifier = rowModifier,
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceDark
            )
        }

        trailingContent()
    }
}
