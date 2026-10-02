package com.swimgear.app.ui.lista

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swimgear.app.R
import com.swimgear.app.model.Equipamento
import com.swimgear.app.ui.components.*
import com.swimgear.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(vm: ListaViewModel, abrir: (String) -> Unit, adicionar: () -> Unit, abrirXml: () -> Unit) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var menu by remember { mutableStateOf(false) }
    var sobre by rememberSaveable { mutableStateOf(false) }
    MensagemEffect(state.mensagem, snackbar, vm::consumirMensagem)
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(painterResource(R.drawable.ic_swim), null, Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary)
                    Text("SwimGear", style = MaterialTheme.typography.headlineMedium)
                }
            }, actions = {
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, "Mais opções") }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("Versão XML") }, onClick = { menu = false; abrirXml() })
                        DropdownMenuItem(text = { Text("Sobre o SwimGear") }, onClick = { menu = false; sobre = true })
                    }
                }
            })
        },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = adicionar,
            icon = { Icon(Icons.Default.Add, null) }, text = { Text("Adicionar equipamento") },
            containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(Modifier.widthIn(max = 760.dp).fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item(key = "hero") { MochilaHero(state.total, state.prontos, state.fase == Fase.CARREGANDO) }
                item(key = "heading") {
                    Text("Meus equipamentos", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 10.dp).semantics { heading() })
                }
                item(key = "search") {
                    OutlinedTextField(value = state.busca, onValueChange = vm::buscar,
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        shape = RoundedCornerShape(16.dp), label = { Text("Buscar equipamento ou marca") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = { if (state.busca.isNotEmpty()) IconButton(onClick = { vm.buscar("") }) {
                            Icon(Icons.Default.Close, "Limpar busca")
                        } })
                }
                item(key = "filters") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Filtro.entries) { filtro ->
                            FilterChip(selected = state.filtro == filtro, onClick = { vm.filtrar(filtro) },
                                modifier = Modifier.heightIn(min = 48.dp), label = { Text(filtro.titulo) })
                        }
                    }
                }
                when (state.fase) {
                    Fase.CARREGANDO -> item { Carregando() }
                    Fase.ERRO -> item { EstadoVazio("Não foi possível abrir a mochila", "Ocorreu um erro ao ler os equipamentos. Tente novamente.", "Tentar novamente", vm::recarregar) }
                    Fase.VAZIO -> item {
                        if (state.total == 0) {
                            EstadoVazio("Sua mochila está vazia", "Cadastre seu primeiro equipamento para preparar o próximo treino.", "Cadastrar equipamento", adicionar)
                            TextButton(onClick = vm::adicionarExemplos, enabled = !state.adicionando,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                Text(if (state.adicionando) "Adicionando…" else "Começar com equipamentos de exemplo")
                            }
                        } else EstadoVazio("Nenhum item por aqui", "Experimente outra busca ou veja todos os seus equipamentos.", "Limpar filtros", vm::limparFiltros)
                    }
                    Fase.CONTEUDO -> items(state.equipamentos, key = { it.id }) { equipamento ->
                        EquipamentoCard(equipamento) { abrir(equipamento.id) }
                    }
                }
                if (state.fase == Fase.CONTEUDO) item(key = "footer") {
                    Text("Tudo salvo neste aparelho. Pronto para usar offline.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
    if (sobre) AlertDialog(onDismissRequest = { sobre = false }, title = { Text("SwimGear") },
        text = { Text("Sua mochila, em dia.\n\nOrganize seus equipamentos de natação e prepare cada treino. Os dados ficam neste aparelho, sem conta e sem internet.\n\nNo menu, a versão XML demonstra a parcial com dados de exemplo independentes.\n\nVersão 1.0.0") },
        confirmButton = { TextButton(onClick = { sobre = false }) { Text("Entendi") } })
}

@Composable
private fun MochilaHero(total: Int, prontos: Int, carregando: Boolean) {
    Surface(shape = RoundedCornerShape(26.dp), color = Color(0xFF073F3C), contentColor = Color.White) {
        Box(Modifier.fillMaxWidth()) {
            Canvas(Modifier.matchParentSize()) {
                repeat(4) { line ->
                    val y = size.height * .34f + line * 18.dp.toPx()
                    val path = Path().apply {
                        moveTo(size.width * .67f, y)
                        cubicTo(size.width * .8f, y - 50f, size.width * .91f, y + 45f, size.width * 1.08f, y - 8f)
                    }
                    drawPath(path, Color(0xFF83D9C5).copy(alpha = .18f), style = Stroke(2.dp.toPx()))
                }
            }
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("SUA MOCHILA, EM DIA", style = MaterialTheme.typography.labelMedium, color = Color(0xFFB5EFDB))
                Text("Menos esquecimento.\nMais natação.", style = MaterialTheme.typography.headlineMedium)
                Text(if (carregando) "Preparando seus equipamentos…" else "$prontos de $total equipamentos prontos para o treino.",
                    style = MaterialTheme.typography.bodyMedium, color = Color(0xFFDEEFE8))
                LinearProgressIndicator(progress = { if (total > 0) prontos.toFloat() / total else 0f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).semantics {
                        contentDescription = "$prontos de $total equipamentos na mochila"
                    }, color = Color(0xFFAFF0D5), trackColor = Color(0xFF36635C))
            }
        }
    }
}

@Composable
private fun EquipamentoCard(item: Equipamento, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            EquipamentoIcon(item.categoria)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(item.nome, style = MaterialTheme.typography.titleMedium)
                Text("${item.marca ?: "Sem marca"} · ${item.quantidade} un.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (item.prontoParaTreino) "✓ Na mochila" else "○ Falta separar",
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold,
                    color = if (item.prontoParaTreino) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
