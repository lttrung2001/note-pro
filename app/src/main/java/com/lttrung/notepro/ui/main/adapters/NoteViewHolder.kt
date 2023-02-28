package com.lttrung.notepro.ui.main.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note

class NoteViewHolder(itemView: View) :
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
                view.findViewById<TextView>(R.id.tv_last_modified).text.toString().toLong(),
                view.findViewById<TextView>(R.id.tv_is_pin).text.toString().toBoolean(),
                view.findViewById<TextView>(R.id.tv_role).text.toString(),
                null
            )
        }
    }
}