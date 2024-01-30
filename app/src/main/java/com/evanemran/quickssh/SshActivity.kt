package com.evanemran.quickssh

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.evanemran.quickssh.databinding.ActivitySshBinding
import com.evanemran.quickssh.dialog.CommandListener
import com.evanemran.quickssh.dialog.CommandsDialog
import com.evanemran.quickssh.localdb.RoomDb
import com.evanemran.quickssh.model.SshCommand
import com.evanemran.quickssh.model.SshUser
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import java.io.ByteArrayOutputStream
import java.util.EnumSet
import java.util.concurrent.TimeUnit


class SshActivity : AppCompatActivity(), CommandListener {

    lateinit var binding: ActivitySshBinding
    lateinit var user: SshUser
    private var sshSession: ClientSession? = null
    var database: RoomDb? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySshBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = RoomDb.getInstance(this)

        setSupportActionBar(binding.appBar)

        val customTypeface: Typeface? =
            ResourcesCompat.getFont(this, R.font.custom_font_bold)

        if (customTypeface != null) {
            val title = getString(R.string.terminal)
            val spannableString = SpannableString(title)
            spannableString.setSpan(
                CustomTypefaceSpan(customTypeface),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            supportActionBar?.title = spannableString
        }

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
            if(binding.commandLine.text.toString().isNotEmpty()) {
                binding.progressbar.visibility = View.VISIBLE
                command = binding.commandLine.text.toString()+"\n"
                executeCommand(command, client, binding.terminal)
                val command = SshCommand()
                command.command = binding.commandLine.text.toString()
                command.time = "Now"
                database?.mainDAO()?.insertCommand(command)
                binding.commandLine.setText("")
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id==R.id.action_commands) {
//            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            val commandsDialog = CommandsDialog(this, this)
            commandsDialog.show(supportFragmentManager, "command")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCommandClicked(command: SshCommand) {
        runCommandOnUiThread {
            binding.commandLine.setText(command.command)
        }
    }

    private class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, typeface)
        }

        override fun updateMeasureState(p: TextPaint) {
            applyCustomTypeFace(p, typeface)
        }

        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            paint.typeface = tf
        }
    }
}