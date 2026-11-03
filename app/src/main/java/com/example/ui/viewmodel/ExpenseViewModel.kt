package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.repository.ExpenseRepository
import com.example.ui.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class CategoryReportItem(
    val category: ExpenseCategory,
    val totalAmount: Double,
    val count: Int,
    val percentage: Float
)

data class MonthSummary(
    val year: Int,
    val month: Int,
    val totalSpent: Double,
    val count: Int,
    val averageDaily: Double,
    val highestExpense: Expense?,
    val categoryBreakdown: List<CategoryReportItem>
)

data class ExpenseUiState(
    val allExpenses: List<Expense> = emptyList(),
    val recentExpenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val todayTotal: Double = 0.0,
    val thisMonthTotal: Double = 0.0,
    val monthlyBudget: Double = 20000.0,
    val currencySymbol: String = "₹",
    val searchQuery: String = "",
    val selectedCategoryFilter: ExpenseCategory? = null,
    val selectedReportYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedReportMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val monthSummary: MonthSummary? = null,
    val isEntrySheetOpen: Boolean = false,
    val editingExpense: Expense? = null
)

class ExpenseViewModel(
    application: Application,
    private val repository: ExpenseRepository
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<ExpenseCategory?>(null)
    val selectedCategoryFilter: StateFlow<ExpenseCategory?> = _selectedCategoryFilter.asStateFlow()

    private val currentCal = Calendar.getInstance()
    private val _selectedReportYear = MutableStateFlow(currentCal.get(Calendar.YEAR))
    private val _selectedReportMonth = MutableStateFlow(currentCal.get(Calendar.MONTH))

    private val _isEntrySheetOpen = MutableStateFlow(false)
    val isEntrySheetOpen: StateFlow<Boolean> = _isEntrySheetOpen.asStateFlow()

    private val _editingExpense = MutableStateFlow<Expense?>(null)
    val editingExpense: StateFlow<Expense?> = _editingExpense.asStateFlow()

    val uiState: StateFlow<ExpenseUiState> = combine(
        repository.allExpenses,
        repository.monthlyBudget,
        repository.currencySymbol,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedReportYear,
        _selectedReportMonth,
        _isEntrySheetOpen,
        _editingExpense
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val allExpenses = params[0] as List<Expense>
        val monthlyBudget = params[1] as Double
        val currencySymbol = params[2] as String
        val query = params[3] as String
        val categoryFilter = params[4] as ExpenseCategory?
        val reportYear = params[5] as Int
        val reportMonth = params[6] as Int
        val isSheetOpen = params[7] as Boolean
        val editing = params[8] as Expense?

        // Compute today's total
        val startOfToday = DateTimeUtils.getStartOfDay()
        val endOfToday = DateTimeUtils.getEndOfDay()
        val todayTotal = allExpenses
            .filter { it.dateMillis in startOfToday..endOfToday }
            .sumOf { it.amount }

        // Compute current month total
        val nowCal = Calendar.getInstance()
        val startOfThisMonth = DateTimeUtils.getStartOfMonth(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH))
        val endOfThisMonth = DateTimeUtils.getEndOfMonth(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH))
        val thisMonthTotal = allExpenses
            .filter { it.dateMillis in startOfThisMonth..endOfThisMonth }
            .sumOf { it.amount }

        // Filtered expenses for History
        val filtered = allExpenses.filter { expense ->
            val matchesCategory = categoryFilter == null ||
                    ExpenseCategory.fromString(expense.category) == categoryFilter
            val matchesQuery = query.isBlank() ||
                    expense.description.contains(query, ignoreCase = true) ||
                    expense.category.contains(query, ignoreCase = true) ||
                    expense.amount.toString().contains(query)
            matchesCategory && matchesQuery
        }

        // Selected month summary for Reports
        val startOfReportMonth = DateTimeUtils.getStartOfMonth(reportYear, reportMonth)
        val endOfReportMonth = DateTimeUtils.getEndOfMonth(reportYear, reportMonth)
        val reportExpenses = allExpenses.filter { it.dateMillis in startOfReportMonth..endOfReportMonth }
        val reportTotalSpent = reportExpenses.sumOf { it.amount }
        val highest = reportExpenses.maxByOrNull { it.amount }

        val calDays = Calendar.getInstance().apply {
            set(Calendar.YEAR, reportYear)
            set(Calendar.MONTH, reportMonth)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dailyAvg = if (calDays > 0) reportTotalSpent / calDays else 0.0

        val categoryGroups = reportExpenses.groupBy { ExpenseCategory.fromString(it.category) }
        val categoryBreakdown = categoryGroups.map { (cat, list) ->
            val total = list.sumOf { it.amount }
            val pct = if (reportTotalSpent > 0) (total / reportTotalSpent).toFloat() else 0f
            CategoryReportItem(
                category = cat,
                totalAmount = total,
                count = list.size,
                percentage = pct
            )
        }.sortedByDescending { it.totalAmount }

        val monthSummary = MonthSummary(
            year = reportYear,
            month = reportMonth,
            totalSpent = reportTotalSpent,
            count = reportExpenses.size,
            averageDaily = dailyAvg,
            highestExpense = highest,
            categoryBreakdown = categoryBreakdown
        )

        ExpenseUiState(
            allExpenses = allExpenses,
            recentExpenses = allExpenses.take(5),
            filteredExpenses = filtered,
            todayTotal = todayTotal,
            thisMonthTotal = thisMonthTotal,
            monthlyBudget = monthlyBudget,
            currencySymbol = currencySymbol,
            searchQuery = query,
            selectedCategoryFilter = categoryFilter,
            selectedReportYear = reportYear,
            selectedReportMonth = reportMonth,
            monthSummary = monthSummary,
            isEntrySheetOpen = isSheetOpen,
            editingExpense = editing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseUiState()
    )

    fun openAddExpense() {
        _editingExpense.value = null
        _isEntrySheetOpen.value = true
    }

    fun openEditExpense(expense: Expense) {
        _editingExpense.value = expense
        _isEntrySheetOpen.value = true
    }

    fun dismissEntrySheet() {
        _isEntrySheetOpen.value = false
        _editingExpense.value = null
    }

    fun saveExpense(
        amount: Double,
        category: String,
        description: String,
        dateMillis: Long,
        id: Long = 0L
    ) {
        if (amount <= 0.0) return

        viewModelScope.launch {
            if (id == 0L) {
                repository.insertExpense(
                    Expense(
                        amount = amount,
                        category = category,
                        description = description.trim(),
                        dateMillis = dateMillis
                    )
                )
            } else {
                repository.updateExpense(
                    Expense(
                        id = id,
                        amount = amount,
                        category = category,
                        description = description.trim(),
                        dateMillis = dateMillis
                    )
                )
            }
            dismissEntrySheet()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun updateMonthlyBudget(newBudget: Double) {
        if (newBudget > 0) {
            repository.setMonthlyBudget(newBudget)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllExpenses()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ExpenseCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun navigateReportMonth(offset: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _selectedReportYear.value)
            set(Calendar.MONTH, _selectedReportMonth.value)
            add(Calendar.MONTH, offset)
        }
        _selectedReportYear.value = cal.get(Calendar.YEAR)
        _selectedReportMonth.value = cal.get(Calendar.MONTH)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(application)
            val prefs = UserPreferences(application)
            val repo = ExpenseRepository(db.expenseDao(), prefs)
            return ExpenseViewModel(application, repo) as T
        }
    }
}
