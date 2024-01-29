package com.evanemran.quickssh.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commands")
class SshCommand {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var command: String = ""
    var time: String = ""
}