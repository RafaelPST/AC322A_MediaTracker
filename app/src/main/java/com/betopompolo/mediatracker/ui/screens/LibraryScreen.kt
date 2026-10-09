package com.betopompolo.mediatracker.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.betopompolo.mediatracker.Book
import com.betopompolo.mediatracker.HomeActivity
import com.betopompolo.mediatracker.LibraryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onAddClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MediaTracker") },
                actions = {
                    // Abre a versão da etapa Parcial (Android Views/XML) por Intent explícita.
                    TextButton(onClick = {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }) { Text("Versão Views") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddClick) { Text("Adicionar livro") }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                LibraryUiState.Loading -> LoadingContent()
                LibraryUiState.Empty -> MessageContent(
                    title = "Sua biblioteca está vazia",
                    message = "Adicione seu primeiro livro para começar a acompanhar a leitura.",
                    actionLabel = "Adicionar livro",
                    onAction = onAddClick
                )
                is LibraryUiState.Error -> MessageContent(
                    title = "Ocorreu um erro",
                    message = state.message,
                    actionLabel = "Tentar novamente",
                    onAction = onRetry
                )
                is LibraryUiState.Content -> BookList(state.books, onBookClick)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = "Carregando biblioteca" }
        )
        Spacer(Modifier.height(12.dp))
        Text("Carregando biblioteca…")
    }
}

@Composable
private fun MessageContent(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(message, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
private fun BookList(books: List<Book>, onBookClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(book = book, onClick = { onBookClick(book.id) })
        }
    }
}

@Composable
private fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Abrir detalhes de ${book.title}", onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${book.author} • ${book.genres.firstOrNull() ?: "Sem gênero"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Página ${book.currentPage} de ${book.totalPages} • ${book.progressPercentage}%",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { book.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}
