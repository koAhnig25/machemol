package com.example.machemol

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Degustation::class], version = 2)
abstract class MachemolDatabase : RoomDatabase() {

    abstract fun degustationDao(): DegustationDao

    companion object {
        @Volatile
        private var INSTANCE: MachemolDatabase? = null

        // Migration von Version 1 auf 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE degustationen ADD COLUMN markiert INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE degustationen ADD COLUMN anzahlBestellt TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): MachemolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MachemolDatabase::class.java,
                    "machemol.db"
                )
                    .addMigrations(MIGRATION_1_2) // ← Migration einbauen
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
