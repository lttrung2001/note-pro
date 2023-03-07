package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.base.adapters.note.NoteAdapter
import com.lttrung.notepro.ui.base.adapters.note.NoteListener
import com.lttrung.notepro.ui.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.setting.SettingActivity
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pinNotesAdapter: NoteAdapter
    private lateinit var normalNotesAdapter: NoteAdapter
    private val mainViewModel: MainViewModel by viewModels()

    private val noteListener: NoteListener by lazy {
        object : NoteListener {
            override fun onClick(note: Note) {
                val noteDetailsIntent = Intent(this@MainActivity, NoteDetailsActivity::class.java)
                noteDetailsIntent.putExtra(NOTE, note)
                launcher.launch(noteDetailsIntent)
            }
        }
    }

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val addNoteIntent = Intent(this, AddNoteActivity::class.java)
            launcher.launch(addNoteIntent)
        }
    }

    private val fabOnScrollChangeListener: View.OnScrollChangeListener by lazy {
        View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY == 0 && scrollY > 0) {
                binding.fab.shrink()
            } else if (scrollY == 0) {
                binding.fab.extend()
            }
        }
    }

    private val refreshListener: SwipeRefreshLayout.OnRefreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            mainViewModel.getNotes()
        }
    }

    private val btnSearchOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.getNotes.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    val pinNotes = resource.data.filter {
                        it.isPin
                    }
                    val normalNotes = resource.data.filter {
                        !it.isPin
                    }
                    pinNotesAdapter.submitList(pinNotes)
                    normalNotesAdapter.submitList(normalNotes)
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initAdapters() {
        pinNotesAdapter = NoteAdapter(noteListener)
        normalNotesAdapter = NoteAdapter(noteListener)
        binding.rcvPinnedNotes.adapter = pinNotesAdapter
        binding.rcvOtherNotes.adapter = normalNotesAdapter
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(fabOnClickListener)
        binding.edtSearch.setOnClickListener(btnSearchOnClickListener)
        binding.refreshLayout.setOnScrollChangeListener(fabOnScrollChangeListener)
        binding.refreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.setLogo(R.drawable.ic_baseline_sticky_note_2_24)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { intent ->
                    val editedNote = intent.getSerializableExtra(EDITED_NOTE) as Note?
                    val deletedNote = intent.getSerializableExtra(DELETED_NOTE) as Note?
                    editedNote?.let { note ->
                            pinNotesAdapter.currentList.find {
                                it.id == note.id
                            }?.let { findingNote ->
                                val pinNotes = pinNotesAdapter.currentList.toMutableList()
                                pinNotes.remove(findingNote)
                                pinNotesAdapter.submitList(pinNotes)
                                val normalNotes = normalNotesAdapter.currentList.toMutableList()
                                normalNotes.add(note)
                                normalNotesAdapter.submitList(normalNotes)
                            }
                            normalNotesAdapter.currentList.find {
                                it.id == note.id
                            }?.let { findingNote ->
                                val normalNotes = normalNotesAdapter.currentList.toMutableList()
                                normalNotes.remove(findingNote)
                                normalNotesAdapter.submitList(normalNotes)
                                val pinNotes = normalNotesAdapter.currentList.toMutableList()
                                pinNotes.add(note)
                                normalNotesAdapter.submitList(pinNotes)
                            }
                        }
                    deletedNote?.let { note ->
                        val pinNotes = pinNotesAdapter.currentList.toMutableList()
                        val normalNotes = normalNotesAdapter.currentList.toMutableList()
                        pinNotesAdapter.submitList(pinNotes.filter {
                            it.id != note.id
                        })
                        normalNotesAdapter.submitList(normalNotes.filter {
                            it.id != note.id
                        })
                    }
                }
            }
        }

}