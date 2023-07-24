package com.lttrung.notepro.ui.activities.notedetails

import android.content.Intent
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityNoteDetailsBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.activities.viewmembers.ViewMembersActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.FeatureId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : BaseActivity() {
    override val binding: ActivityNoteDetailsBinding by lazy {
        ActivityNoteDetailsBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(imageListener)
    }
    private val featureAdapter by lazy {
        FeatureAdapter(object : FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {
                when (item.id) {
                    FeatureId.MEMBERS -> {
                        val viewMembersIntent =
                            Intent(this@NoteDetailsActivity, ViewMembersActivity::class.java).apply {
                                putExtra(NOTE, note)
                            }
                        startActivity(viewMembersIntent)
                    }

                    FeatureId.CHAT -> {
                        val chatIntent =
                            Intent(this@NoteDetailsActivity, ChatActivity::class.java).apply {
                                putExtra(NOTE, note)
                            }
                        startActivity(chatIntent)
                    }

                    else -> {

                    }
                }
            }
        })
    }
    override val viewModel: NoteDetailsViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }

    override fun initViews() {
        super.initViews()
        binding.apply {
            edtNoteTitle.text = note.title
            edtNoteDesc.text = note.content
            rvFeatures.adapter = featureAdapter
        }

        featureAdapter.submitList(getFeatures())
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

    private fun getFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.MEMBERS, R.drawable.ic_baseline_groups_24),
            Feature(FeatureId.CHAT, R.drawable.ic_baseline_message_24),
        )
    }
}