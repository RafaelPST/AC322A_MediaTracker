package com.betopompolo.mediatracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BookTest {
    private fun book(currentPage: Int = 0, totalPages: Int = 200) = Book(
        id = "id",
        title = "Title",
        author = "Author",
        synopsis = "Synopsis",
        totalPages = totalPages,
        currentPage = currentPage
    )

    @Test
    fun progressPercentage_isComputedFromCurrentAndTotalPages() {
        assertEquals(50, book(currentPage = 100).progressPercentage)
    }

    @Test
    fun progressPercentage_isZeroWhenBookHasNoPages() {
        assertEquals(0, book(totalPages = 0).progressPercentage)
    }

    @Test
    fun nextPage_advancesUntilLastPage() {
        assertEquals(1, book().nextPage()?.currentPage)
        assertNull(book(currentPage = 200).nextPage())
    }

    @Test
    fun previousPage_goesBackUntilFirstPage() {
        assertEquals(9, book(currentPage = 10).previousPage()?.currentPage)
        assertNull(book(currentPage = 0).previousPage())
    }

    @Test(expected = IllegalArgumentException::class)
    fun currentPage_cannotExceedTotalPages() {
        book(currentPage = 201)
    }

    @Test
    fun withPageDelta_staysInsideValidRange() {
        assertEquals(200, book(currentPage = 195).withPageDelta(10).currentPage)
        assertEquals(0, book(currentPage = 3).withPageDelta(-10).currentPage)
    }

    @Test
    fun isFinished_onlyOnLastPage() {
        assertEquals(true, book(currentPage = 200).isFinished)
        assertEquals(false, book(currentPage = 199).isFinished)
    }

    @Test
    fun optionalFields_defaultToNull() {
        val b = book()
        assertNull(b.publisher)
        assertNull(b.rating)
        assertNull(b.coverRes)
    }
}
