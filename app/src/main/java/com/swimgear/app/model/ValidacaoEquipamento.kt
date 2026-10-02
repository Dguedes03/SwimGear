package com.swimgear.app.model

data class ErrosFormulario(
    val nome: String? = null,
    val marca: String? = null,
    val descricao: String? = null,
    val quantidade: String? = null,
) {
    val valido: Boolean get() = listOf(nome, marca, descricao, quantidade).all { it == null }
}

object ValidacaoEquipamento {
    fun validar(nome: String, marca: String, descricao: String, quantidade: String): ErrosFormulario {
        val numero = quantidade.toIntOrNull()
        return ErrosFormulario(
            nome = when {
                nome.trim().length < 2 -> "Informe um nome com pelo menos 2 caracteres."
                nome.trim().length > 60 -> "Use no máximo 60 caracteres."
                else -> null
            },
            marca = if (marca.trim().length > 40) "Use no máximo 40 caracteres." else null,
            descricao = if (descricao.trim().length > 500) "Use no máximo 500 caracteres." else null,
            quantidade = if (numero == null || numero !in 1..99) "Informe uma quantidade de 1 a 99." else null,
        )
    }
}
