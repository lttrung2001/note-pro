package com.lttrung.notepro.ui.notedetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityNoteDetailsBinding
import com.lttrung.notepro.ui.editnote.EditNoteActivity
import com.lttrung.notepro.ui.notedetails.adapters.ImagesAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private val noteDetailsViewModel: NoteDetailsViewModel by viewModels()

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initListeners()
        initAdapters()
        initData()
        initObservers()

        setContentView(binding.root)
    }

    private fun initAdapters() {
        imagesAdapter = ImagesAdapter()
        binding.rcvImages.adapter = imagesAdapter

        binding.tvImages.visibility = View.GONE
        binding.rcvImages.visibility = View.GONE
    }

    private fun initObservers() {
        noteDetailsViewModel.noteDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val note = resource.data
                    binding.edtNoteTitle.text = note.title
                    binding.edtNoteDesc.text = note.content
                    binding.tvLastModified.text = note.lastModified.toString()

                    if (!note.images.isNullOrEmpty()) {
                        binding.tvImages.visibility = View.VISIBLE
                        binding.rcvImages.visibility = View.VISIBLE
                        imagesAdapter.submitList(note.images)
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        binding.edtNoteTitle.text = note.title
        binding.edtNoteDesc.text = note.content
        binding.tvLastModified.text = note.lastModified.toString()
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(fabOnClickListener)
        binding.fab.setOnScrollChangeListener(fabOnScrollChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_pin -> {
                val pinnedDrawable =
                    resources.getDrawable(R.drawable.ic_baseline_push_pinned_24, theme)
                val unPinDrawable = resources.getDrawable(R.drawable.ic_baseline_push_pin_24, theme)
                item.icon = pinnedDrawable
                true
            }
            R.id.action_show_members -> {
                // Start show members activity
                startActivity(Intent(this, ShowMembersActivity::class.java))
                true
            }
            else -> {
                onBackPressed()
                true
            }
        }
    }
}