package com.lttrung.notepro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note

class PinnedNoteAdapter(private val onClickListener: View.OnClickListener) :
    ListAdapter<Note, PinnedNoteAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }) {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_id)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvLastModified: TextView = itemView.findViewById(R.id.tv_last_modified)
        private val tvIsPin: TextView = itemView.findViewById(R.id.tv_is_pin)
        private val tvRole: TextView = itemView.findViewById(R.id.tv_role)

        fun bind(note: Note) {
            tvId.text = note.id
            tvTitle.text = note.title
            tvContent.text = note.content
            tvLastModified.text = note.lastModified.toString()
            tvIsPin.text = note.isPin.toString()
            tvRole.text = note.role
        }

        companion object {
            fun bind(view: View): Note {
                return Note(
                    view.findViewById<TextView>(R.id.tv_id).text.toString(),
                    view.findViewById<TextView>(R.id.tv_title).text.toString(),
                    view.findViewById<TextView>(R.id.tv_content).text.toString(),
                    view.findViewById<TextView>(R.id.tv_last_modified).text.toString().toInt(),
                    view.findViewById<TextView>(R.id.tv_is_pin).text.toString().toBoolean(),
                    view.findViewById<TextView>(R.id.tv_role).text.toString(),
                    null
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { view ->
            onClickListener.onClick(view)
        }
    }
}