package com.evanemran.quickssh.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.evanemran.quickssh.model.SshCommand

@Dao
interface MainDao {
    @Insert(onConflict = REPLACE)
    fun insertCommand(command: SshCommand)

    @Delete
    fun deleteCommand(command: SshCommand)

    @Query("UPDATE commands SET command = :newCommand WHERE id = :id")
    fun updateCommand(newCommand: String, id: Int)

    @Query("SELECT * FROM commands ORDER BY id DESC")
    fun getAllCommands(): List<SshCommand>
}