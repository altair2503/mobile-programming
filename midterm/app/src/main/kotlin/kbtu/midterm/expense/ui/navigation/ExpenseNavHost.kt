package kbtu.midterm.expense.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kbtu.midterm.expense.ui.ExpenseViewModel
import kbtu.midterm.expense.ui.screens.AddExpenseScreen
import kbtu.midterm.expense.ui.screens.DashboardScreen
import kbtu.midterm.expense.ui.screens.HistoryScreen
import kbtu.midterm.expense.ui.screens.InsightsScreen
import kotlinx.coroutines.launch

@Composable
fun ExpenseNavHost(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topLevelRoutes = setOf(
        ExpenseRoutes.Dashboard,
        ExpenseRoutes.History,
        ExpenseRoutes.Insights,
    )
    val showBottomBar = currentRoute in topLevelRoutes

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage ?: return@LaunchedEffect
        scope.launch {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == ExpenseRoutes.Dashboard,
                        onClick = {
                            navController.navigate(ExpenseRoutes.Dashboard) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                    )
                    NavigationBarItem(
                        selected = currentRoute == ExpenseRoutes.History,
                        onClick = {
                            navController.navigate(ExpenseRoutes.History) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("History") },
                    )
                    NavigationBarItem(
                        selected = currentRoute == ExpenseRoutes.Insights,
                        onClick = {
                            navController.navigate(ExpenseRoutes.Insights) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Insights, contentDescription = null) },
                        label = { Text("Insights") },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ExpenseRoutes.Dashboard,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ExpenseRoutes.Dashboard) {
                DashboardScreen(
                    viewModel = viewModel,
                    monthTotal = uiState.monthTotal,
                    recentExpenses = viewModel.recentExpenses(),
                    onAddClick = { navController.navigate(ExpenseRoutes.Add) },
                    onExpenseClick = { id -> navController.navigate(ExpenseRoutes.edit(id)) },
                )
            }
            composable(ExpenseRoutes.History) {
                HistoryScreen(
                    viewModel = viewModel,
                    expenses = uiState.expenses,
                    onExpenseClick = { id -> navController.navigate(ExpenseRoutes.edit(id)) },
                )
            }
            composable(ExpenseRoutes.Insights) {
                InsightsScreen(
                    summaries = uiState.categorySummaries,
                    monthTotal = uiState.monthTotal,
                    aiInsights = uiState.aiInsights,
                    isAiLoading = uiState.isAiInsightsLoading,
                    onGetAiInsights = viewModel::loadAiInsights,
                )
            }
            composable(ExpenseRoutes.Add) {
                AddExpenseScreen(
                    viewModel = viewModel,
                    expenseId = null,
                    existing = null,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(
                route = ExpenseRoutes.Edit,
                arguments = listOf(navArgument("expenseId") { type = NavType.StringType }),
            ) { entry ->
                val expenseId = entry.arguments?.getString("expenseId")
                val existing = uiState.expenses.find { it.id == expenseId }
                AddExpenseScreen(
                    viewModel = viewModel,
                    expenseId = expenseId,
                    existing = existing,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
        }
    }
}
