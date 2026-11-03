package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ExpenseEntrySheet
import com.example.ui.screens.BudgetSettingsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.ExpenseTrackerTheme
import com.example.ui.viewmodel.ExpenseViewModel

enum class NavigationTab(
    val title: String,
    val icon: ImageVector,
    val tag: String
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    HISTORY("History", Icons.Default.ReceiptLong, "nav_history"),
    REPORTS("Reports", Icons.Default.PieChart, "nav_reports"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                MainExpenseApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainExpenseApp(viewModel: ExpenseViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle Android system back gesture:
    // If on sub-tabs, return to Dashboard; if in entry sheet, sheet dismisses
    BackHandler(enabled = currentTab != NavigationTab.DASHBOARD) {
        currentTab = NavigationTab.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddExpense() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_expense")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentTab) {
            NavigationTab.DASHBOARD -> {
                DashboardScreen(
                    state = uiState,
                    onAddExpenseClick = { viewModel.openAddExpense() },
                    onExpenseClick = { expense -> viewModel.openEditExpense(expense) },
                    onDeleteExpenseClick = { expense -> viewModel.deleteExpense(expense) },
                    onViewAllClick = { currentTab = NavigationTab.HISTORY },
                    modifier = screenModifier
                )
            }
            NavigationTab.HISTORY -> {
                HistoryScreen(
                    state = uiState,
                    onSearchQueryChange = { query -> viewModel.setSearchQuery(query) },
                    onCategoryFilterChange = { category -> viewModel.setCategoryFilter(category) },
                    onExpenseClick = { expense -> viewModel.openEditExpense(expense) },
                    onDeleteExpense = { expense -> viewModel.deleteExpense(expense) },
                    modifier = screenModifier
                )
            }
            NavigationTab.REPORTS -> {
                ReportsScreen(
                    state = uiState,
                    onNavigateMonth = { offset -> viewModel.navigateReportMonth(offset) },
                    modifier = screenModifier
                )
            }
            NavigationTab.SETTINGS -> {
                BudgetSettingsScreen(
                    state = uiState,
                    onUpdateBudget = { newBudget -> viewModel.updateMonthlyBudget(newBudget) },
                    onClearAllData = { viewModel.clearAllData() },
                    modifier = screenModifier
                )
            }
        }

        // Expense Entry / Edit Sheet
        if (uiState.isEntrySheetOpen) {
            ExpenseEntrySheet(
                sheetState = sheetState,
                editingExpense = uiState.editingExpense,
                currencySymbol = uiState.currencySymbol,
                onDismiss = { viewModel.dismissEntrySheet() },
                onSave = { amount, category, description, dateMillis, id ->
                    viewModel.saveExpense(amount, category, description, dateMillis, id)
                },
                onDelete = { expense ->
                    viewModel.deleteExpense(expense)
                }
            )
        }
    }
}
