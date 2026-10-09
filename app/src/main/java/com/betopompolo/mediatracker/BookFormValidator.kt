package com.betopompolo.mediatracker

/** Dados digitados no formulário (tudo como texto, exatamente como o usuário digitou). */
data class BookDraft(
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val totalPages: String = "",
    val synopsis: String = ""
)

/** Mensagem de erro por campo; `null` significa campo válido. */
data class BookFormErrors(
    val title: String? = null,
    val author: String? = null,
    val genre: String? = null,
    val totalPages: String? = null
) {
    val hasErrors: Boolean
        get() = title != null || author != null || genre != null || totalPages != null
}

object BookFormValidator {
    const val MAX_TITLE = 80
    const val MAX_AUTHOR = 60
    const val MAX_GENRE = 40
    const val MAX_PAGES = 10_000

    fun validate(draft: BookDraft): BookFormErrors {
        val title = draft.title.trim()
        val author = draft.author.trim()
        val genre = draft.genre.trim()
        val pages = draft.totalPages.trim().toIntOrNull()

        return BookFormErrors(
            title = when {
                title.isEmpty() -> "Informe o título."
                title.length > MAX_TITLE -> "O título deve ter no máximo $MAX_TITLE caracteres."
                else -> null
            },
            author = when {
                author.isEmpty() -> "Informe o autor."
                author.length > MAX_AUTHOR -> "O autor deve ter no máximo $MAX_AUTHOR caracteres."
                else -> null
            },
            genre = when {
                genre.isEmpty() -> "Informe o gênero."
                genre.length > MAX_GENRE -> "O gênero deve ter no máximo $MAX_GENRE caracteres."
                else -> null
            },
            totalPages = when {
                pages == null -> "Informe o número de páginas."
                pages < 1 || pages > MAX_PAGES -> "Use um valor entre 1 e $MAX_PAGES."
                else -> null
            }
        )
    }
}

/** Converte um rascunho **já validado** em [Book]. */
fun BookDraft.toBook(id: String): Book = Book(
    id = id,
    title = title.trim(),
    author = author.trim(),
    synopsis = synopsis.trim(),
    totalPages = totalPages.trim().toInt(),
    genres = listOf(genre.trim())
)
