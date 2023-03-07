package com.lttrung.notepro.ui.base.adapters.note

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
}