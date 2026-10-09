package com.betopompolo.mediatracker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

/** Estados da lista: carregando, conteúdo, lista vazia e erro. */
sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Content(val books: List<Book>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}

/** Estado do formulário de cadastro. */
data class BookFormUiState(
    val saving: Boolean = false,
    val saved: Boolean = false,
    val errors: BookFormErrors = BookFormErrors(),
    val saveError: String? = null
)

class LibraryViewModel(private val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(BookFormUiState())
    val formState: StateFlow<BookFormUiState> = _formState.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    /** Carrega (ou recarrega) a biblioteca. Uma nova chamada cancela a anterior. */
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            try {
                repository.load()
                repository.books.collect { books ->
                    _uiState.value =
                        if (books.isEmpty()) LibraryUiState.Empty else LibraryUiState.Content(books)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = LibraryUiState.Error("Não foi possível carregar sua biblioteca.")
            }
        }
    }

    fun submit(draft: BookDraft) {
        if (_formState.value.saving) return
        val errors = BookFormValidator.validate(draft)
        if (errors.hasErrors) {
            _formState.value = BookFormUiState(errors = errors)
            return
        }
        _formState.value = BookFormUiState(saving = true)
        viewModelScope.launch {
            try {
                repository.add(draft.toBook(UUID.randomUUID().toString()))
                _formState.value = BookFormUiState(saved = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _formState.value = BookFormUiState(saveError = "Não foi possível salvar. Tente novamente.")
            }
        }
    }

    fun resetForm() {
        _formState.value = BookFormUiState()
    }

    fun changePage(bookId: String, delta: Int) = updateBook(bookId) { it.withPageDelta(delta) }

    fun finishBook(bookId: String) = updateBook(bookId) { it.copy(currentPage = it.totalPages) }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            try {
                repository.delete(bookId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionError.value = "Não foi possível excluir o livro."
            }
        }
    }

    fun clearActionError() {
        _actionError.value = null
    }

    private fun updateBook(bookId: String, transform: (Book) -> Book) {
        val book = (_uiState.value as? LibraryUiState.Content)?.books?.firstOrNull { it.id == bookId }
            ?: return
        viewModelScope.launch {
            try {
                repository.update(transform(book))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionError.value = "Não foi possível salvar o progresso."
            }
        }
    }
}

/** Injeção manual de dependências: cria o ViewModel com o repositório de arquivo. */
class LibraryViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = FileBookRepository(File(appContext.filesDir, "books.json"))
        return LibraryViewModel(repository) as T
    }
}
