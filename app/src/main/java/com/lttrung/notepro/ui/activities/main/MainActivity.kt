package com.lttrung.notepro.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
                            noteAdapter.submitList(noteAdapter.currentList.toMutableList().apply {
                                add(0, note)
                            })
                        }

                        EDIT_NOTE -> {
                            noteAdapter.submitList(noteAdapter.currentList.toMutableList().apply {
                                removeIf { it.id == note.id }
                                add(0, note)
                            })
                        }

                        DELETE_NOTE -> {
                            noteAdapter.submitList(noteAdapter.currentList.toMutableList().apply {
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

    private fun getMainFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.SETTING, R.drawable.ic_baseline_settings_24),
            Feature(FeatureId.INFO, R.drawable.ic_baseline_person_24)
        )
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.notesLiveData.observe(this) { notes ->
            noteAdapter.submitList(notes)
            if (!ServiceUtils.isServiceRunning(this, ChatSocketService::class.java)) {
                startService(Intent(this, ChatSocketService::class.java))
            }
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.fab.setOnClickListener(fabOnClickListener)
    }

    override fun initViews() {
        super.initViews()
        binding.rvFeatures.adapter = featureAdapter
        binding.rvNotes.adapter = noteAdapter

        featureAdapter.submitList(getMainFeatures())
    }
}