package com.example.data.repository

import com.example.data.local.ExpenseDao
import com.example.data.local.UserPreferences
import com.example.data.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val userPreferences: UserPreferences
) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getRecentExpenses(limit: Int = 5): Flow<List<Expense>> = expenseDao.getRecentExpenses(limit)

    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>> =
        expenseDao.getExpensesBetweenDates(startDate, endDate)

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)

    suspend fun clearAllExpenses() = expenseDao.clearAllExpenses()

    val monthlyBudget: StateFlow<Double> = userPreferences.monthlyBudget
    val currencySymbol: StateFlow<String> = userPreferences.currencySymbol

    fun setMonthlyBudget(amount: Double) = userPreferences.setMonthlyBudget(amount)
}
