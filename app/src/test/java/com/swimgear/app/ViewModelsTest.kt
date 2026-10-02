package com.swimgear.app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.swimgear.app.model.Equipamento
import com.swimgear.app.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { store.clear(); Dispatchers.resetMain() }
    private fun <T : ViewModel> keep(vm: T): T = vm.also { store.put(it.toString(), it) }

    @Test fun `lista apresenta erro e recupera para vazio ao tentar novamente`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository().apply { falharLeitura = true }
        val vm = keep(ListaViewModel(repo, SavedStateHandle()))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        assertEquals(Fase.CARREGANDO, vm.uiState.value.fase)
        advanceUntilIdle()
        assertEquals(Fase.ERRO, vm.uiState.value.fase)
        repo.falharLeitura = false
        vm.recarregar(); advanceUntilIdle()
        assertEquals(Fase.VAZIO, vm.uiState.value.fase)
    }

    @Test fun `busca ignora acentos e filtro usa estado salvo`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository()
        repo.items.value = listOf(Equipamento("1", "Óculos", prontoParaTreino = true), Equipamento("2", "Touca"))
        val saved = SavedStateHandle(mapOf("busca" to "oculos", "filtro" to Filtro.PRONTOS.name))
        val vm = keep(ListaViewModel(repo, saved))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        advanceUntilIdle()
        assertEquals(listOf("1"), vm.uiState.value.equipamentos.map { it.id })
        assertEquals(2, vm.uiState.value.total)
        vm.filtrar(Filtro.PENDENTES); advanceUntilIdle()
        assertEquals(Fase.VAZIO, vm.uiState.value.fase)
        assertEquals(Filtro.PENDENTES.name, saved.get<String>("filtro"))
    }

    @Test fun `formulario invalido nao escreve e erro de escrita preserva rascunho`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository()
        val saved = SavedStateHandle()
        val vm = keep(FormularioViewModel(null, repo, saved))
        vm.salvar(); advanceUntilIdle()
        assertNotNull(vm.uiState.value.erros.nome)
        assertEquals(0, repo.gravacoes)
        vm.nome("Prancha azul"); vm.quantidade("2")
        repo.falharEscrita = true
        vm.salvar(); advanceUntilIdle()
        assertNotNull(vm.uiState.value.mensagem)
        assertEquals("Prancha azul", saved.get<String>("nome"))
        assertFalse(vm.uiState.value.salvando)
        repo.falharEscrita = false
        vm.salvar(); advanceUntilIdle()
        assertTrue(vm.uiState.value.salvo)
        assertEquals(2, repo.items.value.single().quantidade)
    }

    @Test fun `toque duplo em salvar produz somente uma gravacao`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository().apply { gate = CompletableDeferred() }
        val vm = keep(FormularioViewModel(null, repo, SavedStateHandle()))
        vm.nome("Touca azul")
        vm.salvar(); vm.salvar(); runCurrent()
        assertTrue(vm.uiState.value.salvando)
        repo.gate!!.complete(Unit); advanceUntilIdle()
        vm.salvar(); advanceUntilIdle()
        assertEquals(1, repo.gravacoes)
    }

    @Test fun `rascunho restaura campos e identificador sem duplicar item`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository()
        val saved = SavedStateHandle()
        val first = keep(FormularioViewModel(null, repo, saved))
        first.nome("Snorkel"); first.marca(" ")
        val restoredHandle = SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) })
        val restored = keep(FormularioViewModel(null, repo, restoredHandle))
        assertEquals("Snorkel", restored.uiState.value.nome)
        first.salvar(); advanceUntilIdle()
        restored.salvar(); advanceUntilIdle()
        assertEquals(1, repo.items.value.size)
        assertNull(repo.items.value.single().marca)
    }

    @Test fun `cancelar viewmodel interrompe salvamento pendente`() = runTest(dispatcher) {
        val repo = FakeEquipamentoRepository().apply { gate = CompletableDeferred() }
        val vm = keep(FormularioViewModel(null, repo, SavedStateHandle()))
        vm.nome("Palmar")
        vm.salvar(); runCurrent()
        store.clear()
        repo.gate!!.complete(Unit); advanceUntilIdle()
        assertEquals(0, repo.gravacoes)
        assertNull(vm.uiState.value.mensagem)
    }
}
