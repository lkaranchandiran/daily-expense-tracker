package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("expense_tracker_prefs", Context.MODE_PRIVATE)

    private val _monthlyBudget = MutableStateFlow(getSavedBudget())
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

    private val _currencySymbol = MutableStateFlow(getSavedCurrencySymbol())
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    fun getSavedBudget(): Double {
        val value = prefs.getFloat(KEY_MONTHLY_BUDGET, 20000f)
        return value.toDouble()
    }

    fun setMonthlyBudget(amount: Double) {
        prefs.edit().putFloat(KEY_MONTHLY_BUDGET, amount.toFloat()).apply()
        _monthlyBudget.value = amount
    }

    fun getSavedCurrencySymbol(): String {
        return prefs.getString(KEY_CURRENCY_SYMBOL, "₹") ?: "₹"
    }

    fun setCurrencySymbol(symbol: String) {
        prefs.edit().putString(KEY_CURRENCY_SYMBOL, symbol).apply()
        _currencySymbol.value = symbol
    }

    companion object {
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
    }
}
