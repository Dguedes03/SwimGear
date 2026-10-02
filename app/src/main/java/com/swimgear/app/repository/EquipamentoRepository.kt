package com.swimgear.app.repository

import com.swimgear.app.model.Equipamento
import kotlinx.coroutines.flow.Flow

interface EquipamentoRepository {
    fun observarTodos(): Flow<List<Equipamento>>
    fun observarPorId(id: String): Flow<Equipamento?>
    suspend fun inicializar()
    suspend fun buscar(id: String): Equipamento?
    suspend fun salvar(equipamento: Equipamento)
    suspend fun definirPronto(id: String, pronto: Boolean)
    suspend fun excluir(id: String)
    suspend fun adicionarExemplos()
}
