package com.betopompolo.mediatracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookFormValidatorTest {
    private val valid = BookDraft(
        title = "Dune",
        author = "Frank Herbert",
        genre = "Sci-fi",
        totalPages = "412"
    )

    @Test
    fun validDraft_hasNoErrors() {
        assertFalse(BookFormValidator.validate(valid).hasErrors)
    }

    @Test
    fun blankRequiredFields_areRejected() {
        val errors = BookFormValidator.validate(BookDraft())
        assertNotNull(errors.title)
        assertNotNull(errors.author)
        assertNotNull(errors.genre)
        assertNotNull(errors.totalPages)
        assertTrue(errors.hasErrors)
    }

    @Test
    fun whitespaceOnlyTitle_isRejected() {
        assertNotNull(BookFormValidator.validate(valid.copy(title = "   ")).title)
    }

    @Test
    fun tooLongTitle_isRejected() {
        val errors = BookFormValidator.validate(valid.copy(title = "a".repeat(81)))
        assertNotNull(errors.title)
    }

    @Test
    fun invalidPageCounts_areRejected() {
        assertNotNull(BookFormValidator.validate(valid.copy(totalPages = "0")).totalPages)
        assertNotNull(BookFormValidator.validate(valid.copy(totalPages = "abc")).totalPages)
        assertNotNull(BookFormValidator.validate(valid.copy(totalPages = "10001")).totalPages)
        assertNull(BookFormValidator.validate(valid.copy(totalPages = "10000")).totalPages)
    }

    @Test
    fun toBook_trimsValuesAndStartsAtPageZero() {
        val book = valid.copy(title = "  Dune  ", synopsis = " x ").toBook("id-1")
        assertEquals("Dune", book.title)
        assertEquals("x", book.synopsis)
        assertEquals(412, book.totalPages)
        assertEquals(0, book.currentPage)
        assertEquals(listOf("Sci-fi"), book.genres)
    }
}
