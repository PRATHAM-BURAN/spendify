package com.example.spendify.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.InversePrimaryDark
import com.example.spendify.ui.theme.OnPrimaryContainerDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryContainerDark
import com.example.spendify.ui.theme.SurfaceContainerDark

import androidx.compose.material.icons.filled.DateRange

enum class BottomNavTab {
    DASHBOARD,
    CALENDAR,
    HISTORY,
    CHARTS,
    BUDGETS
}

@Composable
fun SpendifyBottomNavBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(84.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating glass bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = PillShape,
                    ambientColor = Color(0x66000000),
                    spotColor = Color(0x66000000)
                )
                .clip(PillShape)
                .background(SurfaceContainerDark.copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            GlassBorderDark.copy(alpha = 0.4f),
                            GlassBorderDark.copy(alpha = 0.1f)
                        )
                    ),
                    shape = PillShape
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Dashboard
                NavTabItem(
                    icon = Icons.Default.Dashboard,
                    isSelected = currentTab == BottomNavTab.DASHBOARD,
                    onClick = { onTabSelected(BottomNavTab.DASHBOARD) }
                )

                // 2. Calendar Tracker
                NavTabItem(
                    icon = Icons.Default.DateRange,
                    isSelected = currentTab == BottomNavTab.CALENDAR,
                    onClick = { onTabSelected(BottomNavTab.CALENDAR) }
                )

                // Empty space for center FAB
                Box(modifier = Modifier.size(54.dp))

                // 3. History
                NavTabItem(
                    icon = Icons.Default.History,
                    isSelected = currentTab == BottomNavTab.HISTORY,
                    onClick = { onTabSelected(BottomNavTab.HISTORY) }
                )

                // 4. Budgets / Wallet
                NavTabItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentTab == BottomNavTab.BUDGETS,
                    onClick = { onTabSelected(BottomNavTab.BUDGETS) }
                )
            }
        }

        // Floating Center FAB elevated slightly above the bar
        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(58.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = PrimaryContainerDark.copy(alpha = 0.6f),
                    spotColor = InversePrimaryDark.copy(alpha = 0.6f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PrimaryContainerDark,
                            InversePrimaryDark
                        )
                    )
                )
                .border(3.dp, SurfaceContainerDark, CircleShape)
                .clickable(onClick = onFabClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Transaction",
                tint = OnPrimaryDark,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tintColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimaryContainerDark else OnSurfaceVariantDark,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navTabTint"
    )

    val backgroundModifier = if (isSelected) {
        Modifier
            .size(48.dp)
            .shadow(8.dp, CircleShape, ambientColor = PrimaryContainerDark.copy(alpha = 0.4f))
            .clip(CircleShape)
            .background(PrimaryContainerDark)
    } else {
        Modifier
            .size(48.dp)
            .clip(CircleShape)
    }

    Box(
        modifier = backgroundModifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
