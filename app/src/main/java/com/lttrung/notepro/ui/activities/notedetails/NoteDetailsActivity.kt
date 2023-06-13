package com.lttrung.notepro.ui.activities.notedetails

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lttrung.notepro.databinding.ActivityNoteDetailsBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.IMAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : AppCompatActivity() {
    private val binding: ActivityNoteDetailsBinding by lazy {
        ActivityNoteDetailsBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(imageListener)
    }
    private val noteDetailsViewModel: NoteDetailsViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initData()
        initObservers()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    private fun initObservers() {
        noteDetailsViewModel.noteDetailsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    val note = resource.data
                    intent.putExtra(NOTE, note)
                    binding.edtNoteTitle.setText(note.title)
                    binding.edtNoteDesc.setText(note.content)
                    imagesAdapter.submitList(note.images)
                }

                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initData() {
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)

        noteDetailsViewModel.getNoteDetails(note)
    }

    // R.id.action_pin -> {
    //                if (item.isChecked) {
    //                    item.icon.setTint(resources.getColor(R.color.black, theme))
    //                } else {
    //                    item.icon.setTint(resources.getColor(R.color.primary, theme))
    //                }
    //                item.isChecked = !item.isChecked
    //            }
    //            R.id.action_show_conservation -> {
    //                val showConservationIntent =
    //                    Intent(this@NoteDetailsActivity, ChatActivity::class.java)
    //                showConservationIntent.putExtra(ROOM_ID, note.id)
    //                showConservationIntent.putExtra(NOTE, note)
    //                startActivity(showConservationIntent)
    //            }
    //            else -> {
    //                note.isPin = false
    //                noteDetailsViewModel.editNote(note)
    //                val resultIntent = Intent()
    //                resultIntent.putExtra(EDITED_NOTE, note)
    //                setResult(RESULT_OK, resultIntent)
    //                finish()
    //            }

    private val imageListener: ImageAdapter.ImageListener by lazy {
        object : ImageAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
                startActivity(
                    Intent(
                        this@NoteDetailsActivity,
                        ViewImageDetailsActivity::class.java
                    ).apply {
                        putExtra(IMAGES_JSON, Gson().toJson(imagesAdapter.currentList))
                    }
                )
            }

            override fun onDelete(image: Image) {
                return
            }
        }
    }
}