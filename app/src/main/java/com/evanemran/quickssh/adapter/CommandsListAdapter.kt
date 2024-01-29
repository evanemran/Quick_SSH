package com.evanemran.quickssh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evanemran.quickssh.R
import com.evanemran.quickssh.dialog.CommandListener
import com.evanemran.quickssh.model.SshCommand

class CommandsListAdapter(private val context: Context, private val list: List<SshCommand>, private val listener: CommandListener)
    : RecyclerView.Adapter<CommandsListHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandsListHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.list_commands, parent, false)
        return CommandsListHolder(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommandsListHolder, position: Int) {
        val item = list[position]

        holder.textViewCommand.text = item.command
        holder.commandContainer.setOnClickListener {
            listener.onCommandClicked(item)
        }
    }
}


class CommandsListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textViewCommand: TextView = itemView.findViewById(R.id.textView_command)
    var commandContainer: LinearLayout = itemView.findViewById(R.id.command_item_container)
}