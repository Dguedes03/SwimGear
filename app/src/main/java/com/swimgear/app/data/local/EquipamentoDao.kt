package com.swimgear.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipamentoDao {
    @Query("SELECT * FROM equipamentos ORDER BY nome COLLATE NOCASE")
    fun observarTodos(): Flow<List<EquipamentoEntity>>

    @Query("SELECT * FROM equipamentos WHERE id = :id")
    fun observarPorId(id: String): Flow<EquipamentoEntity?>

    @Query("SELECT * FROM equipamentos WHERE id = :id")
    suspend fun buscarPorId(id: String): EquipamentoEntity?

    @Upsert suspend fun salvar(equipamento: EquipamentoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirExemplos(equipamentos: List<EquipamentoEntity>)

    @Query("UPDATE equipamentos SET prontoParaTreino = :pronto WHERE id = :id")
    suspend fun definirPronto(id: String, pronto: Boolean): Int

    @Query("DELETE FROM equipamentos WHERE id = :id")
    suspend fun excluir(id: String)

    @Query("SELECT valor FROM configuracao WHERE chave = 'inicializado'")
    suspend fun foiInicializado(): Boolean?

    @Upsert suspend fun salvarConfiguracao(configuracao: ConfiguracaoEntity)
}
