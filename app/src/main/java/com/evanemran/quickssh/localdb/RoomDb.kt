package com.evanemran.quickssh.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.evanemran.quickssh.model.SshCommand


@Database(entities = [SshCommand::class], version = 1, exportSchema = false)
abstract class RoomDb: RoomDatabase() {

    companion object {
        private var database: RoomDb? = null
        private val DATABASE_NAME = "ssh_access"

        @Synchronized
        fun getInstance(context: Context): RoomDb {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDb::class.java, DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database as RoomDb
        }
    }

    abstract fun mainDAO(): MainDao?
}