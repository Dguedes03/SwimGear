package com.swimgear.app.model

enum class Categoria(val titulo: String) {
    OCULOS("Óculos"), TOUCA("Touca"), NADADEIRA("Nadadeira"), PRANCHA("Prancha"),
    SNORKEL("Snorkel"), PULL_BUOY("Pull buoy"), PALMAR("Palmar"), OUTROS("Outros");

    companion object {
        fun fromStorage(value: String): Categoria = entries.find { it.name == value } ?: OUTROS
    }
}

/** Modelo de domínio imutável; os campos opcionais realmente podem ser nulos. */
data class Equipamento(
    val id: String,
    val nome: String,
    val marca: String? = null,
    val descricao: String? = null,
    val categoria: Categoria = Categoria.OUTROS,
    val quantidade: Int = 1,
    val prontoParaTreino: Boolean = false,
)
