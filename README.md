# Media Tracker

Aplicativo Android para **acompanhar a leitura de livros**. O usuário cadastra livros, vê a biblioteca em uma lista, abre os detalhes de cada um e registra o progresso de leitura (página atual / total / porcentagem). Tudo fica salvo no aparelho e permanece depois de fechar e abrir o app.

O projeto cobre as duas etapas da disciplina:

- **Parcial:** duas telas em Android Views (XML) com navegação por `Intent` e dados simulados.
- **Etapa 2:** interface em Jetpack Compose com Navigation 3, ViewModel, Repository e persistência local.

Ao abrir o app você entra na versão da **Etapa 2** (Compose). Pelo botão **"Versão Views"** na barra superior abre-se a versão da **etapa Parcial** (Views/XML), por `Intent` explícita.

## Como rodar localmente

Pré-requisitos:
- **Android Studio** recente (compatível com Android Gradle Plugin 9.3.1)
- **JDK 17+** (o Android Studio já traz um JDK embutido)
- Android SDK com a plataforma **API 37** (o Android Studio oferece instalar automaticamente)
- Emulador ou celular com **Android 13 (API 33) ou superior**
- Conexão com a internet no primeiro *sync* (para baixar as dependências do Gradle)

Passos:
1. Clone o repositório:
   ```bash
   git clone https://github.com/RafaelPST/AC322A_MediaTracker.git
   ```
2. Abra a pasta no Android Studio (**File > Open**) e aguarde o *Gradle Sync* terminar.
3. Selecione um emulador/dispositivo e clique em **Run ▶** (módulo `app`).

Pela linha de comando (com `ANDROID_HOME` configurado):
```bash
./gradlew assembleDebug      # APK em app/build/outputs/apk/debug
./gradlew testDebugUnitTest  # testes unitários
```

Não há chaves de API, senhas nem arquivos `.env`: o app usa apenas dados locais. Na primeira execução a biblioteca é criada com alguns livros de exemplo.

## Funcionalidades

- Lista de livros (`LazyColumn`) com barra de progresso por livro.
- Cadastro de livro com **validação por campo** (título, autor, gênero e total de páginas) e mensagens de erro.
- Detalhes do livro: sinopse, progresso, botões de −1, +1, +10 páginas e "Concluir", além de excluir (com confirmação).
- **Estados de tela distintos:** carregando, conteúdo, lista vazia e erro (com "Tentar novamente").
- **Persistência local** em arquivo JSON privado do app; os dados sobrevivem a reiniciar o aplicativo.
- Estado preservado após rotação: back stack (Navigation 3), `ViewModel`, campos do formulário (`rememberSaveable`) e posição da lista.
- Acessibilidade: descrições de conteúdo, rótulos de ação nos itens clicáveis, áreas de toque padrão do Material 3, telas roláveis para textos maiores e erros também em texto (não só em cor).

## Requisitos da disciplina

### Parcial (Views/XML)
| Requisito | Onde |
|---|---|
| Duas telas em Views/XML | `activity_home.xml` (lista) e `book_detail_layout.xml` (detalhes) |
| Navegação por `Intent` explícita com dados | `HomeActivity` → `BookDetailActivity` (extra `book_id`) |
| ViewBinding + interação que atualiza a UI | Botões de página anterior/próxima atualizam texto, porcentagem e barra |
| `data class` imutável e opcionais | `Book` (`publisher`, `firstPublished`, `rating`, `coverRes` são `null`-áveis) |
| Dados simulados | `MockBooks.kt` |
| Opcional: somente ViewBinding | Não há `findViewById` |
| Opcional: componente XML reutilizável | `badge_layout.xml`, inflado na lista e no detalhe |

### Etapa 2 (Compose)
| Requisito | Onde |
|---|---|
| Navigation 3 (rotas, entries, back stack, argumentos) | `ui/screens/LibraryApp.kt` (`LibraryRoute`, `AddBookRoute`, `BookDetailRoute(bookId)`) |
| Lista com `LazyColumn` | `ui/screens/LibraryScreen.kt` |
| Formulário com validação e feedback | `ui/screens/AddBookScreen.kt` + `BookFormValidator.kt` |
| `ViewModel`, `UiState`/`StateFlow`, `collectAsStateWithLifecycle`, fluxo unidirecional | `LibraryViewModel.kt` e `LibraryApp.kt` |
| Estados de carregamento, conteúdo, vazio e erro | `LibraryUiState` e `LibraryScreen.kt` |
| `Repository` | `BookRepository` (interface) e `FileBookRepository` |
| Operação assíncrona com coroutines, `scope` e cancelamento | `viewModelScope`, `Dispatchers.IO` no arquivo, recarga cancela a anterior (`loadJob`), `Mutex` nas gravações |
| Persistência local com exposição reativa | arquivo JSON + `Flow<List<Book>>` |
| Sem segredos no código | Nenhum; `.env` e chaves estão no `.gitignore` |
| Opcional: injeção manual de dependências e fakes | `LibraryViewModelFactory`; `FakeBookRepository` nos testes |
| Opcional: testes automatizados | `app/src/test` (modelo, validação, repositório e ViewModel) |
| Opcional: boas práticas de segurança | Entradas validadas; nada é registrado no Logcat |

## Arquitetura

```
UI (Compose)  ──►  LibraryViewModel  ──►  BookRepository (interface)  ──►  FileBookRepository ──► books.json
 ui/screens        StateFlow<UiState>                                       Dispatchers.IO + Mutex
```

- A UI **só observa estados** (`StateFlow`) e **envia eventos** ao ViewModel (fluxo unidirecional).
- O ViewModel depende da interface `BookRepository`, o que permite testá-lo com um repositório falso.
- `Book` é uma `data class` imutável e serializável; o mesmo modelo serve às telas em Views e em Compose.

## Estrutura

```
app/src/main/java/com/betopompolo/mediatracker/
├── MainActivity.kt           # entrada do app (Compose)
├── Book.kt                   # modelo imutável + regras de progresso
├── MockBooks.kt              # dados simulados (Parcial e dados iniciais)
├── BookRepository.kt         # interface da camada de dados
├── FileBookRepository.kt     # persistência em JSON
├── BookFormValidator.kt      # validação do formulário
├── LibraryViewModel.kt       # UiState, ViewModel e factory
├── HomeActivity.kt           # Parcial: lista (Views/XML)
├── BookDetailActivity.kt     # Parcial: detalhes (Views/XML)
└── ui/
    ├── screens/              # telas Compose e navegação
    └── theme/                # tema Material 3
app/src/test/                 # testes unitários
```

## Bibliotecas externas

- **Jetpack Compose (BOM, Material 3, UI, tooling)**: interface declarativa e componentes Material 3.
- **Activity Compose / Activity KTX**: `setContent`, `enableEdgeToEdge` e `viewModels`.
- **Navigation 3 (`navigation3-runtime`, `navigation3-ui`)**: rotas tipadas, back stack e `NavDisplay`.
- **Lifecycle (`runtime-ktx`, `runtime-compose`, `viewmodel-compose`)**: `ViewModel`, `viewModelScope` e `collectAsStateWithLifecycle`.
- **Kotlinx Serialization JSON**: grava e lê a biblioteca em JSON (e torna as rotas do Navigation 3 serializáveis).
- **Kotlin Coroutines**: operações assíncronas (transitiva do Lifecycle); **`kotlinx-coroutines-test`** nos testes do ViewModel.
- **AndroidX AppCompat / Core KTX, Material Components, ConstraintLayout e RecyclerView**: telas em Views/XML da etapa Parcial (toolbar, FAB, indicador de progresso, lista).
- **JUnit 4**: testes unitários. **Espresso / AndroidX Test / Compose UI Test**: dependências de testes instrumentados.

## Recursos

- [Avaliação da disciplina](docs/Avaliação.md)
- [App design (Figma)](https://www.figma.com/design/8AUmcU78PiL6MBnXBysYWR/Programa%C3%A7%C3%A3o-Mobile-1?node-id=0-1&t=8ebYRjctd567bEle-1)
- [Material3 design tokens](https://m3.material.io/foundations/design-tokens/overview)
- [Android developer (views)](https://developer.android.com/develop/ui/views/layout/declaring-layout?hl=pt-br)
