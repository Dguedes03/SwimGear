package com.swimgear.app

import com.swimgear.app.model.ValidacaoEquipamento
import org.junit.Assert.*
import org.junit.Test

class ValidacaoEquipamentoTest {
    @Test fun `nome em branco e quantidade invalida sao rejeitados`() {
        val erros = ValidacaoEquipamento.validar("  ", "", "", "0")
        assertFalse(erros.valido)
        assertNotNull(erros.nome)
        assertNotNull(erros.quantidade)
    }
    @Test fun `campos opcionais vazios e limites validos sao aceitos`() {
        assertTrue(ValidacaoEquipamento.validar("Óculos", "", "", "1").valido)
        assertTrue(ValidacaoEquipamento.validar("N".repeat(60), "M".repeat(40), "D".repeat(500), "99").valido)
    }
    @Test fun `valores acima dos limites nao sao salvos`() {
        val erros = ValidacaoEquipamento.validar("N".repeat(61), "M".repeat(41), "D".repeat(501), "100")
        assertNotNull(erros.nome); assertNotNull(erros.marca)
        assertNotNull(erros.descricao); assertNotNull(erros.quantidade)
    }
    @Test fun `numero negativo decimal ou texto nao passa pela validacao`() {
        listOf("-1", "1.5", "abc", "", "99999999999999999999").forEach {
            assertNotNull(ValidacaoEquipamento.validar("Touca", "", "", it).quantidade)
        }
    }
}
