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
import androidx.appcompat.widget.SearchView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.base.adapters.note.NoteAdapter
import com.lttrung.notepro.ui.base.adapters.note.NoteListener
import com.lttrung.notepro.ui.editnote.EditNoteActivity
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
    private lateinit var searchView: SearchView
    private val mainViewModel: MainViewModel by viewModels()

    private val noteListener: NoteListener by lazy {
        object : NoteListener {
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

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val addNoteIntent = Intent(this, AddNoteActivity::class.java)
            launcher.launch(addNoteIntent)
        }
    }

    private val fabOnScrollChangeListener: View.OnScrollChangeListener by lazy {
        View.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
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

    private val searchListener: SearchView.OnQueryTextListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val filterPinNotes = pinNotesAdapter.currentList.filter {
                        it.title.contains(query) || it.content.contains(query)
                    }
                    pinNotesAdapter.submitList(filterPinNotes)
                    val filterNormalNotes = normalNotesAdapter.currentList.filter {
                        it.title.contains(query) || it.content.contains(query)
                    }
                    normalNotesAdapter.submitList(filterNormalNotes)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText.isBlank()) {
                    val resource = mainViewModel.getNotes.value
                    if (resource is Resource.Success) {
                        val pinNotes = resource.data.filter {
                            it.isPin
                        }
                        val normalNotes = resource.data.filter {
                            !it.isPin
                        }
                        pinNotesAdapter.submitList(pinNotes)
                        normalNotesAdapter.submitList(normalNotes)
                    }
                }
                return true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
        initObservers()
        if (mainViewModel.getNotes.value == null) {
            mainViewModel.getNotes()
        }
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
                    }.sortedByDescending {
                        it.lastModified
                    }
                    val normalNotes = resource.data.filter {
                        !it.isPin
                    }.sortedByDescending {
                        it.lastModified
                    }
                    pinNotesAdapter.submitList(pinNotes)
                    normalNotesAdapter.submitList(normalNotes)

                    val service = (application as NoteProApplication).chatService
                    if (service == null) {
                        startService(Intent(this, ChatSocketService::class.java))
                    }
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
                    Snackbar.make(binding.root, resource.t.message.toString(),
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
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
        binding.nestedScrollView.setOnScrollChangeListener(fabOnScrollChangeListener)
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
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchView = menu?.getItem(0)?.actionView as SearchView
        searchView.setOnQueryTextListener(searchListener)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { intent ->
                    val addedNote = intent.getSerializableExtra(NOTE) as Note?
                    val editedNote = intent.getSerializableExtra(EDITED_NOTE) as Note?
                    val deletedNote = intent.getSerializableExtra(DELETED_NOTE) as Note?
                    addedNote?.let { note ->
                        val previousResource = mainViewModel.getNotes.value as Resource.Success<List<Note>>
                        val notes = previousResource.data.toMutableList()
                        notes.add(note)
                        mainViewModel.getNotes.postValue(Resource.Success(notes))
                    }
                    editedNote?.let { note ->
                        val previousResource = mainViewModel.getNotes.value as Resource.Success<List<Note>>
                        val notes = previousResource.data.toMutableList()
                        val oldNote = notes.find {
                            it.id == note.id
                        }
                        notes.remove(oldNote)
                        notes.add(note)
                        mainViewModel.getNotes.postValue(Resource.Success(notes))
                    }
                    deletedNote?.let { note ->
                        val previousResource = mainViewModel.getNotes.value as Resource.Success<List<Note>>
                        val notes = previousResource.data.toMutableList().filter {
                            it.id != note.id
                        }
                        mainViewModel.getNotes.postValue(Resource.Success(notes))
                    }
                }
            }
        }

}