package com.swimgear.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EquipamentoEntity::class, ConfiguracaoEntity::class], version = 1, exportSchema = true)
abstract class SwimGearDatabase : RoomDatabase() {
    abstract fun equipamentoDao(): EquipamentoDao
}
