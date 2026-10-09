package com.betopompolo.mediatracker

/** Fonte de dados simulada (mock) usada nesta etapa, sem API nem banco de dados. */
object MockBooks {
    val all: List<Book> = listOf(
        Book(
            id = "lotr-1",
            title = "The Lord of the Rings: The Fellowship of the Ring",
            author = "J.R.R. Tolkien",
            synopsis = "In a sleepy village in the Shire, a young hobbit named Frodo Baggins is entrusted with an immense task: he must make a perilous journey across Middle-earth to the Cracks of Doom, there to destroy the One Ring of Power.",
            totalPages = 423,
            genres = listOf("Fantasy", "Adventure", "Epic"),
            publisher = "George Allen & Unwin",
            firstPublished = "July 29, 1954",
            rating = 9.2,
            coverRes = R.drawable.lotr_book_cover
        ),
        Book(
            id = "dune",
            title = "Dune",
            author = "Frank Herbert",
            synopsis = "Paul Atreides, heir of a noble family, travels to the desert planet Arrakis, the only source of the most valuable substance in the universe.",
            totalPages = 412,
            genres = listOf("Science Fiction", "Adventure"),
            publisher = "Chilton Books",
            firstPublished = "August 1965",
            rating = 9.0
        ),
        Book(
            id = "1984",
            title = "1984",
            author = "George Orwell",
            synopsis = "In a totalitarian state ruled by the Party and its leader Big Brother, Winston Smith secretly rebels against constant surveillance and the rewriting of history.",
            totalPages = 328,
            genres = listOf("Dystopian", "Political Fiction"),
            publisher = "Secker & Warburg",
            firstPublished = "June 8, 1949",
            rating = 8.9
        ),
        Book(
            id = "hobbit",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            synopsis = "Bilbo Baggins, a comfort-loving hobbit, is swept into an expedition to reclaim a treasure guarded by the dragon Smaug.",
            totalPages = 310,
            genres = listOf("Fantasy", "Adventure"),
            publisher = "George Allen & Unwin",
            firstPublished = "September 21, 1937",
            rating = 9.1
        ),
        Book(
            id = "pride",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            synopsis = "Elizabeth Bennet navigates issues of manners, upbringing, morality and marriage in the landed gentry of early 19th-century England.",
            totalPages = 279,
            genres = listOf("Romance", "Classic"),
            firstPublished = "January 28, 1813"
        ),
        Book(
            id = "sapiens",
            title = "Sapiens: A Brief History of Humankind",
            author = "Yuval Noah Harari",
            synopsis = "A survey of the history of humankind, from the Stone Age to the twenty-first century.",
            totalPages = 443,
            genres = listOf("History", "Non-fiction"),
            publisher = "Harvill Secker",
            rating = 8.7
        ),
        Book(
            id = "clean-code",
            title = "Clean Code",
            author = "Robert C. Martin",
            synopsis = "A handbook of agile software craftsmanship with practical advice on writing readable, maintainable code.",
            totalPages = 464,
            genres = listOf("Technology", "Programming"),
            publisher = "Prentice Hall",
            firstPublished = "August 1, 2008",
            rating = 8.8
        ),
        Book(
            id = "little-prince",
            title = "The Little Prince",
            author = "Antoine de Saint-Exupéry",
            synopsis = "A pilot stranded in the desert meets a young prince who has traveled from planet to planet.",
            totalPages = 96,
            genres = listOf("Fable", "Classic"),
            publisher = "Reynal & Hitchcock",
            firstPublished = "April 6, 1943",
            rating = 9.3
        )
    )

    fun findById(id: String): Book? = all.firstOrNull { it.id == id }
}
