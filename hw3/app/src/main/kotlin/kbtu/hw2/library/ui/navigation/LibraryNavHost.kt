package kbtu.hw2.library.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kbtu.hw2.library.ui.AppViewModelFactory
import kbtu.hw2.library.ui.detail.BookDetailScreen
import kbtu.hw2.library.ui.detail.BookDetailViewModel
import kbtu.hw2.library.ui.form.BookFormScreen
import kbtu.hw2.library.ui.form.BookFormViewModel
import kbtu.hw2.library.ui.library.LibraryScreen
import kbtu.hw2.library.ui.library.LibraryViewModel
import kbtu.hw2.library.ui.loans.LoansScreen
import kbtu.hw2.library.ui.loans.LoansViewModel

@Composable
fun LibraryNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val bookIdArgument = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.StringType })

    NavHost(
        navController = navController,
        startDestination = Routes.LIBRARY,
        modifier = modifier,
    ) {
        composable(Routes.LIBRARY) {
            val viewModel: LibraryViewModel = viewModel(factory = AppViewModelFactory.Factory)
            LibraryScreen(
                viewModel = viewModel,
                onOpenBook = { id -> navController.navigate(Routes.book(id)) },
                onAddBook = { navController.navigate(Routes.NEW_BOOK) },
                onOpenLoans = { navController.navigate(Routes.LOANS) },
            )
        }

        composable(Routes.LOANS) {
            val viewModel: LoansViewModel = viewModel(factory = AppViewModelFactory.Factory)
            LoansScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.BOOK, arguments = bookIdArgument) {
            val viewModel: BookDetailViewModel = viewModel(factory = AppViewModelFactory.Factory)
            BookDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.editBook(id)) },
            )
        }

        composable(Routes.NEW_BOOK) {
            val viewModel: BookFormViewModel = viewModel(factory = AppViewModelFactory.Factory)
            BookFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.EDIT_BOOK, arguments = bookIdArgument) {
            val viewModel: BookFormViewModel = viewModel(factory = AppViewModelFactory.Factory)
            BookFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
