package com.lttrung.notepro.ui.notedetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityNoteDetailsBinding
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private val noteDetailsViewModel: NoteDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initAdapters()
        initData()
        initObservers()
    }

    private fun initViews() {
        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    private fun initAdapters() {
        imagesAdapter = ImagesAdapter()
        binding.rcvImages.adapter = imagesAdapter
        binding.rcvImages.layoutManager = CardSliderLayoutManager(this)
        CardSnapHelper().attachToRecyclerView(binding.rcvImages)
    }

    private fun initObservers() {
        noteDetailsViewModel.noteDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val note = resource.data
                    intent.putExtra(NOTE, note)
                    binding.edtNoteTitle.text = note.title
                    binding.edtNoteDesc.text = note.content
                    binding.tvLastModified.text = note.lastModified.toString()
                    imagesAdapter.submitList(note.images)
                }
                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        binding.edtNoteTitle.text = note.title
        binding.edtNoteDesc.text = note.content
        binding.tvLastModified.text = note.lastModified.toString()

        noteDetailsViewModel.getNoteDetails(note)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_members -> {
                // Start show members activity
                val showMembersIntent = Intent(this, ShowMembersActivity::class.java)
                val note = intent.getSerializableExtra(NOTE) as Note
                showMembersIntent.putExtra(NOTE, note)
                startActivity(showMembersIntent)
                true
            }
            else -> {
                onBackPressed()
                true
            }
        }
    }
}