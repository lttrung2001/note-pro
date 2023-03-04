package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.main.adapters.NoteAdapter
import com.lttrung.notepro.ui.main.adapters.NoteListener
import com.lttrung.notepro.ui.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.setting.SettingActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pinnedNoteAdapter: NoteAdapter
    private val mainViewModel: MainViewModel by viewModels()

    private val noteListener: NoteListener by lazy {
        object : NoteListener {
            override fun onClick(note: Note) {
                val noteDetailsIntent = Intent(this@MainActivity, NoteDetailsActivity::class.java)
                noteDetailsIntent.putExtra(NOTE, note)
                startActivityIfNeeded(noteDetailsIntent, AppConstant.SHOW_NOTE_DETAIL_REQUEST)
            }
        }
    }

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
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
        mainViewModel.getNotes()
    }

    private fun initObservers() {
        mainViewModel.getNotes.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    pinnedNoteAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initAdapters() {
        pinnedNoteAdapter = NoteAdapter(noteListener)
        binding.rcvPinnedNotes.adapter = pinnedNoteAdapter
        binding.rcvOtherNotes.adapter = pinnedNoteAdapter
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

}