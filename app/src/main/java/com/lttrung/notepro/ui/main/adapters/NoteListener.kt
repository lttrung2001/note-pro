package com.lttrung.notepro.ui.main.adapters

import com.lttrung.notepro.database.data.networks.models.Note

interface NoteListener {
    fun onClick(note: Note)
}