package com.evanemran.quickssh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import com.evanemran.quickssh.databinding.ActivityMainBinding
import com.evanemran.quickssh.model.SshUser
import org.apache.sshd.client.channel.ClientChannel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun authenticate(view: View) {
        val intent: Intent = Intent(this, SshActivity::class.java)

        val host = binding.hostField.text.toString()
        val port = binding.portField.text.toString().toInt()
        val username = binding.usernameField.text.toString()
        val password = binding.passwordField.text.toString()

        val user: SshUser = SshUser(host, port, username, password)

        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }
}