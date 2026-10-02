package com.swimgear.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swimgear.app.model.Equipamento
import com.swimgear.app.repository.EquipamentoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.Normalizer

enum class Filtro(val titulo: String) { TODOS("Todos"), PENDENTES("Pendentes"), PRONTOS("Na mochila") }
enum class Fase { CARREGANDO, CONTEUDO, VAZIO, ERRO }

data class ListaUiState(
    val fase: Fase = Fase.CARREGANDO,
    val equipamentos: List<Equipamento> = emptyList(),
    val total: Int = 0,
    val prontos: Int = 0,
    val busca: String = "",
    val filtro: Filtro = Filtro.TODOS,
    val mensagem: String? = null,
    val adicionando: Boolean = false,
)

internal fun String.searchKey(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "").lowercase(java.util.Locale.ROOT)

class ListaViewModel(
    private val repository: EquipamentoRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val source = MutableStateFlow(ListaUiState())
    private var loadJob: Job? = null
    val uiState: StateFlow<ListaUiState> = combine(
        source,
        savedState.getStateFlow("busca", ""),
        savedState.getStateFlow("filtro", Filtro.TODOS.name),
    ) { state, busca, filtroNome ->
        val filtro = Filtro.entries.find { it.name == filtroNome } ?: Filtro.TODOS
        val encontrados = state.equipamentos.filter {
            val corresponde = "${it.nome} ${it.marca.orEmpty()} ${it.categoria.titulo}".searchKey().contains(busca.trim().searchKey())
            corresponde && when (filtro) {
                Filtro.TODOS -> true
                Filtro.PRONTOS -> it.prontoParaTreino
                Filtro.PENDENTES -> !it.prontoParaTreino
            }
        }
        state.copy(
            equipamentos = encontrados, busca = busca, filtro = filtro,
            fase = if (state.fase in listOf(Fase.CONTEUDO, Fase.VAZIO)) {
                if (encontrados.isEmpty()) Fase.VAZIO else Fase.CONTEUDO
            } else state.fase,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListaUiState())

    init { recarregar() }

    fun buscar(value: String) { savedState["busca"] = value.take(120) }
    fun filtrar(filtro: Filtro) { savedState["filtro"] = filtro.name }
    fun limparFiltros() { buscar(""); filtrar(Filtro.TODOS) }
    fun consumirMensagem() { source.update { it.copy(mensagem = null) } }

    fun recarregar() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            source.update { it.copy(fase = Fase.CARREGANDO) }
            try {
                repository.inicializar()
                repository.observarTodos().collect { equipamentos ->
                    source.update { it.copy(
                        fase = if (equipamentos.isEmpty()) Fase.VAZIO else Fase.CONTEUDO,
                        equipamentos = equipamentos,
                        total = equipamentos.size,
                        prontos = equipamentos.count { e -> e.prontoParaTreino },
                    ) }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { source.update { it.copy(fase = Fase.ERRO) } }
        }
    }

    fun adicionarExemplos() {
        if (source.value.adicionando) return
        source.update { it.copy(adicionando = true) }
        viewModelScope.launch {
            try { repository.adicionarExemplos() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { source.update { it.copy(mensagem = "Não foi possível adicionar os exemplos. Tente novamente.") } }
            finally { source.update { it.copy(adicionando = false) } }
        }
    }
}
