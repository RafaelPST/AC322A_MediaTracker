package com.betopompolo.mediatracker

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Modelo imutável de um livro, usado tanto pelas telas em Views (Parcial) quanto pelas
 * telas em Compose (Etapa 2). Campos opcionais (`null`) são tratados na interface.
 * [coverRes] é um recurso local e por isso não é gravado no JSON.
 */
@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val synopsis: String,
    val totalPages: Int,
    val genres: List<String> = emptyList(),
    val publisher: String? = null,
    val firstPublished: String? = null,
    val rating: Double? = null,
    @Transient val coverRes: Int? = null,
    val currentPage: Int = 0
) {
    init {
        require(totalPages >= 0) { "totalPages is not valid" }
        require(currentPage in 0..totalPages) { "currentPage is not valid" }
    }

    val progressPercentage: Int
        get() = if (totalPages == 0) 0 else (currentPage * 100) / totalPages

    val isFinished: Boolean
        get() = totalPages > 0 && currentPage == totalPages

    fun nextPage(): Book? =
        if (currentPage < totalPages) copy(currentPage = currentPage + 1) else null

    fun previousPage(): Book? =
        if (currentPage > 0) copy(currentPage = currentPage - 1) else null

    /** Move a página atual por [delta] páginas, sempre dentro de 0..[totalPages]. */
    fun withPageDelta(delta: Int): Book =
        copy(currentPage = (currentPage + delta).coerceIn(0, totalPages))
}
