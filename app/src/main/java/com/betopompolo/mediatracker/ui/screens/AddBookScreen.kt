package com.betopompolo.mediatracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.betopompolo.mediatracker.BookDraft
import com.betopompolo.mediatracker.BookFormUiState
import com.betopompolo.mediatracker.BookFormValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    formState: BookFormUiState,
    onSubmit: (BookDraft) -> Unit,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    // rememberSaveable preserva o que foi digitado após rotação/recriação da tela.
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var pages by rememberSaveable { mutableStateOf("") }
    var synopsis by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(formState.saved) {
        if (formState.saved) onSaved()
    }

    val errors = formState.errors

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar livro") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Cancelar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título *") },
                singleLine = true,
                isError = errors.title != null,
                supportingText = { Text(errors.title ?: "${title.length}/${BookFormValidator.MAX_TITLE}") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor *") },
                singleLine = true,
                isError = errors.author != null,
                supportingText = { Text(errors.author ?: "${author.length}/${BookFormValidator.MAX_AUTHOR}") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Gênero *") },
                singleLine = true,
                isError = errors.genre != null,
                supportingText = { Text(errors.genre ?: "${genre.length}/${BookFormValidator.MAX_GENRE}") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pages,
                onValueChange = { value -> pages = value.filter { it.isDigit() }.take(5) },
                label = { Text("Total de páginas *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errors.totalPages != null,
                supportingText = { Text(errors.totalPages ?: "Entre 1 e ${BookFormValidator.MAX_PAGES}") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = synopsis,
                onValueChange = { synopsis = it },
                label = { Text("Sinopse (opcional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            formState.saveError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { onSubmit(BookDraft(title, author, genre, pages, synopsis)) },
                enabled = !formState.saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (formState.saving) "Salvando…" else "Salvar na biblioteca")
            }
            Text("Campos marcados com * são obrigatórios.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
