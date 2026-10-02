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

data class DetalhesUiState(
    val carregando: Boolean = true,
    val equipamento: Equipamento? = null,
    val erro: Boolean = false,
    val ocupado: Boolean = false,
    val excluido: Boolean = false,
    val mensagem: String? = null,
)

class DetalhesViewModel(
    private val id: String,
    private val repository: EquipamentoRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val state = MutableStateFlow(DetalhesUiState(excluido = savedState["excluido"] ?: false))
    val uiState = state.asStateFlow()
    private var loadJob: Job? = null
    init { carregar() }

    fun carregar() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            state.update { it.copy(carregando = true, erro = false) }
            try {
                repository.observarPorId(id).collect { equipamento ->
                    state.update { it.copy(carregando = false, equipamento = equipamento) }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { state.update { it.copy(carregando = false, erro = true) } }
        }
    }

    fun consumirMensagem() { state.update { it.copy(mensagem = null) } }
    fun definirPronto(pronto: Boolean) = executar {
        repository.definirPronto(id, pronto)
    }
    fun excluir() = executar {
        repository.excluir(id)
        savedState["excluido"] = true
        state.update { it.copy(excluido = true) }
    }
    private fun executar(action: suspend () -> Unit) {
        if (state.value.ocupado || state.value.excluido) return
        state.update { it.copy(ocupado = true) }
        viewModelScope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { state.update { it.copy(mensagem = "Não foi possível salvar a alteração. Tente novamente.") } }
            finally { state.update { it.copy(ocupado = false) } }
        }
    }
}
