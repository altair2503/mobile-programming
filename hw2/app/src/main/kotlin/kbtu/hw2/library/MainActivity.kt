package kbtu.hw2.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import kbtu.hw2.library.ui.LibraryScreen
import kbtu.hw2.library.ui.LibraryViewModel
import kbtu.hw2.library.ui.theme.LibraryCatalogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryCatalogTheme {
                val libraryViewModel: LibraryViewModel = viewModel()
                LibraryScreen(viewModel = libraryViewModel)
            }
        }
    }
}
