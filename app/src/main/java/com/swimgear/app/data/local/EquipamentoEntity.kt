package com.swimgear.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swimgear.app.model.Categoria
import com.swimgear.app.model.Equipamento

@Entity(tableName = "equipamentos")
data class EquipamentoEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val marca: String?,
    val descricao: String?,
    val categoria: String,
    val quantidade: Int,
    val prontoParaTreino: Boolean,
)

@Entity(tableName = "configuracao")
data class ConfiguracaoEntity(@PrimaryKey val chave: String, val valor: Boolean)

fun EquipamentoEntity.toModel() = Equipamento(id, nome, marca, descricao, Categoria.fromStorage(categoria), quantidade, prontoParaTreino)
fun Equipamento.toEntity() = EquipamentoEntity(id, nome, marca, descricao, categoria.name, quantidade, prontoParaTreino)
