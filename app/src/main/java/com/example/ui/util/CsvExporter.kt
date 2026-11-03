package com.example.ui.util

import android.content.Context
import android.content.Intent
import com.example.data.model.Expense

object CsvExporter {

    fun generateCsv(expenses: List<Expense>, currencySymbol: String = "INR"): String {
        val builder = StringBuilder()
        // CSV Header
        builder.append("ID,Date,Category,Amount ($currencySymbol),Description\n")

        for (expense in expenses) {
            val dateStr = DateTimeUtils.formatCsvDate(expense.dateMillis)
            val escapedDescription = "\"" + expense.description.replace("\"", "\"\"") + "\""
            val escapedCategory = "\"" + expense.category.replace("\"", "\"\"") + "\""
            builder.append("${expense.id},$dateStr,$escapedCategory,${expense.amount},$escapedDescription\n")
        }

        return builder.toString()
    }

    fun shareCsv(context: Context, csvData: String, fileName: String = "expenses_export.csv") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvData)
            putExtra(Intent.EXTRA_SUBJECT, "Daily Expense Tracker Export")
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Expenses CSV")
        context.startActivity(shareIntent)
    }
}
