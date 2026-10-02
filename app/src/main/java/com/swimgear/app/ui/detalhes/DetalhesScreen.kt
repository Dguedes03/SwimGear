package com.swimgear.app.ui.detalhes

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swimgear.app.ui.components.*
import com.swimgear.app.viewmodel.DetalhesViewModel

@Composable
fun DetalhesScreen(vm: DetalhesViewModel, voltar: () -> Unit, editar: () -> Unit) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    var confirmarExclusao by rememberSaveable { mutableStateOf(false) }
    MensagemEffect(state.mensagem, snackbar, vm::consumirMensagem)
    ConclusaoEffect(state.excluido, voltar)
    BackHandler(enabled = state.ocupado) { /* Aguarda a operação para não abandonar o resultado. */ }
    Scaffold(topBar = { BarraTitulo("Seu equipamento", voltar, !state.ocupado) },
        snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 720.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                when {
                    state.carregando -> Carregando()
                    state.erro -> EstadoVazio("Não foi possível carregar", "Tente abrir o equipamento novamente.", "Tentar novamente", vm::carregar)
                    state.equipamento == null -> EstadoVazio("Equipamento não encontrado", "Este item pode ter sido excluído.", "Voltar para a lista", voltar)
                    else -> {
                        val item = state.equipamento!!
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            EquipamentoIcon(item.categoria, 112.dp)
                            Text(item.categoria.titulo.uppercase(), style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary)
                            Text(item.nome, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center,
                                modifier = Modifier.semantics { heading() })
                            Text("${item.marca ?: "Marca não informada"} · ${item.quantidade} un.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Card(shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Row(Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(if (item.prontoParaTreino) "Pronto para o treino" else "Vamos preparar a mochila?",
                                        style = MaterialTheme.typography.titleMedium)
                                    Text(if (item.prontoParaTreino) "Este equipamento já está separado." else "Marque depois de separar este item.",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                                Switch(checked = item.prontoParaTreino, onCheckedChange = vm::definirPronto,
                                    enabled = !state.ocupado, modifier = Modifier.semantics {
                                        contentDescription = "Pronto para o treino"
                                        stateDescription = if (item.prontoParaTreino) "Na mochila" else "Pendente"
                                    })
                            }
                        }
                        if (state.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Observações", style = MaterialTheme.typography.titleMedium)
                                Text(item.descricao ?: "Nenhuma observação. Você pode adicionar detalhes ao editar o equipamento.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(onClick = editar, enabled = !state.ocupado, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                            Text("Editar equipamento")
                        }
                        OutlinedButton(onClick = {
                            val texto = buildString {
                                appendLine("SwimGear · ${item.nome}")
                                appendLine("Categoria: ${item.categoria.titulo}")
                                item.marca?.let { appendLine("Marca: $it") }
                                appendLine("Quantidade: ${item.quantidade}")
                                appendLine(if (item.prontoParaTreino) "Pronto para o treino" else "Pendente de separar")
                                item.descricao?.let { appendLine(it) }
                            }
                            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, texto)
                            }, "Compartilhar equipamento"))
                        }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Compartilhar equipamento") }
                        TextButton(onClick = { confirmarExclusao = true }, enabled = !state.ocupado,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Excluir equipamento") }
                    }
                }
            }
        }
    }
    if (confirmarExclusao) AlertDialog(onDismissRequest = { confirmarExclusao = false },
        title = { Text("Excluir equipamento?") },
        text = { Text("O item será removido da sua lista. Essa ação não pode ser desfeita.") },
        confirmButton = { TextButton(onClick = { confirmarExclusao = false; vm.excluir() }) { Text("Excluir") } },
        dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } })
}
