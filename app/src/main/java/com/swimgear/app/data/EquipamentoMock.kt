package com.swimgear.app.data

import com.swimgear.app.model.Categoria
import com.swimgear.app.model.Equipamento

/** Fonte da parcial XML. A etapa Compose importa uma cópia no primeiro uso. */
object EquipamentoMock {
    val equipamentos = listOf(
        Equipamento("ex-oculos", "Óculos de natação", "Arena", "Lentes para os treinos na piscina. Guardar no estojo após o uso.", Categoria.OCULOS, prontoParaTreino = true),
        Equipamento("ex-touca", "Touca de silicone", "Speedo", "Touca de uso diário.", Categoria.TOUCA, prontoParaTreino = true),
        Equipamento("ex-nadadeira", "Nadadeira curta", "Speedo", "Um par para séries de perna e técnica.", Categoria.NADADEIRA),
        Equipamento("ex-pull", "Pull buoy", null, "Flutuador para séries de braço.", Categoria.PULL_BUOY, prontoParaTreino = true),
        Equipamento("ex-snorkel", "Snorkel frontal", "Arena", null, Categoria.SNORKEL),
        Equipamento("ex-palmar", "Palmar de treino", null, "Um par de tamanho médio.", Categoria.PALMAR),
    )
}
