package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.NoteItemBinding
import com.lttrung.notepro.databinding.TitleItemBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ITEM
import com.lttrung.notepro.utils.AppConstant.Companion.TITLE_ITEM

class NoteAdapter(private val noteListener: NoteListener) :
    ListAdapter<Note, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLE_ITEM -> {
                TitleViewHolder(TitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                NoteViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = getItem(position)
        when (getItemViewType(position)) {
            TITLE_ITEM -> {
                (holder as TitleViewHolder).bind(note)
            }
            else -> {
                (holder as NoteViewHolder).bind(note, noteListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).id == "") TITLE_ITEM else NOTE_ITEM

    interface NoteListener {
        fun onClick(note: Note)
    }

    class NoteViewHolder(val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, noteListener: NoteListener) {
            binding.apply {
                tvId.text = note.id
                tvTitle.text = note.title
                tvContent.text = note.content
                tvLastModified.text = note.lastModified.toString()
                tvIsPin.text = note.isPin.toString()
                tvRole.text = note.role
                root.setOnClickListener { noteListener.onClick(note) }
            }
        }
    }

    class TitleViewHolder(val binding: TitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.root.text = note.title
        }
    }
}