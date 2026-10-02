package com.swimgear.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.swimgear.app.R
import com.swimgear.app.model.Categoria

fun Categoria.icone(): Int = when (this) {
    Categoria.OCULOS -> R.drawable.ic_goggles
    Categoria.TOUCA -> R.drawable.ic_cap
    Categoria.NADADEIRA -> R.drawable.ic_fins
    Categoria.PRANCHA -> R.drawable.ic_board
    Categoria.SNORKEL -> R.drawable.ic_snorkel
    Categoria.PULL_BUOY -> R.drawable.ic_buoy
    Categoria.PALMAR -> R.drawable.ic_paddle
    Categoria.OUTROS -> R.drawable.ic_bag
}

@Composable
fun EquipamentoIcon(categoria: Categoria, size: Dp = 58.dp) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primaryContainer) {
        Icon(painterResource(categoria.icone()), contentDescription = null,
            modifier = Modifier.size(size).padding(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTitulo(titulo: String, voltar: () -> Unit, enabled: Boolean = true) {
    TopAppBar(title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = { IconButton(onClick = voltar, enabled = enabled) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
        } })
}

@Composable
fun EstadoVazio(titulo: String, descricao: String, acao: String? = null, onAction: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth().padding(vertical = 36.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        EquipamentoIcon(Categoria.OUTROS, 80.dp)
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() })
        Text(descricao, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (acao != null) Button(onClick = onAction, modifier = Modifier.heightIn(min = 48.dp)) { Text(acao) }
    }
}

@Composable
fun Carregando(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CircularProgressIndicator(Modifier.semantics { contentDescription = "Carregando equipamentos" })
        Text("Organizando sua mochila…")
    }
}

@Composable
fun MensagemEffect(mensagem: String?, snackbar: SnackbarHostState, consumir: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentConsumir by rememberUpdatedState(consumir)
    LaunchedEffect(mensagem, lifecycle) {
        if (mensagem != null) lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snackbar.showSnackbar(mensagem)
            currentConsumir()
        }
    }
}

/** A confirmação só navega enquanto esta tela estiver visível/resumed. */
@Composable
fun ConclusaoEffect(concluido: Boolean, concluir: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentConcluir by rememberUpdatedState(concluir)
    LaunchedEffect(concluido, lifecycle) {
        if (concluido) lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { currentConcluir() }
    }
}
