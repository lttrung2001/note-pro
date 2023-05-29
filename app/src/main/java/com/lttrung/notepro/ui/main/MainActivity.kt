package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.base.adapters.note.NoteAdapter
import com.lttrung.notepro.ui.base.adapters.note.NoteListener
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.editnote.EditNoteActivity
import com.lttrung.notepro.ui.main.MainViewModel.Companion.GET_ARCHIVED_NOTES
import com.lttrung.notepro.ui.main.MainViewModel.Companion.GET_CURRENT_NOTES
import com.lttrung.notepro.ui.main.MainViewModel.Companion.GET_REMOVED_NOTES
import com.lttrung.notepro.ui.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.setting.SettingActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()
    private val pinNotesAdapter: NoteAdapter by lazy {
        val adapter = NoteAdapter(noteListener)
        binding.rcvPinnedNotes.adapter = adapter
        adapter
    }
    private val normalNotesAdapter: NoteAdapter by lazy {
        val adapter = NoteAdapter(noteListener)
        binding.rcvOtherNotes.adapter = adapter
        adapter
    }
    private val categoryAdapter: ArrayAdapter<String> by lazy {
        val data = ArrayList<String>()
        data.add("Current")
        data.add("Archived")
        data.add("Removed")

        val adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1)
        adapter.addAll(data)
        adapter.notifyDataSetChanged()
        adapter
    }

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
            mainViewModel.getNotes(GET_CURRENT_NOTES)
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
                        val pinNotes = filterNotes(resource.data, isPin = true)
                        val normalNotes = filterNotes(resource.data, isPin = false)
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
                    val currentNotes = resource.data
                    val pinNotes = filterNotes(currentNotes, isPin = true)
                    val normalNotes = filterNotes(currentNotes, isPin = false)
                    pinNotesAdapter.submitList(pinNotes)
                    normalNotesAdapter.submitList(normalNotes)

                    if (!ServiceUtils.isServiceRunning(this, ChatSocketService::class.java)) {
                        startService(Intent(this, ChatSocketService::class.java))
                    }
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(fabOnClickListener)
        binding.nestedScrollView.setOnScrollChangeListener(fabOnScrollChangeListener)
        binding.refreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.dashboard)

        binding.spinnerCategories.adapter = categoryAdapter
        binding.spinnerCategories.setSelection(0, true)
        binding.spinnerCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (p2) {
                        0 -> {

                            mainViewModel.getNotes(GET_CURRENT_NOTES)
                        }
                        1 -> {
                            mainViewModel.getNotes(GET_ARCHIVED_NOTES)
                        }
                        2 -> {
                            mainViewModel.getNotes(GET_REMOVED_NOTES)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
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
                    fetchData()
                }
            }
        }

    private fun filterNotes(list: List<Note>, isPin: Boolean): List<Note> {
        return list.filter {
            it.isPin == isPin
        }
    }

    private fun fetchData() {
        when (binding.spinnerCategories.selectedItemPosition) {
            0 -> {
                mainViewModel.getNotes(GET_CURRENT_NOTES)
            }
            1 -> {
                mainViewModel.getNotes(GET_ARCHIVED_NOTES)
            }
            2 -> {
                mainViewModel.getNotes(GET_REMOVED_NOTES)
            }
        }
    }
}