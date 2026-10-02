package com.swimgear.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swimgear.app.model.*
import com.swimgear.app.repository.EquipamentoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class FormularioUiState(
    val nome: String = "",
    val marca: String = "",
    val descricao: String = "",
    val quantidade: String = "1",
    val categoria: Categoria = Categoria.OCULOS,
    val pronto: Boolean = false,
    val carregando: Boolean = false,
    val falhaCarregamento: Boolean = false,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
    val erros: ErrosFormulario = ErrosFormulario(),
    val mensagem: String? = null,
)

class FormularioViewModel(
    private val equipamentoId: String?,
    private val repository: EquipamentoRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    // Reusar o ID do rascunho torna o salvamento idempotente após recriação do processo.
    private val id: String = equipamentoId ?: savedState.get<String>("draftId")
        ?: UUID.randomUUID().toString().also { savedState["draftId"] = it }
    private val state = MutableStateFlow(FormularioUiState(
        nome = savedState["nome"] ?: "",
        marca = savedState["marca"] ?: "",
        descricao = savedState["descricao"] ?: "",
        quantidade = savedState["quantidade"] ?: "1",
        categoria = Categoria.fromStorage(savedState["categoria"] ?: Categoria.OCULOS.name),
        pronto = savedState["pronto"] ?: false,
        salvo = savedState["salvo"] ?: false,
    ))
    val uiState = state.asStateFlow()
    init { if (equipamentoId != null && savedState.get<Boolean>("carregado") != true) carregar() }

    fun carregar() {
        if (state.value.carregando) return
        state.update { it.copy(carregando = true, falhaCarregamento = false) }
        viewModelScope.launch {
            try {
                val item = checkNotNull(repository.buscar(id))
                state.value = FormularioUiState(item.nome, item.marca.orEmpty(), item.descricao.orEmpty(), item.quantidade.toString(), item.categoria, item.prontoParaTreino)
                persistirRascunho()
                savedState["carregado"] = true
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { state.update { it.copy(carregando = false, falhaCarregamento = true) } }
        }
    }

    fun nome(value: String) = editar { it.copy(nome = value, erros = it.erros.copy(nome = null)) }
    fun marca(value: String) = editar { it.copy(marca = value, erros = it.erros.copy(marca = null)) }
    fun descricao(value: String) = editar { it.copy(descricao = value, erros = it.erros.copy(descricao = null)) }
    fun quantidade(value: String) = editar { it.copy(quantidade = value, erros = it.erros.copy(quantidade = null)) }
    fun categoria(value: Categoria) = editar { it.copy(categoria = value) }
    fun pronto(value: Boolean) = editar { it.copy(pronto = value) }
    fun consumirMensagem() { state.update { it.copy(mensagem = null) } }

    private fun editar(transform: (FormularioUiState) -> FormularioUiState) {
        if (state.value.salvando || state.value.carregando || state.value.salvo) return
        state.update(transform)
        persistirRascunho()
    }

    private fun persistirRascunho() {
        val s = state.value
        savedState["nome"] = s.nome
        savedState["marca"] = s.marca
        savedState["descricao"] = s.descricao
        savedState["quantidade"] = s.quantidade
        savedState["categoria"] = s.categoria.name
        savedState["pronto"] = s.pronto
    }

    fun salvar() {
        val form = state.value
        if (form.salvando || form.salvo || form.carregando || form.falhaCarregamento) return
        val erros = ValidacaoEquipamento.validar(form.nome, form.marca, form.descricao, form.quantidade)
        state.update { it.copy(erros = erros) }
        if (!erros.valido) return
        state.update { it.copy(salvando = true) }
        viewModelScope.launch {
            try {
                repository.salvar(Equipamento(
                    id, form.nome.trim(), form.marca.trim().ifEmpty { null },
                    form.descricao.trim().ifEmpty { null }, form.categoria,
                    form.quantidade.toInt(), form.pronto,
                ))
                savedState["salvo"] = true
                state.update { it.copy(salvo = true) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { state.update { it.copy(mensagem = "Não foi possível salvar. Seu formulário foi preservado; tente novamente.") } }
            finally { state.update { it.copy(salvando = false) } }
        }
    }
}
