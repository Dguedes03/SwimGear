package com.swimgear.app.repository

import androidx.room.withTransaction
import com.swimgear.app.data.EquipamentoMock
import com.swimgear.app.data.local.*
import com.swimgear.app.model.Equipamento
import com.swimgear.app.model.ValidacaoEquipamento
import kotlinx.coroutines.flow.map

class RoomEquipamentoRepository(private val database: SwimGearDatabase) : EquipamentoRepository {
    private val dao = database.equipamentoDao()
    override fun observarTodos() = dao.observarTodos().map { list -> list.map { it.toModel() } }
    override fun observarPorId(id: String) = dao.observarPorId(id).map { it?.toModel() }
    override suspend fun buscar(id: String) = dao.buscarPorId(id)?.toModel()

    override suspend fun inicializar() = database.withTransaction {
        if (dao.foiInicializado() != true) {
            adicionarExemplos()
            dao.salvarConfiguracao(ConfiguracaoEntity("inicializado", true))
        }
    }

    override suspend fun salvar(equipamento: Equipamento) {
        require(ValidacaoEquipamento.validar(equipamento.nome, equipamento.marca.orEmpty(), equipamento.descricao.orEmpty(), equipamento.quantidade.toString()).valido)
        dao.salvar(equipamento.copy(
            nome = equipamento.nome.trim(),
            marca = equipamento.marca?.trim()?.takeIf { it.isNotEmpty() },
            descricao = equipamento.descricao?.trim()?.takeIf { it.isNotEmpty() },
        ).toEntity())
    }

    override suspend fun definirPronto(id: String, pronto: Boolean) {
        check(dao.definirPronto(id, pronto) == 1) { "Equipamento não encontrado" }
    }
    override suspend fun excluir(id: String) = dao.excluir(id)
    override suspend fun adicionarExemplos() = dao.inserirExemplos(EquipamentoMock.equipamentos.map { it.toEntity() })
}
