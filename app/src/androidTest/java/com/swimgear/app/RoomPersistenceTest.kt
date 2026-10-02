package com.swimgear.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.swimgear.app.data.local.SwimGearDatabase
import com.swimgear.app.model.Equipamento
import com.swimgear.app.repository.RoomEquipamentoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class RoomPersistenceTest {
    @Test fun cadastroAlteracaoExclusaoEReabertura() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "test-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder(context, SwimGearDatabase::class.java, name).build()
        var db = open()
        try {
            var repo = RoomEquipamentoRepository(db)
            repo.inicializar()
            repo.observarTodos().first().forEach { repo.excluir(it.id) }
            repo.salvar(Equipamento("teste", "  Touca azul  ", marca = "  "))
            repo.definirPronto("teste", true)
            db.close(); db = open(); repo = RoomEquipamentoRepository(db)
            repo.inicializar()
            val item = repo.observarTodos().first().single()
            assertEquals("Touca azul", item.nome)
            assertNull(item.marca)
            assertTrue(item.prontoParaTreino)
            repo.excluir("teste")
            db.close(); db = open(); repo = RoomEquipamentoRepository(db)
            repo.inicializar()
            assertTrue(repo.observarTodos().first().isEmpty())
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
