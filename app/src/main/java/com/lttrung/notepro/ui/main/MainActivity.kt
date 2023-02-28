package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.main.adapters.NoteAdapter
import com.lttrung.notepro.ui.main.adapters.NoteViewHolder
import com.lttrung.notepro.ui.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.setting.SettingActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pinnedNoteAdapter: NoteAdapter
    private val mainViewModel: MainViewModel by viewModels()

    private val onClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val note = NoteViewHolder.bind(view)
            val intent = Intent(this, NoteDetailsActivity::class.java)
            val bundle = Bundle()

            bundle.putSerializable(AppConstant.NOTE, note)
            intent.putExtras(bundle)

            startActivityIfNeeded(intent, AppConstant.SHOW_NOTE_DETAIL_REQUEST)
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

        setContentView(binding.root)
    }

    private fun initObservers() {
        mainViewModel.getNotes.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    pinnedNoteAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initAdapters() {
        pinnedNoteAdapter = NoteAdapter(onClickListener)
        binding.rcvPinnedNotes.adapter = pinnedNoteAdapter
        binding.rcvOtherNotes.adapter = pinnedNoteAdapter
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(fabOnClickListener)
        binding.btnSearch.setOnClickListener(btnSearchOnClickListener)
        binding.scrollView.setOnScrollChangeListener(fabOnScrollChangeListener)
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.setLogo(R.drawable.ic_baseline_sticky_note_2_24)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
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