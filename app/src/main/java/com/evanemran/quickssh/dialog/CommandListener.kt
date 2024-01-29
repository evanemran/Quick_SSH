package com.evanemran.quickssh.dialog

import com.evanemran.quickssh.model.SshCommand

interface CommandListener {
    fun onCommandClicked(command: SshCommand)
}