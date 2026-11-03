package com.example.ui.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    private val decimalFormat = DecimalFormat("#,##,##0.00")
    private val compactFormat = DecimalFormat("#,##,##0")

    fun format(amount: Double, symbol: String = "₹", compact: Boolean = false): String {
        return if (compact && amount % 1.0 == 0.0) {
            "$symbol${compactFormat.format(amount)}"
        } else {
            "$symbol${decimalFormat.format(amount)}"
        }
    }
}

object DateTimeUtils {
    private val fullDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val csvDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun formatDate(timeMillis: Long): String {
        val now = Calendar.getInstance()
        val expenseCal = Calendar.getInstance().apply { timeInMillis = timeMillis }

        return when {
            isSameDay(now, expenseCal) -> "Today"
            isYesterday(now, expenseCal) -> "Yesterday"
            else -> fullDateFormat.format(Date(timeMillis))
        }
    }

    fun formatExactDate(timeMillis: Long): String {
        return fullDateFormat.format(Date(timeMillis))
    }

    fun formatMonthYear(timeMillis: Long): String {
        return monthYearFormat.format(Date(timeMillis))
    }

    fun formatCsvDate(timeMillis: Long): String {
        return csvDateFormat.format(Date(timeMillis))
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, target: Calendar): Boolean {
        val copy = now.clone() as Calendar
        copy.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(copy, target)
    }

    fun getStartOfDay(timeMillis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfDay(timeMillis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun getStartOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun getDaysLeftInCurrentMonth(): Int {
        val now = Calendar.getInstance()
        val maxDays = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        return (maxDays - currentDay + 1).coerceAtLeast(1)
    }
}
