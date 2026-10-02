# Roteiro de demonstração

## 1. Objetivo

“O SwimGear ajuda um nadador a cadastrar seus equipamentos e conferir o que já está separado para o treino. Ele funciona offline e mantém os dados no aparelho.”

## 2. Mostrar o fluxo completo

1. Abra o app e explique os exemplos iniciais e o resumo da mochila.
2. Busque `oculos`, sem acento, e mostre o item `Óculos de natação`.
3. Limpe a busca. Toque em **Adicionar equipamento**.
4. Tente salvar sem nome e mostre a validação. Preencha `Prancha azul`, categoria `Prancha`, quantidade `1` e uma observação.
5. Antes de salvar, gire o aparelho: o rascunho permanece.
6. Salve e abra o item. Marque **Pronto para o treino**.
7. Volte e selecione **Na mochila**. O item aparece e o contador é atualizado.
8. Feche e reabra o app. O equipamento e seu status continuam salvos.
9. Edite a quantidade, volte aos detalhes e confira a mudança.
10. Mostre o compartilhamento; é suficiente abrir o seletor, sem enviar nada.
11. Exclua o item com confirmação. Para mostrar um resultado vazio, faça uma busca inexistente; limpar filtros volta ao conteúdo.

## 3. Mostrar a parcial XML

1. No menu **⋮**, abra **Versão XML · parcial**.
2. Toque em um equipamento para abrir a segunda Activity.
3. Marque/desmarque o status e volte: a lista reflete o resultado.
4. Explique que a parcial usa mocks e não altera o banco da versão Compose.
5. Mostre no código o layout XML, o `findViewById` e a Intent com o ID.

## 4. Explicar as decisões

| Pergunta | Ponto para explicar com suas palavras |
|---|---|
| Por que uma `data class` com `val`? | O modelo é um valor imutável; mudanças geram uma cópia, facilitando observar estados e raciocinar sobre o fluxo. |
| Como os campos opcionais funcionam? | Marca e descrição vazias viram `null`; a UI fornece uma mensagem alternativa. |
| Por que ViewModel? | Separa estado/regras da tela e mantém operações durante mudanças de configuração. |
| ViewModel resolve morte do processo? | Sozinho, não. O rascunho usa SavedStateHandle, a navegação tem estado salvo e os cadastros ficam no Room. |
| Por que Repository? | A interface separa acesso a dados e apresentação e permite testes com fake. |
| Onde está a tarefa assíncrona? | Consultas e gravações suspend no Room, disparadas pelo viewModelScope. Não há espera artificial só para cumprir requisito. |
| Como a lista muda após salvar? | Room emite pelo Flow; o ViewModel atualiza o UiState; Compose recompõe a tela. |
| Como funciona Navigation 3? | A aplicação mantém uma pilha de chaves. Cada entry associa uma rota a uma tela; detalhes e edição recebem o ID. |
| O que acontece se salvar duas vezes? | O botão bloqueia enquanto salva; o rascunho também tem um ID estável e o banco usa upsert. |
| Por que não usar API? | O problema é organizar equipamentos pessoais; persistência local resolve o fluxo obrigatório sem conta ou credenciais. |
| Como mostrar erro com segurança? | Execute os testes que injetam falha no fake e mostram transição para erro, recuperação e preservação do formulário. |

## 5. Verificações manuais úteis

- Tema claro e escuro.
- Fonte do sistema maior, aparelho em retrato e paisagem.
- Botão Voltar do Android e setas do app.
- Edição com teclado aberto e acesso ao botão Salvar por rolagem.
- Cancelar a confirmação de exclusão preserva o equipamento.
- Forçar a parada e reabrir preserva os equipamentos gravados (o rascunho não salvo não é garantido nessa situação).
- Sem conexão, cadastro e alteração continuam funcionando.

Não apresente conceitos apenas de memória: localize os arquivos indicados em `REQUISITOS.md`, rode os testes e explique o código que foi entregue.
