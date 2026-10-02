# Mapa dos requisitos

Todos os caminhos abaixo são relativos à raiz do projeto. A base Kotlin é `app/src/main/java/com/swimgear/app/`.

## Parcial · Android Views

| Requisito | Onde verificar |
|---|---|
| Duas telas XML | `res/layout/activity_equipamentos_xml.xml` e `activity_detalhes_xml.xml`; Activities em `ui/legacy/`. |
| TextView, ImageView e Space | `res/layout/item_equipamento.xml`. |
| LinearLayout, FrameLayout e RecyclerView | Layouts da lista e do item. |
| Intent explícita com dados | `EquipamentosXmlActivity`: classe destino + `EXTRA_ID` e `EXTRA_PRONTO`. |
| Conexão Views/Kotlin | `findViewById` com tipos explícitos nas Activities XML e no adapter. |
| Interação que atualiza UI | Botão de status em `DetalhesXmlActivity`; resultado atualiza a lista. |
| Modelo imutável e opcionais | `model/Equipamento.kt`: `data class`, propriedades `val`, `marca`/`descricao` anuláveis. |
| Mocks sem banco/API | `data/EquipamentoMock.kt`; a parcial usa apenas esses valores em memória. |
| Componente XML reutilizado (opcional) | Item inflado pelo `EquipamentoAdapter`, um `ListAdapter` com `DiffUtil`. |

## Etapa 2 · Compose

| Requisito | Onde verificar |
|---|---|
| Tema e fluxo coerentes | Lista → detalhes → status / lista → cadastro → gravação → lista. |
| Acessibilidade | Material 3, descrição das ações, texto para status além da cor, campos com rótulos/erros, alvos de ao menos 48dp e telas roláveis. |
| Navigation 3 | `ui/navigation/SwimGearNav.kt`: `NavKey`, `@Serializable`, `rememberNavBackStack`, `entryProvider`, `NavDisplay`, argumento ID e decoradores. |
| Lista LazyColumn/LazyRow | `ui/lista/ListaScreen.kt`: equipamentos e filtros. |
| Formulário e validação | `ui/formulario/FormularioScreen.kt`, `model/ValidacaoEquipamento.kt`. |
| ViewModel + StateFlow + UiState | Classes em `viewmodel/`; telas usam `collectAsStateWithLifecycle`. |
| Fluxo unidirecional | UI envia eventos para o ViewModel; observa estados imutáveis; não acessa DAO. |
| Carregamento, conteúdo, vazio e erro | `ListaUiState`/`Fase` e `ListaScreen`; erros de operação em Snackbar e opção de tentar novamente. |
| Estado após recriação | `SavedStateHandle` de busca/filtro/rascunho; back stack serializada; estado Room; rolagem Compose. |
| Repository | Interface e implementação em `repository/`. |
| Operação assíncrona relevante | Consultar, gravar, editar e excluir no Room; coroutines em `viewModelScope`; cancelamento propagado. |
| Persistência local reativa | Room e SQLite em `data/local/`; DAO expõe `Flow`; esquema exportado em `app/schemas/`. |
| Ausência de secrets | Não usa conta, credenciais, API externa nem `.env`; `.gitignore` exclui arquivos locais e chaves. |
| Fluxo completo persistente | Cadastrar → listar → abrir detalhes → marcar pronto → reabrir o app e conferir. |
| README e bibliotecas | `README.md`. |

## Opcionais implementados

- Testes de regras de negócio, ViewModel, persistência Room e interface Compose.
- Injeção manual, interface do Repository e fake de testes.
- Operação offline com banco local; nenhuma conexão necessária durante o uso.
- Tema escuro, conteúdo com largura limitada em tablets, textos ajustáveis e rolagem.
- Compartilhamento nativo pelo seletor do Android.

Cache de API, sincronização por WorkManager, login, fotos e backend não fazem parte deste fluxo local e não são necessários para os requisitos obrigatórios. Os estados de erro são tratados para falhas reais de leitura/gravação; a simulação determinística deles está nos testes com fake.
