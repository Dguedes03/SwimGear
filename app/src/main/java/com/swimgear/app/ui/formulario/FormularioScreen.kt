package com.swimgear.app.ui.formulario

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swimgear.app.model.Categoria
import com.swimgear.app.ui.components.*
import com.swimgear.app.viewmodel.FormularioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(vm: FormularioViewModel, editando: Boolean, voltar: () -> Unit) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val keyboard = LocalSoftwareKeyboardController.current
    var categorias by remember { mutableStateOf(false) }
    MensagemEffect(state.mensagem, snackbar, vm::consumirMensagem)
    ConclusaoEffect(state.salvo, voltar)
    BackHandler(enabled = state.salvando) { }
    Scaffold(topBar = { BarraTitulo(if (editando) "Editar equipamento" else "Novo equipamento", voltar, !state.salvando) },
        snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).imePadding(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                when {
                    state.carregando -> Carregando()
                    state.falhaCarregamento -> EstadoVazio("Não foi possível abrir o item", "Ele pode ter sido excluído ou não estar disponível.", "Tentar novamente", vm::carregar)
                    else -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            EquipamentoIcon(state.categoria, 64.dp)
                            Text(if (editando) "Cada detalhe faz diferença." else "Mais um parceiro para o seu treino.",
                                style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        }
                        Text("Nome e quantidade são obrigatórios. Os outros detalhes ficam por sua conta.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Campo(state.nome, vm::nome, "Nome do equipamento *", state.erros.nome, !state.salvando,
                            hint = "Ex.: Óculos de natação")
                        Campo(state.marca, vm::marca, "Marca (opcional)", state.erros.marca, !state.salvando)
                        ExposedDropdownMenuBox(expanded = categorias, onExpandedChange = { if (!state.salvando) categorias = !categorias }) {
                            OutlinedTextField(value = state.categoria.titulo, onValueChange = {}, readOnly = true,
                                label = { Text("Categoria") }, enabled = !state.salvando,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categorias) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable))
                            ExposedDropdownMenu(expanded = categorias, onDismissRequest = { categorias = false }) {
                                Categoria.entries.forEach { categoria ->
                                    DropdownMenuItem(text = { Text(categoria.titulo) }, onClick = {
                                        vm.categoria(categoria); categorias = false
                                    })
                                }
                            }
                        }
                        Campo(state.quantidade, vm::quantidade, "Quantidade *", state.erros.quantidade, !state.salvando,
                            hint = "1 a 99; um par pode ser cadastrado como 1 unidade", numeric = true)
                        Campo(state.descricao, vm::descricao, "Observações (opcional)", state.erros.descricao,
                            !state.salvando, hint = "Tamanho, cor, cuidados… até 500 caracteres", multiline = true)
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Já está na mochila?", style = MaterialTheme.typography.titleMedium)
                                Text("Marque se estiver pronto para levar.", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = state.pronto, onCheckedChange = vm::pronto, enabled = !state.salvando,
                                modifier = Modifier.semantics { contentDescription = "Já está na mochila" })
                        }
                        if (!state.erros.valido) Text("Revise os campos indicados para continuar.",
                            color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                        Button(onClick = { keyboard?.hide(); vm.salvar() }, enabled = !state.salvando && !state.salvo,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                            if (state.salvando) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(10.dp))
                            }
                            Text(if (state.salvando) "Salvando…" else "Salvar equipamento")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Campo(value: String, onValueChange: (String) -> Unit, label: String, erro: String?, enabled: Boolean,
                  hint: String? = null, numeric: Boolean = false, multiline: Boolean = false) {
    OutlinedTextField(value = value, onValueChange = { onValueChange(it.take(if (multiline) 1000 else 120)) },
        modifier = Modifier.fillMaxWidth(), enabled = enabled, label = { Text(label) }, isError = erro != null,
        singleLine = !multiline, minLines = if (multiline) 3 else 1,
        supportingText = { if (erro != null || hint != null) Text(erro ?: hint.orEmpty()) },
        keyboardOptions = KeyboardOptions(
            capitalization = if (numeric) KeyboardCapitalization.None else KeyboardCapitalization.Sentences,
            keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text,
            imeAction = if (multiline) ImeAction.Default else ImeAction.Next,
        ))
}
