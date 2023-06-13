package com.lttrung.notepro.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.addnote.AddNoteActivity
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.editnote.EditNoteActivity
import com.lttrung.notepro.ui.activities.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.NoteAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { _ ->

                }
            }
        }
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()
    private val noteAdapter by lazy {
        NoteAdapter(noteListener)
    }
    private val featureAdapter by lazy {
        FeatureAdapter()
    }

    private val noteListener by lazy {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.notesLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    noteAdapter.submitList(resource.data)
                    if (!ServiceUtils.isServiceRunning(this, ChatSocketService::class.java)) {
                        startService(Intent(this, ChatSocketService::class.java))
                    }
                }

                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(fabOnClickListener)
    }

    private fun initViews() {
        setContentView(binding.root)
        binding.rvFeatures.adapter = featureAdapter
        binding.rvNotes.adapter = noteAdapter

        featureAdapter.submitList(MainFeatures.get(this@MainActivity))
    }
}