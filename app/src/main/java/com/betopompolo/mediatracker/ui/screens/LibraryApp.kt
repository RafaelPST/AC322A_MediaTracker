package com.betopompolo.mediatracker.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.betopompolo.mediatracker.LibraryUiState
import com.betopompolo.mediatracker.LibraryViewModel
import kotlinx.serialization.Serializable

/** Rotas do Navigation 3 (chaves serializáveis, o que preserva o back stack após rotação). */
@Serializable
data object LibraryRoute : NavKey

@Serializable
data object AddBookRoute : NavKey

@Serializable
data class BookDetailRoute(val bookId: String) : NavKey

@Composable
fun LibraryApp(viewModel: LibraryViewModel) {
    val backStack = rememberNavBackStack(LibraryRoute)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val actionError by viewModel.actionError.collectAsStateWithLifecycle()

    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { goBack() },
        entryProvider = entryProvider {
            entry<LibraryRoute> {
                LibraryScreen(
                    state = uiState,
                    onAddClick = {
                        viewModel.resetForm()
                        backStack.add(AddBookRoute)
                    },
                    onBookClick = { id -> backStack.add(BookDetailRoute(id)) },
                    onRetry = viewModel::load
                )
            }
            entry<AddBookRoute> {
                AddBookScreen(
                    formState = formState,
                    onSubmit = viewModel::submit,
                    onSaved = {
                        viewModel.resetForm()
                        goBack()
                    },
                    onBack = goBack
                )
            }
            entry<BookDetailRoute> { route ->
                val book = (uiState as? LibraryUiState.Content)?.books
                    ?.firstOrNull { it.id == route.bookId }
                BookDetailScreen(
                    book = book,
                    actionError = actionError,
                    onActionErrorShown = viewModel::clearActionError,
                    onBack = goBack,
                    onChangePage = { delta -> viewModel.changePage(route.bookId, delta) },
                    onFinish = { viewModel.finishBook(route.bookId) },
                    onDelete = {
                        viewModel.deleteBook(route.bookId)
                        goBack()
                    }
                )
            }
        }
    )
}
