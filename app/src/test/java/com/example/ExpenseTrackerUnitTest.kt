package com.example

import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.ui.util.CsvExporter
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExpenseTrackerUnitTest {

    @Test
    fun testCategoryFromString() {
        assertEquals(ExpenseCategory.GROCERIES, ExpenseCategory.fromString("Groceries"))
        assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromString("food"))
        assertEquals(ExpenseCategory.TRAVEL, ExpenseCategory.fromString("TRAVEL"))
        assertEquals(ExpenseCategory.SHOPPING, ExpenseCategory.fromString("Shopping"))
        assertEquals(ExpenseCategory.BILLS, ExpenseCategory.fromString("Bills"))
        assertEquals(ExpenseCategory.ENTERTAINMENT, ExpenseCategory.fromString("Entertainment"))
        assertEquals(ExpenseCategory.HEALTH, ExpenseCategory.fromString("Health"))
        assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString("Other"))
        assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString("UnknownCategory"))
        assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString(null))
    }

    @Test
    fun testCurrencyFormatting() {
        val formattedStandard = CurrencyFormatter.format(1250.50, "₹")
        assertTrue("Formatted standard contains 1,250.50: $formattedStandard", formattedStandard.contains("1,250.50"))

        val formattedCompact = CurrencyFormatter.format(5000.0, "₹", compact = true)
        assertTrue("Formatted compact contains 5,000: $formattedCompact", formattedCompact.contains("5,000"))
        assertTrue("Contains rupee symbol", formattedCompact.startsWith("₹"))
    }

    @Test
    fun testDateTimeUtilsStartAndEndOfDay() {
        val now = System.currentTimeMillis()
        val startOfDay = DateTimeUtils.getStartOfDay(now)
        val endOfDay = DateTimeUtils.getEndOfDay(now)

        assertTrue(startOfDay <= now)
        assertTrue(endOfDay >= now)
        assertTrue(endOfDay > startOfDay)

        val cal = Calendar.getInstance().apply { timeInMillis = startOfDay }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
    }

    @Test
    fun testDateTimeUtilsMonthBoundaries() {
        val startOfJan = DateTimeUtils.getStartOfMonth(2026, Calendar.JANUARY)
        val endOfJan = DateTimeUtils.getEndOfMonth(2026, Calendar.JANUARY)

        val calStart = Calendar.getInstance().apply { timeInMillis = startOfJan }
        assertEquals(1, calStart.get(Calendar.DAY_OF_MONTH))

        val calEnd = Calendar.getInstance().apply { timeInMillis = endOfJan }
        assertEquals(31, calEnd.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, calEnd.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun testCsvExporter() {
        val testExpenses = listOf(
            Expense(id = 1L, amount = 450.0, category = "Groceries", description = "Fresh apples, bananas", dateMillis = 1700000000000L),
            Expense(id = 2L, amount = 1200.0, category = "Bills", description = "Electricity \"Oct\"", dateMillis = 1700086400000L)
        )

        val csv = CsvExporter.generateCsv(testExpenses, "₹")
        assertTrue(csv.contains("ID,Date,Category,Amount (₹),Description"))
        assertTrue(csv.contains("1,"))
        assertTrue(csv.contains("Groceries"))
        assertTrue(csv.contains("450.0"))
        assertTrue(csv.contains("\"Fresh apples, bananas\""))
        assertTrue(csv.contains("Electricity \"\"Oct\"\""))
    }

    @Test
    fun testBudgetCalculations() {
        val monthlyBudget = 25000.0
        val spentTotal = 15000.0
        val remaining = (monthlyBudget - spentTotal).coerceAtLeast(0.0)
        val progressPercent = (spentTotal / monthlyBudget).toFloat()

        assertEquals(10000.0, remaining, 0.001)
        assertEquals(0.6f, progressPercent, 0.001f)

        // Exceeded budget test
        val spentOver = 30000.0
        val remainingOver = (monthlyBudget - spentOver).coerceAtLeast(0.0)
        assertEquals(0.0, remainingOver, 0.001)
        assertTrue(spentOver > monthlyBudget)
    }
}
