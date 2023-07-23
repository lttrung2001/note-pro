package com.lttrung.notepro.ui.activities.main

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.get
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.addnote.AddNoteActivity
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.editnote.EditNoteActivity
import com.lttrung.notepro.ui.activities.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.activities.setting.SettingActivity
import com.lttrung.notepro.ui.activities.viewprofile.ViewProfileActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.NoteAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.utils.AppConstant.Companion.ADD_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.DELETE_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDIT_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ACTION_TYPE
import com.lttrung.notepro.utils.FeatureId
import com.lttrung.notepro.utils.ServiceUtils
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
                            viewModel.listNote.add(0, note)
                            noteAdapter.submitList(viewModel.listNote)
                        }

                        EDIT_NOTE -> {
                            viewModel.listNote.removeIf { it.id == note.id }
                            viewModel.listNote.add(0, note)
                            noteAdapter.submitList(viewModel.listNote)
                        }

                        DELETE_NOTE -> {
                            viewModel.listNote.removeIf { it.id == note.id }
                            noteAdapter.submitList(viewModel.listNote)
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

    private val searchWatcher by lazy {
        object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {
                    noteAdapter.submitList(viewModel.listNote)
                } else {
                    val filteredNotes = viewModel.listNote.filter {
                        it.title.contains(p0.toString()) or it.content.contains(p0.toString())
                    }
                    noteAdapter.submitList(filteredNotes)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.searchBar.clearFocus()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.notesLiveData.observe(this) { notes ->
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
            if (!ServiceUtils.isServiceRunning(this, ChatSocketService::class.java)) {
                startService(Intent(this, ChatSocketService::class.java))
            }
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            fab.setOnClickListener(fabOnClickListener)
            searchBar.addTextChangedListener(searchWatcher)
            searchBar.setOnFocusChangeListener { _, b ->
                if (!b) {
                    noteAdapter.submitList(viewModel.listNote)
                }
            }
            bottomNavView.background = null
            bottomNavView.menu[2].isEnabled = false
            bottomNavView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.action_settings -> {
                        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    }
                    R.id.action_profile -> {
                        startActivity(Intent(this@MainActivity, ViewProfileActivity::class.java))
                    }
                    else -> {

                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun initViews() {
        super.initViews()
        binding.rvNotes.adapter = noteAdapter
    }
}