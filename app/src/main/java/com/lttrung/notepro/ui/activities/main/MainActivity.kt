package com.lttrung.notepro.ui.activities.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.paging.Config
import com.google.android.material.snackbar.Snackbar
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
import timber.log.Timber

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
                            viewModel.notesLiveData.postValue(noteAdapter.currentList.toMutableList()
                                .apply {
                                    add(0, note)
                                })
                        }

                        EDIT_NOTE -> {
                            viewModel.notesLiveData.postValue(noteAdapter.currentList.toMutableList()
                                .apply {
                                    removeIf { it.id == note.id }
                                    add(0, note)
                                })
                        }

                        DELETE_NOTE -> {
                            viewModel.notesLiveData.postValue(noteAdapter.currentList.toMutableList()
                                .apply {
                                    removeIf { it.id == note.id }
                                })
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
    private val featureAdapter by lazy {
        FeatureAdapter(object : FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {
                when (item.id) {
                    FeatureId.INFO -> {
                        startActivity(Intent(this@MainActivity, ViewProfileActivity::class.java))
                    }

                    FeatureId.SETTING -> {
                        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    }

                    else -> {

                    }
                }
            }
        })
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
                val allNotes = viewModel.notesLiveData.value.orEmpty()
                if (p0.toString().isEmpty()) {
                    noteAdapter.submitList(allNotes)
                } else {
                    val filteredNotes = allNotes.filter {
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

    private fun getMainFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.SETTING, R.drawable.ic_baseline_settings_24),
            Feature(FeatureId.INFO, R.drawable.ic_baseline_person_24)
        )
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.notesLiveData.observe(this) { notes ->
            val notesTemp = notes.toMutableList()
            notesTemp.removeAll { it.id == "" }
            // Fix sort
            notesTemp.sortBy { !it.isPin }
            val firstNormalNoteIdx = notesTemp.indexOfFirst { !it.isPin }
            if (firstNormalNoteIdx != -1) {
                notesTemp.add(firstNormalNoteIdx, Note("", getString(R.string.unpin)))
                if (firstNormalNoteIdx > 0) {
                    notesTemp.add(0, Note("", getString(R.string.pinned)))
                }
            }
            noteAdapter.submitList(notesTemp)
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
            searchBar.setOnFocusChangeListener { view, b ->
                if (!b) {
                    noteAdapter.submitList(viewModel.notesLiveData.value.orEmpty())
                }
            }
        }
    }

    override fun initViews() {
        super.initViews()
        binding.apply {
            rvFeatures.adapter = featureAdapter
            rvNotes.adapter = noteAdapter
        }

        featureAdapter.submitList(getMainFeatures())
    }
}