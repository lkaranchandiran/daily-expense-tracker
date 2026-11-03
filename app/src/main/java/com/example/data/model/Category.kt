package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color
) {
    GROCERIES("Groceries", Icons.Default.ShoppingCart, Color(0xFF2E7D32)),
    FOOD("Food", Icons.Default.Fastfood, Color(0xFFE65100)),
    TRAVEL("Travel", Icons.Default.DirectionsCar, Color(0xFF0277BD)),
    SHOPPING("Shopping", Icons.Default.ShoppingBag, Color(0xFF8E24AA)),
    BILLS("Bills", Icons.Default.ReceiptLong, Color(0xFFC2185B)),
    ENTERTAINMENT("Entertainment", Icons.Default.Movie, Color(0xFFF57F17)),
    HEALTH("Health", Icons.Default.LocalHospital, Color(0xFFD32F2F)),
    OTHER("Other", Icons.Default.MoreHoriz, Color(0xFF546E7A));

    companion object {
        fun fromString(name: String?): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.title.equals(name, ignoreCase = true) }
                ?: OTHER
        }
    }
}
