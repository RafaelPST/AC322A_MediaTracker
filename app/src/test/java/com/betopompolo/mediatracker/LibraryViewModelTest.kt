package com.betopompolo.mediatracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Repositório falso em memória, usado para testar o ViewModel sem arquivos. */
private class FakeBookRepository(
    initial: List<Book> = emptyList(),
    private val failOnLoad: Boolean = false
) : BookRepository {
    private val state = MutableStateFlow<List<Book>?>(null)
    private val initialBooks = initial

    override val books: Flow<List<Book>> = state.filterNotNull()

    override suspend fun load() {
        if (failOnLoad) throw IllegalStateException("falha simulada")
        state.value = initialBooks
    }

    override suspend fun add(book: Book) {
        state.value = state.value.orEmpty() + book
    }

    override suspend fun update(book: Book) {
        state.value = state.value.orEmpty().map { if (it.id == book.id) book else it }
    }

    override suspend fun delete(id: String) {
        state.value = state.value.orEmpty().filterNot { it.id == id }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun book(id: String, currentPage: Int = 0) = Book(
        id = id,
        title = "Livro $id",
        author = "Autor",
        synopsis = "",
        totalPages = 100,
        currentPage = currentPage
    )

    @Test
    fun emptyRepository_showsEmptyState() {
        val viewModel = LibraryViewModel(FakeBookRepository(emptyList()))
        assertEquals(LibraryUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun repositoryWithBooks_showsContent() {
        val viewModel = LibraryViewModel(FakeBookRepository(listOf(book("1"))))
        assertEquals(LibraryUiState.Content(listOf(book("1"))), viewModel.uiState.value)
    }

    @Test
    fun loadFailure_showsErrorState() {
        val viewModel = LibraryViewModel(FakeBookRepository(failOnLoad = true))
        assertTrue(viewModel.uiState.value is LibraryUiState.Error)
    }

    @Test
    fun invalidForm_doesNotSaveAndExposesErrors() {
        val viewModel = LibraryViewModel(FakeBookRepository(emptyList()))
        viewModel.submit(BookDraft())
        assertTrue(viewModel.formState.value.errors.hasErrors)
        assertEquals(LibraryUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun validForm_addsBookAndMarksAsSaved() {
        val viewModel = LibraryViewModel(FakeBookRepository(emptyList()))
        viewModel.submit(BookDraft("Dune", "Frank Herbert", "Sci-fi", "412", ""))
        assertTrue(viewModel.formState.value.saved)
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertEquals("Dune", state.books.single().title)
    }

    @Test
    fun changePage_updatesProgress_andIsLimitedToTotalPages() {
        val viewModel = LibraryViewModel(FakeBookRepository(listOf(book("1", currentPage = 95))))
        viewModel.changePage("1", 10)
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertEquals(100, state.books.first().currentPage)
    }

    @Test
    fun deleteLastBook_returnsToEmptyState() {
        val viewModel = LibraryViewModel(FakeBookRepository(listOf(book("1"))))
        viewModel.deleteBook("1")
        assertEquals(LibraryUiState.Empty, viewModel.uiState.value)
    }
}
