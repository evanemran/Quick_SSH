package com.evanemran.quickssh.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.quickssh.R
import com.evanemran.quickssh.adapter.CommandsListAdapter
import com.evanemran.quickssh.databinding.ActivitySshBinding
import com.evanemran.quickssh.databinding.DialogCommandBinding
import com.evanemran.quickssh.localdb.RoomDb
import com.evanemran.quickssh.model.SshCommand


class CommandsDialog(val listener: CommandListener, private val context: Context):
    DialogFragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: CommandsListAdapter? = null
    private var commandList: List<SshCommand> = listOf()
    var database: RoomDb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = RoomDb.getInstance(context)
        commandList = database?.mainDAO()!!.getAllCommands()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_command, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.command_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = CommandsListAdapter(context, commandList ,commandListener)
        recyclerView?.adapter = adapter

    }

    private val commandListener: CommandListener = object : CommandListener{
        override fun onCommandClicked(command: SshCommand) {
            listener.onCommandClicked(command)
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        val d = dialog
        if (d != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            d.window!!.setLayout(width, height)
        }
    }
}