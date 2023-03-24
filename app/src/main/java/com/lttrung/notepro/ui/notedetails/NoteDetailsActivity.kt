package com.lttrung.notepro.ui.notedetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.locals.entities.Note
import com.lttrung.notepro.databinding.ActivityNoteDetailsBinding
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.chat.ChatActivity
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var menu: Menu
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
                    Snackbar.make(
                        binding.root, resource.message,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
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
        menuInflater.inflate(R.menu.menu_note_details, menu)
        this.menu = menu!!
        val noteDetails = intent.getSerializableExtra(NOTE) as Note
        val pinButton = menu.getItem(0)
        pinButton.isChecked = noteDetails.isPin
        if (noteDetails.isPin) {
            pinButton.icon.setTint(resources.getColor(R.color.primary, theme))
        } else {
            pinButton.icon.setTint(resources.getColor(R.color.black, theme))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_pin -> {
                if (item.isChecked) {
                    item.icon.setTint(resources.getColor(R.color.black, theme))
                } else {
                    item.icon.setTint(resources.getColor(R.color.primary, theme))
                }
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_show_members -> {
                // Start show members activity
                val showMembersIntent = Intent(this, ShowMembersActivity::class.java)
                val note = intent.getSerializableExtra(NOTE) as Note
                showMembersIntent.putExtra(NOTE, note)
                startActivity(showMembersIntent)
                true
            }
            R.id.action_show_conservation -> {
                val note = intent.getSerializableExtra(NOTE) as Note
                val showConservationIntent =
                    Intent(this@NoteDetailsActivity, ChatActivity::class.java)
                showConservationIntent.putExtra(NOTE, note)
                startActivity(showConservationIntent)
                true
            }
            else -> {
                // Update pin here...
                val note = intent.getSerializableExtra(NOTE) as Note
                val isPin = menu.getItem(0).isChecked
                noteDetailsViewModel.updatePin(note.id, isPin)
                val resultIntent = Intent()
                resultIntent.putExtra(
                    EDITED_NOTE,
                    Note(
                        note.id,
                        note.title,
                        note.content,
                        note.lastModified,
                        isPin,
                        note.role,
                        note.images
                    )
                )
                setResult(RESULT_OK, resultIntent)
                finish()
                true
            }
        }
    }
}