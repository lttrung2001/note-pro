package com.lttrung.notepro.ui.base.adapters.note

import com.lttrung.notepro.database.data.networks.models.Note

interface NoteListener {
    fun onClick(note: Note)
}