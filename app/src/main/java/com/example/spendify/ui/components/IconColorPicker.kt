package com.example.spendify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconColorPicker(
    selectedIcon: String,
    selectedColorHex: String,
    onIconSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon Picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Icon",
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariantDark
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryIconHelper.availableIcons.forEach { iconName ->
                    val isSelected = iconName == selectedIcon
                    val icon = CategoryIconHelper.getIconByName(iconName)

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) PrimaryDark else SurfaceContainerHighDark.copy(alpha = 0.6f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryDark else GlassBorderDark,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onIconSelected(iconName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = iconName,
                            tint = if (isSelected) OnPrimaryDark else OnSurfaceDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Color Swatch Picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Color",
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariantDark
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryIconHelper.availableColors.forEach { colorHex ->
                    val isSelected = colorHex.equals(selectedColorHex, ignoreCase = true)
                    val parsedColor = CategoryIconHelper.parseColorHex(colorHex)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorHex) }
                    )
                }
            }
        }
    }
}
