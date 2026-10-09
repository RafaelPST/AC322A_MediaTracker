package com.betopompolo.mediatracker

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.betopompolo.mediatracker.databinding.BadgeLayoutBinding
import com.betopompolo.mediatracker.databinding.BookDetailLayoutBinding

/** Tela 2: detalhes do livro recebido por Intent (id) e controle do progresso de leitura. */
class BookDetailActivity : AppCompatActivity() {

    companion object {
        const val BOOK_ID_KEY = "book_id"
        private const val CURRENT_PAGE_KEY = "current_page"
        private const val MAX_BADGES_PER_ROW = 3
    }

    private lateinit var binding: BookDetailLayoutBinding
    private lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selected = intent.getStringExtra(BOOK_ID_KEY)?.let { MockBooks.findById(it) }
        if (selected == null) {
            Toast.makeText(this, R.string.book_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val savedPage = savedInstanceState?.getInt(CURRENT_PAGE_KEY, selected.currentPage)
            ?: selected.currentPage
        book = selected.copy(currentPage = savedPage.coerceIn(0, selected.totalPages))

        binding = BookDetailLayoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bindBook()

        binding.fabPreviousPage.setOnClickListener {
            book.previousPage()?.let { book = it }
            updateReadingProgressViews()
        }
        binding.fabNextPage.setOnClickListener {
            book.nextPage()?.let { book = it }
            updateReadingProgressViews()
        }
        updateReadingProgressViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::book.isInitialized) outState.putInt(CURRENT_PAGE_KEY, book.currentPage)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun bindBook() {
        val notInformed = getString(R.string.not_informed)

        binding.cover.setImageResource(book.coverRes ?: R.drawable.book_cover_placeholder)
        binding.title.text = book.title
        binding.synopsis.text = book.synopsis
        binding.author.text = book.author
        binding.publisher.text = book.publisher ?: notInformed
        binding.firstPublished.text = book.firstPublished ?: notInformed
        binding.genres.text =
            if (book.genres.isEmpty()) notInformed else book.genres.joinToString(", ")

        val badges = buildList {
            add(getString(R.string.book))
            book.genres.firstOrNull()?.let { add(it) }
            add(getString(R.string.pages_count, book.totalPages))
            book.rating?.let { add(it.toString()) }
        }
        createAndLayoutBadges(badges)
    }

    private fun updateReadingProgressViews() {
        binding.readingProgressPage.text =
            getString(R.string.of, book.currentPage, book.totalPages)
        binding.readingProgressPercent.text = getString(R.string.percent, book.progressPercentage)
        binding.readingProgressIndicator.progress = book.progressPercentage
        binding.fabPreviousPage.isEnabled = book.currentPage > 0
        binding.fabNextPage.isEnabled = book.currentPage < book.totalPages
    }

    private fun createAndLayoutBadges(texts: List<String>) {
        texts.take(MAX_BADGES_PER_ROW).forEach { createBadgeView(binding.badgeRow1, it) }
        texts.drop(MAX_BADGES_PER_ROW).take(MAX_BADGES_PER_ROW)
            .forEach { createBadgeView(binding.badgeRow2, it) }
    }

    private fun createBadgeView(row: LinearLayout, text: String) {
        val badgeBinding = BadgeLayoutBinding.inflate(layoutInflater, row, false)
        badgeBinding.badgeText.text = text
        row.addView(badgeBinding.root)
    }
}
