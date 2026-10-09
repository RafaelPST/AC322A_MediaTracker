package com.betopompolo.mediatracker

import kotlinx.coroutines.flow.Flow

/** Camada de dados. A interface permite trocar a implementação por um fake nos testes. */
interface BookRepository {
    /** Emite a lista atualizada sempre que ela mudar (exposição reativa). */
    val books: Flow<List<Book>>

    /** Carrega os dados do armazenamento local. Lança exceção em caso de falha. */
    suspend fun load()

    suspend fun add(book: Book)
    suspend fun update(book: Book)
    suspend fun delete(id: String)
}
