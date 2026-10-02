package com.swimgear.app

import android.app.Application
import androidx.room.Room
import com.swimgear.app.data.local.SwimGearDatabase
import com.swimgear.app.repository.EquipamentoRepository
import com.swimgear.app.repository.RoomEquipamentoRepository

/** Injeção manual: uma instância de banco e repositório por processo. */
class SwimGearApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, SwimGearDatabase::class.java, "swimgear.db").build()
    }
    val repository: EquipamentoRepository by lazy { RoomEquipamentoRepository(database) }
}
