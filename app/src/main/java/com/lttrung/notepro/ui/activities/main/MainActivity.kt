package com.lttrung.notepro.ui.activities.main

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.addnote.AddNoteActivity
import com.lttrung.notepro.ui.activities.changepassword.ChangePasswordActivity
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.editnote.EditNoteActivity
import com.lttrung.notepro.ui.activities.login.LoginActivity
import com.lttrung.notepro.ui.activities.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.activities.viewprofile.ViewProfileActivity
import com.lttrung.notepro.ui.adapters.MessageAdapter2
import com.lttrung.notepro.ui.adapters.NoteAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.ADD_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.DELETE_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDIT_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ACTION_TYPE
import com.lttrung.notepro.utils.ServiceUtils
import com.lttrung.notepro.utils.remove
import com.lttrung.notepro.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { i ->
                    val note = i.getSerializableExtra(NOTE) as Note
                    when (i.getIntExtra(NOTE_ACTION_TYPE, 0)) {
                        ADD_NOTE -> {
                            handleAddNoteResult(note)
                        }

                        EDIT_NOTE -> {
                            handleEditNoteResult(note)
                        }

                        DELETE_NOTE -> {
                            handleDeleteNoteResult(note)
                        }

                        else -> {

                        }
                    }
                }
            }
        }

    override val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override val viewModel: MainViewModel by viewModels()
    private val noteAdapter: NoteAdapter by lazy {
        NoteAdapter(noteListener)
    }
    private val messageAdapter by lazy {
        MessageAdapter2 {
            val chatIntent =
                Intent(this@MainActivity, ChatActivity::class.java).apply {
                    putExtra(NOTE, it)
                }
            launcher.launch(chatIntent)
        }
    }

    private val noteListener: NoteAdapter.NoteListener by lazy {
        object : NoteAdapter.NoteListener {
            override fun onClick(note: Note) {
                if (note.hasEditPermission()) {
                    val editNoteIntent = Intent(this@MainActivity, EditNoteActivity::class.java)
                    editNoteIntent.putExtra(NOTE, note)
                    launcher.launch(editNoteIntent)
                } else {
                    val noteDetailsIntent =
                        Intent(this@MainActivity, NoteDetailsActivity::class.java)
                    noteDetailsIntent.putExtra(NOTE, note)
                    launcher.launch(noteDetailsIntent)
                }
            }
        }
    }

    private val fabOnClickListener by lazy {
        View.OnClickListener {
            val addNoteIntent = Intent(this, AddNoteActivity::class.java)
            launcher.launch(addNoteIntent)
        }
    }

    private val viewProfileListener by lazy {
        View.OnClickListener {
            val viewProfileIntent = Intent(this, ViewProfileActivity::class.java)
            startActivity(viewProfileIntent)
        }
    }

    private val changePasswordListener by lazy {
        View.OnClickListener {
            val changePasswordIntent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(changePasswordIntent)
        }
    }

    private val logoutOnClickListener by lazy {
        View.OnClickListener {
            viewModel.logout()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.searchBar.clearFocus()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.notesLiveData.observe(this) {
            viewModel.listNote.removeAll { it.id == "" }
            // Fix sort
            viewModel.listNote.sortBy { !it.isPin }
            val firstNormalNoteIdx = viewModel.listNote.indexOfFirst { !it.isPin }
            if (firstNormalNoteIdx != -1) {
                viewModel.listNote.add(firstNormalNoteIdx, Note("", getString(R.string.unpin)))
                if (firstNormalNoteIdx > 0) {
                    viewModel.listNote.add(0, Note("", getString(R.string.pinned)))
                }
            }
            noteAdapter.submitList(viewModel.listNote)
            messageAdapter.submitList(viewModel.listNote.filter {
                it.lastMessage != null
            })
            try {
                if (!ServiceUtils.isServiceRunning(this, ChatSocketService::class.java)) {
                    startService(Intent(this, ChatSocketService::class.java))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModel.userLiveData.observe(this) { user ->
            if (user == null) {
                viewModel.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            binding.tvName.text = user.fullName ?: ""
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            fab.setOnClickListener(fabOnClickListener)
            searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.toString().isEmpty()) {
                        noteAdapter.submitList(viewModel.listNote)
                    } else {
                        val filteredNotes = viewModel.listNote.filter {
                            it.title.contains(newText.toString()) or it.content.contains(newText.toString())
                        }
                        noteAdapter.submitList(filteredNotes)
                    }
                    return true
                }
            })
            bottomNavView.background = null
            bottomNavView.menu[2].isEnabled = false
            bottomNavView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.action_view_notes -> {
                        llNotes.show()
                        llSettings.remove()
                        llMessages.remove()
                    }
                    R.id.action_settings -> {
                        llNotes.remove()
                        llSettings.show()
                        llMessages.remove()
                    }
                    R.id.action_message -> {
                        llNotes.remove()
                        llSettings.remove()
                        llMessages.show()
                    }
                    R.id.action_profile -> {
                        startActivity(Intent(this@MainActivity, ViewProfileActivity::class.java))
                    }
                    else -> {

                    }
                }
                return@setOnItemSelectedListener true
            }

            btnViewProfile.setOnClickListener(viewProfileListener)
            btnChangePassword.setOnClickListener(changePasswordListener)
            btnLogout.setOnClickListener(logoutOnClickListener)
        }
    }

    override fun initViews() {
        super.initViews()
        binding.rvNotes.adapter = noteAdapter
        binding.rvMessages.adapter = messageAdapter
    }

    private fun handleDeleteNoteResult(note: Note) {
        val pos = viewModel.listNote.indexOfFirst { it.id == note.id }
        viewModel.listNote.removeAt(pos)
        noteAdapter.submitList(viewModel.listNote)
        noteAdapter.notifyItemRemoved(pos)
    }

    private fun handleEditNoteResult(note: Note) {
        val pos = viewModel.listNote.indexOfFirst { it.id == note.id }
        if (note.isPin != viewModel.listNote[pos].isPin) {
            viewModel.listNote.removeAt(pos)
            if (note.isPin) {
                val firstIdx = viewModel.listNote.indexOfFirst { it.isPin && it.id != "" }
                if (firstIdx != -1) {
                    viewModel.listNote.add(firstIdx, note)
                    noteAdapter.notifyItemInserted(firstIdx)
                } else {
                    viewModel.listNote.add(0, Note("", getString(R.string.pinned)))
                    viewModel.listNote.add(1, note)
                    noteAdapter.submitList(viewModel.listNote)
                    noteAdapter.notifyItemRangeInserted(0, 2)
                }
            } else {
                val firstIdx = viewModel.listNote.indexOfFirst { !it.isPin && it.id != "" }
                if (firstIdx != -1) {
                    viewModel.listNote.add(firstIdx, note)
                    noteAdapter.notifyItemInserted(firstIdx)
                } else {
                    viewModel.listNote.add(Note("", getString(R.string.unpin)))
                    viewModel.listNote.add(note)
                    noteAdapter.submitList(viewModel.listNote)
                    noteAdapter.notifyItemRangeInserted(
                        noteAdapter.itemCount - 2,
                        noteAdapter.itemCount
                    )
                }
            }
        } else {
            val firstIdx = viewModel.listNote.indexOfFirst { it.isPin == note.isPin && it.id != "" }
            if (firstIdx != -1) {
                viewModel.listNote.add(firstIdx, note)
                noteAdapter.notifyItemInserted(firstIdx)
            }
        }
        noteAdapter.submitList(viewModel.listNote.apply {
            removeAt(pos + 1)
        })
        noteAdapter.notifyItemRemoved(pos + 1)
    }

    private fun handleAddNoteResult(note: Note) {
        val firstNoteIdx = if (note.isPin) {
            viewModel.listNote.indexOfFirst { it.isPin && it.id != "" }
        } else {
            viewModel.listNote.indexOfFirst { !it.isPin && it.id != "" }
        }
        if (firstNoteIdx != -1) {
            viewModel.listNote.add(firstNoteIdx, note)
            noteAdapter.notifyItemInserted(firstNoteIdx)
        } else {
            if (note.isPin) {
                viewModel.listNote.add(0, Note("", getString(R.string.pinned)))
                viewModel.listNote.add(1, note)
                noteAdapter.submitList(viewModel.listNote)
                noteAdapter.notifyItemRangeInserted(0, 2)
            } else {
                viewModel.listNote.add(Note("", getString(R.string.unpin)))
                viewModel.listNote.add(note)
                noteAdapter.submitList(viewModel.listNote)
                noteAdapter.notifyItemRangeInserted(
                    noteAdapter.itemCount - 2,
                    noteAdapter.itemCount
                )
            }
        }
    }
}