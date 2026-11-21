package kbtu.hw2.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kbtu.hw2.library.ui.common.AppSnackbar
import kbtu.hw2.library.ui.common.LocalAppSnackbar
import kbtu.hw2.library.ui.navigation.LibraryNavHost
import kbtu.hw2.library.ui.theme.LibraryCatalogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryCatalogTheme {
                val hostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val appSnackbar = remember(hostState, scope) { AppSnackbar(hostState, scope) }

                CompositionLocalProvider(LocalAppSnackbar provides appSnackbar) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LibraryNavHost()
                        SnackbarHost(
                            hostState = hostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}
