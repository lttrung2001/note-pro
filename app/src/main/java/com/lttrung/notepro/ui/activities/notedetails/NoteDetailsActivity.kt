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
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.IMAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : BaseActivity() {
    override val binding: ActivityNoteDetailsBinding by lazy {
        ActivityNoteDetailsBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(imageListener)
    }
    override val viewModel: NoteDetailsViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }

    override fun initViews() {
        super.initViews()
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)

        viewModel.getNoteDetails(note)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.noteDetailsLiveData.observe(this) { note ->
            intent.putExtra(NOTE, note)
            binding.edtNoteTitle.setText(note.title)
            binding.edtNoteDesc.setText(note.content)
            imagesAdapter.submitList(note.images)
        }
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
    //                viewModel.editNote(note)
    //                val resultIntent = Intent()
    //                resultIntent.putExtra(EDITED_NOTE, note)
    //                setResult(RESULT_OK, resultIntent)
    //                finish()
    //            }

    private val imageListener: ImageAdapter.ImageListener by lazy {
        object : ImageAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
                startActivity(Intent(
                    this@NoteDetailsActivity, ViewImageDetailsActivity::class.java
                ).apply {
                    putExtra(AppConstant.LIST_IMAGE, ListImage(imagesAdapter.currentList))
                })
            }

            override fun onDelete(image: Image) {
                return
            }
        }
    }
}