package com.betopompolo.mediatracker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Persiste a biblioteca em um arquivo JSON privado do aplicativo.
 * Na primeira execução o arquivo é criado com os dados de [seed].
 * Toda leitura/gravação de arquivo roda em [ioDispatcher] (fora da thread principal).
 */
class FileBookRepository(
    private val file: File,
    private val seed: List<Book> = MockBooks.all,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BookRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val serializer = ListSerializer(Book.serializer())
    private val mutex = Mutex()
    private val state = MutableStateFlow<List<Book>?>(null)

    override val books: Flow<List<Book>> = state.filterNotNull()

    override suspend fun load() {
        mutex.withLock {
            state.value = withContext(ioDispatcher) { readOrSeed() }
        }
    }

    override suspend fun add(book: Book) = mutate { it + book }

    override suspend fun update(book: Book) =
        mutate { list -> list.map { if (it.id == book.id) book else it } }

    override suspend fun delete(id: String) =
        mutate { list -> list.filterNot { it.id == id } }

    private suspend fun mutate(transform: (List<Book>) -> List<Book>) {
        mutex.withLock {
            val current = state.value ?: withContext(ioDispatcher) { readOrSeed() }
            val updated = transform(current)
            withContext(ioDispatcher) { write(updated) }
            state.value = updated
        }
    }

    private fun readOrSeed(): List<Book> {
        if (!file.exists()) {
            write(seed)
            return seed
        }
        return json.decodeFromString(serializer, file.readText())
    }

    private fun write(items: List<Book>) {
        file.parentFile?.mkdirs()
        val temp = File(file.parentFile, file.name + ".tmp")
        temp.writeText(json.encodeToString(serializer, items))
        if (!temp.renameTo(file)) {
            file.writeText(temp.readText())
            temp.delete()
        }
    }
}
