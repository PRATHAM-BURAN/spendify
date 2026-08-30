package com.example.spendify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {

    fun getIconByName(iconName: String): ImageVector {
        return when (iconName.lowercase()) {
            "restaurant" -> Icons.Default.Restaurant
            "shopping_cart" -> Icons.Default.ShoppingCart
            "flight_takeoff" -> Icons.Default.FlightTakeoff
            "shopping_bag" -> Icons.Default.ShoppingBag
            "receipt_long" -> Icons.Default.ReceiptLong
            "home" -> Icons.Default.Home
            "account_balance" -> Icons.Default.AccountBalance
            "subscriptions" -> Icons.Default.Subscriptions
            "school" -> Icons.Default.School
            "medical_services" -> Icons.Default.MedicalServices
            "movie" -> Icons.Default.Movie
            "directions_car" -> Icons.Default.DirectionsCar
            "card_giftcard" -> Icons.Default.CardGiftcard
            "payments" -> Icons.Default.Payments
            "laptop_mac" -> Icons.Default.LaptopMac
            "trending_up" -> Icons.Default.TrendingUp
            "storefront" -> Icons.Default.Storefront
            "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
            "local_cafe" -> Icons.Default.LocalCafe
            "electric_bolt" -> Icons.Default.ElectricBolt
            "music_note" -> Icons.Default.MusicNote
            "arrow_upward" -> Icons.Default.ArrowUpward
            "arrow_downward" -> Icons.Default.ArrowDownward
            "credit_card" -> Icons.Default.CreditCard
            else -> Icons.Default.Category
        }
    }

    fun parseColorHex(hex: String, defaultColor: Color = Color(0xFFC0C1FF)): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorInt = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(colorInt or 0x00000000FF000000)
            } else if (cleanHex.length == 8) {
                Color(colorInt)
            } else {
                defaultColor
            }
        } catch (e: Exception) {
            defaultColor
        }
    }

    val availableIcons = listOf(
        "restaurant", "shopping_cart", "flight_takeoff", "shopping_bag",
        "receipt_long", "home", "account_balance", "subscriptions",
        "school", "medical_services", "movie", "directions_car",
        "card_giftcard", "payments", "laptop_mac", "trending_up",
        "storefront", "account_balance_wallet", "local_cafe", "electric_bolt",
        "music_note"
    )

    val availableColors = listOf(
        "#4edea3", "#8083ff", "#ffb95f", "#ffb4ab",
        "#c0c1ff", "#00a572", "#ca8100", "#6ffbbe",
        "#ffddb8", "#908fa0", "#e1e0ff", "#005236"
    )
}
