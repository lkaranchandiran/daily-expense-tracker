package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ExpenseDao
import com.example.data.local.UserPreferences
import com.example.data.model.Expense
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExpenseTrackerRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var repository: ExpenseRepository
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        expenseDao = db.expenseDao()
        userPreferences = UserPreferences(context)
        repository = ExpenseRepository(expenseDao, userPreferences)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveExpense() = runBlocking {
        val expense = Expense(
            amount = 350.0,
            category = "Food",
            description = "Lunch at cafe",
            dateMillis = System.currentTimeMillis()
        )

        val id = repository.insertExpense(expense)
        assertTrue(id > 0)

        val retrieved = repository.getExpenseById(id)
        assertNotNull(retrieved)
        assertEquals(350.0, retrieved!!.amount, 0.001)
        assertEquals("Food", retrieved.category)
        assertEquals("Lunch at cafe", retrieved.description)

        val all = repository.allExpenses.first()
        assertEquals(1, all.size)
        assertEquals(id, all[0].id)
    }

    @Test
    fun testUpdateExpense() = runBlocking {
        val expense = Expense(
            amount = 100.0,
            category = "Travel",
            description = "Bus ticket",
            dateMillis = System.currentTimeMillis()
        )
        val id = repository.insertExpense(expense)

        // Update amount and description
        val updatedExpense = Expense(
            id = id,
            amount = 150.0,
            category = "Travel",
            description = "Metro and bus ticket",
            dateMillis = expense.dateMillis
        )
        repository.updateExpense(updatedExpense)

        val retrieved = repository.getExpenseById(id)
        assertNotNull(retrieved)
        assertEquals(150.0, retrieved!!.amount, 0.001)
        assertEquals("Metro and bus ticket", retrieved.description)
    }

    @Test
    fun testDeleteExpense() = runBlocking {
        val expense = Expense(
            amount = 899.0,
            category = "Shopping",
            description = "T-shirt",
            dateMillis = System.currentTimeMillis()
        )
        val id = repository.insertExpense(expense)

        val saved = repository.getExpenseById(id)
        assertNotNull(saved)

        repository.deleteExpense(saved!!)
        val afterDelete = repository.getExpenseById(id)
        assertNull(afterDelete)

        val all = repository.allExpenses.first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun testClearAllExpenses() = runBlocking {
        repository.insertExpense(Expense(amount = 100.0, category = "Food"))
        repository.insertExpense(Expense(amount = 200.0, category = "Travel"))
        repository.insertExpense(Expense(amount = 300.0, category = "Bills"))

        val initialList = repository.allExpenses.first()
        assertEquals(3, initialList.size)

        repository.clearAllExpenses()

        val afterClear = repository.allExpenses.first()
        assertEquals(0, afterClear.size)
    }

    @Test
    fun testBudgetPreferencesPersistence() {
        userPreferences.setMonthlyBudget(35000.0)
        val savedBudget = userPreferences.getSavedBudget()
        assertEquals(35000.0, savedBudget, 0.001)

        // Simulate reading again
        val reloadedPrefs = UserPreferences(context)
        assertEquals(35000.0, reloadedPrefs.getSavedBudget(), 0.001)
    }
}
