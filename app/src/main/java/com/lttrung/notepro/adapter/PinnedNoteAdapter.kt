package com.lttrung.notepro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.model.Note

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
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val isPin: Boolean = true

        fun bind(note: Note) {
            tvId.text = note.id
            tvTitle.text = note.title
            tvDescription.text = note.description
        }

        companion object {
            fun bind(view: View): Note {
                return Note(
                    view.findViewById<TextView>(R.id.tv_id).text.toString(),
                    view.findViewById<TextView>(R.id.tv_title).text.toString(),
                    view.findViewById<TextView>(R.id.tv_description).text.toString(),
                    isPin = true
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