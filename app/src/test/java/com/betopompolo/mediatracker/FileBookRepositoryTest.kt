package com.betopompolo.mediatracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileBookRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun book(id: String, currentPage: Int = 0) = Book(
        id = id,
        title = "Livro $id",
        author = "Autor",
        synopsis = "",
        totalPages = 100,
        currentPage = currentPage
    )

    private fun repository(file: File, seed: List<Book> = emptyList()) =
        FileBookRepository(file, seed, Dispatchers.Unconfined)

    @Test
    fun firstLoad_usesSeedData() {
        runBlocking {
            val repo = repository(File(folder.root, "books.json"), seed = listOf(book("seed")))
            repo.load()
            assertEquals(listOf("seed"), repo.books.first().map { it.id })
        }
    }

    @Test
    fun changes_arePersistedAfterRestart() {
        runBlocking {
            val file = File(folder.root, "books.json")
            val first = repository(file)
            first.load()
            first.add(book("1"))
            first.add(book("2"))
            first.update(book("1", currentPage = 40))
            first.delete("2")

            val second = repository(file)
            second.load()
            val books = second.books.first()
            assertEquals(listOf("1"), books.map { it.id })
            assertEquals(40, books.first().currentPage)
        }
    }
}
