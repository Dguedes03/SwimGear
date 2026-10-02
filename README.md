# SwimGear · Equipamentos de natação

Aplicativo Android em **Kotlin** para organizar os equipamentos de natação e preparar a mochila de treino. O mesmo projeto contém a **parcial em Android Views/XML** e a **evolução em Jetpack Compose**.

## Abrir no Android Studio

1. Extraia o ZIP para uma pasta local. Não abra o projeto de dentro do arquivo compactado.
2. Abra o Android Studio e escolha **Open**.
3. Selecione a pasta **SwimGear**, que contém `settings.gradle.kts`. Não selecione somente a pasta `app`.
4. Aguarde a sincronização do Gradle. No primeiro uso, é necessária internet para baixar as bibliotecas.
5. Se o Android Studio solicitar componentes do SDK ou licenças, aceite a instalação pelo **SDK Manager**.
6. Em **Settings > Build, Execution, Deployment > Build Tools > Gradle**, use **Gradle JDK 17 ou 21**. O JDK integrado do Android Studio pode ser usado se for uma dessas versões.
7. Selecione a configuração **app**, escolha um emulador ou celular com **Android 8.0/API 26 ou superior** e clique em **Run ▶**.

Nenhuma alteração no código, conta, chave de API, `.env` ou servidor é necessária. O SDK e o JDK são os pré-requisitos normais de um projeto Android. O caminho do SDK (`local.properties`) é específico de cada computador e é criado pelo Android Studio.

### Versões fixadas

| Componente | Versão |
|---|---|
| Android Gradle Plugin | 8.13.1 |
| Gradle Wrapper | 8.14 |
| Kotlin e plugin Compose | 2.2.20 |
| KSP | 2.2.20-2.0.4 |
| compileSdk / targetSdk | 36 / 36 |
| minSdk | 26 |
| Bytecode Java/Kotlin | 17 |
| Compose BOM | 2025.12.00 |
| Material 3 | Resolvida pelo Compose BOM |
| Activity | 1.12.2 |
| Lifecycle | 2.10.0 |
| Navigation 3 | 1.0.0 |
| Room | 2.8.4 |

Use uma versão do Android Studio compatível com AGP 8.13.1. As versões das bibliotecas estão fixadas para reproduzir a compilação; não é necessário aceitar sugestões de atualização para executar o app.

## O que o aplicativo faz

- Lista os equipamentos com nome, marca, quantidade e situação da mochila.
- Busca por nome, marca ou categoria, ignorando acentos e maiúsculas.
- Filtra todos os itens, pendentes ou prontos para levar.
- Cadastra e edita equipamentos com validação por campo.
- Exibe detalhes e permite marcar/desmarcar **Pronto para o treino**.
- Exclui um item com confirmação.
- Abre o compartilhamento nativo do Android com os dados do equipamento.
- Mantém os equipamentos em banco local e funciona sem internet.
- Preserva busca, filtros, navegação e rascunho durante recriações do Android.
- Usa tema claro/escuro conforme o sistema, rolagem e suporte a fontes maiores.

No primeiro uso da versão Compose, seis equipamentos fictícios são importados uma única vez. Eles são editáveis e removíveis. Se todos forem excluídos, a lista continua vazia mesmo após reiniciar o app; há uma ação explícita para adicionar exemplos novamente. A quantidade representa unidades cadastradas: um par de nadadeiras, por exemplo, pode ser cadastrado como uma unidade.

### Acessar a parcial XML

No menu **⋮**, toque em **Versão XML · parcial**. Essa versão tem duas Activities próprias:

1. `EquipamentosXmlActivity`: lista em `RecyclerView` com dados simulados.
2. `DetalhesXmlActivity`: recebe o ID e o status por `Intent` explícita, exibe os detalhes e atualiza o status com um botão.

O resultado retorna à lista com a Activity Result API. Os estados em memória são preservados na rotação por `Bundle`. A parcial não usa banco e suas alterações não modificam a versão Compose; ao encerrar esse fluxo, os exemplos voltam ao estado original na próxima abertura.

## Arquitetura

```text
Compose / Activity
       ↓ eventos do usuário
ViewModel + StateFlow<UiState> + SavedStateHandle
       ↓ operações suspend / observação Flow
EquipamentoRepository (interface)
       ↓
RoomEquipamentoRepository → DAO → SQLite local
       ↑ alterações reativas voltam à interface
```

`SwimGearApplication` constrói o banco e injeta o repositório manualmente. As telas coletam estados usando `collectAsStateWithLifecycle`. Cada destino possui seu próprio ViewModel, mantido pelo decorador de ViewModels do Navigation 3 e descartado quando a entrada sai da pilha.

O formulário mantém um ID de rascunho estável no `SavedStateHandle`. A gravação usa `@Upsert`, evitando duplicar um cadastro se o mesmo rascunho for retomado. Toques repetidos em salvar são bloqueados durante a operação. Room executa as operações suspend fora da thread principal; as coroutines pertencem ao `viewModelScope`, propagam `CancellationException` e são canceladas com o ViewModel.

O estado salvo do Android cobre rotação e recriação pelo sistema quando o estado foi registrado. Fechar deliberadamente a tarefa ou limpar os dados pode descartar rascunhos ainda não salvos. Os equipamentos já gravados no Room continuam no aparelho até a exclusão, limpeza dos dados ou desinstalação.

### Estrutura

```text
app/src/main/java/com/swimgear/app/
├── MainActivity.kt
├── SwimGearApplication.kt
├── model/                   # Modelo imutável, categorias, validação
├── data/
│   ├── EquipamentoMock.kt
│   └── local/               # Entity, DAO, banco Room e mapeamento
├── repository/              # Interface e implementação Room
├── viewmodel/               # Lista, detalhes e formulário + UiState
└── ui/
    ├── navigation/          # Rotas serializáveis, back stack e entries
    ├── lista/
    ├── detalhes/
    ├── formulario/
    ├── components/
    ├── theme/
    └── legacy/              # Parcial XML com findViewById

app/src/main/res/layout/     # Duas telas XML e item reutilizável
app/src/test/                # Validação, estados e coroutines
app/src/androidTest/         # Persistência Room e fluxo de interface
app/schemas/                # Esquema versionado do banco
docs/                      # Requisitos, roteiro e validação
gradle/wrapper/             # Wrapper completo, incluindo o JAR
```

## Bibliotecas e por que foram usadas

| Biblioteca | Uso |
|---|---|
| AndroidX Core / Activity | Integração com Android, insets e Activity em Compose. |
| AppCompat / RecyclerView | Telas da parcial XML e lista eficiente. As Views são ligadas ao Kotlin com `findViewById`. |
| Compose UI / Foundation / Material 3 | Interface declarativa, listas, campos, diálogos, tema e acessibilidade. |
| Material Icons Core | Ícones de navegação e ações. Os desenhos de equipamentos são vetores locais. |
| Lifecycle / ViewModel / SavedState | Estado por tela, rascunhos e coleta consciente do ciclo de vida. |
| Navigation 3 Runtime / UI | Rotas `NavKey`, `NavDisplay`, `entryProvider` e pilha salva. |
| Lifecycle ViewModel Navigation 3 | Escopo e descarte de ViewModels por entrada da navegação. |
| kotlinx.serialization | Serialização das rotas para salvar a pilha. |
| kotlinx.coroutines | Operações assíncronas, StateFlow e cancelamento estruturado. |
| Room Runtime / KTX / Compiler + KSP | Banco SQLite, consultas reativas e geração de código dos DAOs. |
| JUnit / coroutines-test / AndroidX Test / Compose UI Test | Testes de validação, ViewModels, Room e interface. Usadas só em testes. |

Não há API externa nem permissão de internet no aplicativo. As dependências são obtidas dos repositórios oficiais Google Maven, Maven Central e Gradle Plugin Portal durante a compilação. Os dados não são sincronizados entre aparelhos; o backup automático está desativado.

## Compilar e testar pelo terminal

No Windows, com o JDK e SDK configurados, execute na pasta do projeto:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest lintDebug
# Requer um dispositivo/emulador conectado:
.\gradlew.bat connectedDebugAndroidTest
```

No macOS/Linux, use `chmod +x gradlew` uma vez e depois `./gradlew` nos mesmos comandos. O APK de desenvolvimento é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

Os testes locais cobrem validação, busca/filtros, leitura com erro e recuperação, preservação do rascunho, erro de escrita, clique duplo, identificador estável e cancelamento. Os testes instrumentados verificam o banco após reabertura e o fluxo de cadastro, recriação, edição, status e exclusão. Consulte [o registro de validação](docs/VALIDACAO.md) para distinguir o que foi efetivamente executado.

### Se aparecer um problema ao abrir

- **JDK incompatível:** selecione JDK 17 ou 21 nas configurações do Gradle.
- **SDK 36 ausente:** instale Android SDK Platform 36 e Build Tools 35.0.0 ou a versão solicitada pelo Gradle pelo SDK Manager.
- **Falha ao baixar dependências:** confira a conexão/proxy e desative Gradle Offline Mode na primeira sincronização.
- **Pouca memória:** feche emuladores desnecessários e sincronize novamente. A compilação já limita os workers; para uma máquina com pouca RAM, o comando pode receber `--max-workers 1`.
- **Projeto não reconhecido:** abra a pasta `SwimGear`, no nível do arquivo `settings.gradle.kts`.

## Entrega e apresentação

O enunciado pede **o link de um repositório GitHub**. Este ZIP contém os arquivos para montar esse repositório; ele não publica nada automaticamente. Suba o conteúdo da pasta `SwimGear`, mantendo o `.gitignore`, incluindo o wrapper e o esquema Room. Não envie `local.properties`, pastas `build`, `.gradle`, chaves ou arquivos pessoais.

Leia [o mapa de requisitos](docs/REQUISITOS.md) e [o roteiro de apresentação](docs/ROTEIRO.md). O estudante deve estudar e conseguir explicar as decisões técnicas e o funcionamento do código antes da entrega.

## Referências oficiais

- [Navigation 3: conceitos, entries e back stack](https://developer.android.com/guide/navigation/navigation-3/basics)
- [Navigation 3: preservação de estado](https://developer.android.com/guide/navigation/navigation-3/save-state)
- [Room](https://developer.android.com/training/data-storage/room)
- [Compatibilidade do Android Gradle Plugin](https://developer.android.com/build/releases/about-agp)

As referências orientam a arquitetura. As versões realmente utilizadas estão fixadas nos arquivos Gradle deste projeto.
