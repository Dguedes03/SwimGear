package com.swimgear.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FluxoComposeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun cadastrarRecriarAbrirMarcarEditarEExcluir() {
        val nome = "Touca teste ${System.nanoTime()}"
        compose.onNodeWithText("Adicionar equipamento").performClick()
        compose.onNodeWithText("Salvar equipamento").performScrollTo().performClick()
        compose.onNodeWithText("Informe um nome com pelo menos 2 caracteres.").assertExists()
        compose.onNodeWithText("Nome do equipamento *").performScrollTo().performTextInput(nome)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText(nome).assertExists()
        compose.onNodeWithText("Salvar equipamento").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Buscar equipamento ou marca").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Buscar equipamento ou marca").performTextInput(nome)
        compose.onNodeWithText(nome).performScrollTo().performClick()
        compose.onNodeWithContentDescription("Pronto para o treino").performScrollTo().performClick()
        compose.waitForIdle()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Pronto para o treino").assertIsOn()
        compose.onNodeWithText("Editar equipamento").performScrollTo().performClick()
        compose.onNodeWithText("Nome do equipamento *").performScrollTo().performTextReplacement("$nome editada")
        compose.onNodeWithText("Salvar equipamento").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("$nome editada").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Excluir equipamento").performScrollTo().performClick()
        compose.onNodeWithText("Excluir", useUnmergedTree = true).performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Nenhum item por aqui").fetchSemanticsNodes().isNotEmpty() }
    }
}
