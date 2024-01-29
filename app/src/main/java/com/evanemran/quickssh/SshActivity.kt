package com.evanemran.quickssh

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.evanemran.quickssh.databinding.ActivitySshBinding
import com.evanemran.quickssh.model.SshUser
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import java.io.ByteArrayOutputStream
import java.util.EnumSet
import java.util.concurrent.TimeUnit


class SshActivity : AppCompatActivity() {

    lateinit var binding: ActivitySshBinding
    lateinit var user: SshUser
    private var sshSession: ClientSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySshBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressbar.visibility = View.VISIBLE


        user = intent.getSerializableExtra("user") as SshUser

        var command = "pwd\n"
        val key = "user.home"
        val sysContext: Context = applicationContext
        val value = sysContext.applicationInfo.dataDir
        System.setProperty(key, value)

        val client: SshClient = SshClient.setUpDefaultClient()
        client.forwardingFilter = AcceptAllForwardingFilter.INSTANCE
        client.start()

        executeCommand(command, client, binding.terminal)


        binding.run.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            command = binding.commandLine.text.toString()+"\n"
            executeCommand(command, client, binding.terminal)
            binding.commandLine.setText("")
        }
    }

    private fun executeCommand(command: String, client: SshClient, terminalTextView: TextView) {
        val thread = Thread {
            try {
                // Check if there is an existing session
                if (sshSession == null || !sshSession!!.isOpen) {
                    // If no session or the existing session is closed, establish a new connection
                    sshSession = client.connect(user.username, user.host, user.port).verify(10000).session
                    sshSession!!.addPasswordIdentity(user.password)
                    sshSession!!.auth().verify(50000)
                    println("Connection Established")
                }

                // Create a channel within the existing session
                sshSession!!.createChannel(Channel.CHANNEL_SHELL).use { channel ->
                    println("Starting shell")
                    val responseStream = ByteArrayOutputStream()
                    channel.setOut(responseStream)

                    channel.open().verify(5, TimeUnit.SECONDS)

                    channel.invertedIn.use { pipedIn ->
                        pipedIn.write(command.toByteArray())
                        pipedIn.flush()
                    }

                    channel.waitFor(
                        EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(5)
                    )

                    val responseString = String(responseStream.toByteArray())
                    println(responseString)

                    runCommandOnUiThread {
                        terminalTextView.post {
                            terminalTextView.append("$command\n$responseString")
                        }
                        binding.progressbar.visibility = View.GONE
                    }
                    runCommandOnUiThread {
                        binding.scrollView.post(Runnable { binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.progressbar.visibility = View.GONE
            }
        }
        thread.start()
    }


    private fun runCommandOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }
}