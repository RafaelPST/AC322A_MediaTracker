package com.betopompolo.mediatracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.betopompolo.mediatracker.ui.screens.LibraryApp
import com.betopompolo.mediatracker.ui.theme.MediaTrackerTheme

/** Entrada do app (Etapa 2): interface em Jetpack Compose com Navigation 3. */
class MainActivity : ComponentActivity() {
    private val viewModel: LibraryViewModel by viewModels { LibraryViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaTrackerTheme {
                LibraryApp(viewModel)
            }
        }
    }
}
