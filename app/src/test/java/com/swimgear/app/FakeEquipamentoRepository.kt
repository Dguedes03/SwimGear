package com.swimgear.app

import com.swimgear.app.model.Equipamento
import com.swimgear.app.repository.EquipamentoRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*

class FakeEquipamentoRepository : EquipamentoRepository {
    val items = MutableStateFlow<List<Equipamento>>(emptyList())
    var falharLeitura = false
    var falharEscrita = false
    var gravacoes = 0
    var gate: CompletableDeferred<Unit>? = null
    override fun observarTodos(): Flow<List<Equipamento>> = flow {
        if (falharLeitura) error("Falha de teste")
        emitAll(items)
    }
    override fun observarPorId(id: String) = items.map { list -> list.find { it.id == id } }
    override suspend fun inicializar() {}
    override suspend fun buscar(id: String) = items.value.find { it.id == id }
    override suspend fun salvar(equipamento: Equipamento) {
        gate?.await()
        if (falharEscrita) error("Falha de teste")
        gravacoes++
        items.value = items.value.filterNot { it.id == equipamento.id } + equipamento
    }
    override suspend fun definirPronto(id: String, pronto: Boolean) {
        if (falharEscrita) error("Falha de teste")
        items.value = items.value.map { if (it.id == id) it.copy(prontoParaTreino = pronto) else it }
    }
    override suspend fun excluir(id: String) { items.value = items.value.filterNot { it.id == id } }
    override suspend fun adicionarExemplos() {}
}
