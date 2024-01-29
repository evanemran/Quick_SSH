package com.evanemran.quickssh.model

import java.io.Serializable

class SshUser(private val mHost: String,
    private val mPort: Int,
    private val mUsername: String,
    private val mPassword: String): Serializable {

    val host: String = mHost
    val port: Int = mPort
    val username: String = mUsername
    val password: String = mPassword
}