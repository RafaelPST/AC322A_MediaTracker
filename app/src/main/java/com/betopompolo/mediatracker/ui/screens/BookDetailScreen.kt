package com.betopompolo.mediatracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.betopompolo.mediatracker.Book

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun BookDetailScreen(
    book: Book?,
    actionError: String?,
    onActionErrorShown: () -> Unit,
    onBack: () -> Unit,
    onChangePage: (Int) -> Unit,
    onFinish: () -> Unit,
    onDelete: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(actionError) {
        if (actionError != null) {
            snackbarHostState.showSnackbar(actionError)
            onActionErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (book == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Livro não encontrado", style = MaterialTheme.typography.titleLarge)
                Text("Volte à biblioteca e tente novamente.")
                Button(onClick = onBack) { Text("Voltar à biblioteca") }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(book.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "${book.author} • ${book.genres.joinToString(", ").ifBlank { "Sem gênero" }}",
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()

            Text("Sinopse", style = MaterialTheme.typography.titleLarge)
            Text(book.synopsis.ifBlank { "Nenhuma sinopse foi informada." })

            Spacer(Modifier.height(4.dp))
            Text("Progresso de leitura", style = MaterialTheme.typography.titleLarge)
            Text(
                if (book.isFinished) "Leitura concluída 🎉"
                else "Página ${book.currentPage} de ${book.totalPages} • ${book.progressPercentage}%"
            )
            LinearProgressIndicator(
                progress = { book.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Progresso de leitura: ${book.progressPercentage} por cento"
                    }
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { onChangePage(-1) }, enabled = book.currentPage > 0) {
                    Text("−1 página")
                }
                Button(onClick = { onChangePage(1) }, enabled = book.currentPage < book.totalPages) {
                    Text("+1 página")
                }
                OutlinedButton(onClick = { onChangePage(10) }, enabled = book.currentPage < book.totalPages) {
                    Text("+10 páginas")
                }
                OutlinedButton(onClick = onFinish, enabled = !book.isFinished) {
                    Text("Concluir")
                }
            }
            Text("As alterações são salvas localmente no aparelho.", style = MaterialTheme.typography.bodySmall)

            HorizontalDivider()
            TextButton(onClick = { confirmDelete = true }) {
                Text("Excluir livro", color = MaterialTheme.colorScheme.error)
            }
        }

        if (confirmDelete) {
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text("Excluir livro?") },
                text = { Text("\"${book.title}\" será removido da sua biblioteca.") },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                        onDelete()
                    }) { Text("Excluir") }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
