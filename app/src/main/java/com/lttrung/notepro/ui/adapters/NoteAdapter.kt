package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Note

class NoteAdapter(private val noteListener: NoteListener) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note, noteListener)
    }

    interface NoteListener {
        fun onClick(note: Note)
    }

    class NoteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_id)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvLastModified: TextView = itemView.findViewById(R.id.tv_last_modified)
        private val tvIsPin: TextView = itemView.findViewById(R.id.tv_is_pin)
        private val tvRole: TextView = itemView.findViewById(R.id.tv_role)

        fun bind(note: Note, noteListener: NoteListener) {
            tvId.text = note.id
            tvTitle.text = note.title
            tvContent.text = note.content
            tvLastModified.text = note.lastModified.toString()
            tvIsPin.text = note.isPin.toString()
            tvRole.text = note.role
            itemView.setOnClickListener { noteListener.onClick(note) }
        }
    }
}