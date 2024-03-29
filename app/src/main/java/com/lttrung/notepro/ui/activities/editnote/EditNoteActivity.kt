package com.lttrung.notepro.ui.activities.editnote

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.activities.viewmembers.ViewMembersActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.common.StartSnapHelper
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.DELETE_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDIT_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ACTION_TYPE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.AppConstant.Companion.THEME
import com.lttrung.notepro.utils.FeatureId
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.openGallery
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import com.lttrung.notepro.utils.requestPermissionToReadGallery
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val theme = resultIntent.getSerializableExtra(THEME) as Theme?
                    if (theme != null) {
                        note.theme = theme
                        return@registerForActivityResult
                    }
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    val tempList = note.images.toMutableList()
                    tempList.addAll(images)
                    note.images = tempList
                    viewModel.noteDetailsLiveData.postValue(note)
                }
            }
        }

    override val binding by lazy {
        ActivityEditNoteBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(object : ImageAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
                startActivity(Intent(
                    this@EditNoteActivity, ViewImageDetailsActivity::class.java
                ).apply {
                    putExtra(AppConstant.LIST_IMAGE, ListImage(imagesAdapter.currentList))
                })
            }

            override fun onDelete(image: Image) {
                viewModel.deleteImage(note, image)
            }
        })
    }
    private val featureAdapter by lazy {
        FeatureAdapter(object : FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {
                when (item.id) {
                    FeatureId.CAMERA -> {
                        if (requestPermissionToOpenCamera(this@EditNoteActivity)) {
                            openCamera(launcher)
                        }
                    }

                    FeatureId.GALLERY -> {
                        if (requestPermissionToReadGallery(this@EditNoteActivity)) {
                            openGallery(launcher, this@EditNoteActivity)
                        }
                    }

                    FeatureId.MEMBERS -> {
                        val viewMembersIntent =
                            Intent(this@EditNoteActivity, ViewMembersActivity::class.java).apply {
                                putExtra(NOTE, note)
                            }
                        startActivity(viewMembersIntent)
                    }

                    FeatureId.CHAT -> {
                        val chatIntent =
                            Intent(this@EditNoteActivity, ChatActivity::class.java).apply {
                                putExtra(NOTE, note)
                            }
                        launcher.launch(chatIntent)
                    }

                    FeatureId.DELETE -> {
                        DialogBuilder(this@EditNoteActivity)
                            .setNotice(R.string.ask_for_delete_note)
                            .addButtonRight {
                                viewModel.deleteNote(getNoteFromUi())
                            }
                            .addButtonLeft(R.string.cancel)
                            .setCanTouchOutside(false)
                            .build()
                            .show()
                    }

                    else -> {

                    }
                }
            }
        })
    }

    override val viewModel: EditNoteViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getNoteDetails(note.id)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK, Intent().apply {
            putExtra(NOTE_ACTION_TYPE, EDIT_NOTE)
            putExtra(NOTE, note)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == AppConstant.CAMERA_REQUEST) {
                openCamera(launcher)
            } else if (requestCode == AppConstant.READ_EXTERNAL_STORAGE_REQUEST) {
                openGallery(launcher, this@EditNoteActivity)
            }
        }
    }

    private fun getFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.CAMERA, R.drawable.ic_baseline_camera_alt_24),
            Feature(FeatureId.GALLERY, R.drawable.ic_baseline_photo_album_24),
            Feature(FeatureId.MEMBERS, R.drawable.ic_baseline_groups_24),
            Feature(FeatureId.CHAT, R.drawable.ic_baseline_message_24),
            Feature(FeatureId.DELETE, R.drawable.ic_baseline_delete_outline_24)
        )
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnSave.setOnClickListener {
            // Save note
            viewModel.editNote(getNoteFromUi())
        }
        binding.btnPin.apply {
            setOnClickListener {
                isSelected = !isSelected
                updatePinButtonStatus(isSelected)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.editNoteLiveData.observe(this) { editNote ->
            val resultIntent = Intent()
            resultIntent.putExtra(NOTE, editNote)
            resultIntent.putExtra(NOTE_ACTION_TYPE, EDIT_NOTE)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        viewModel.deleteNoteLiveData.observe(this) {
            socketService?.sendDeleteNoteMessage(note.id)

            val resultIntent = Intent()
            resultIntent.putExtra(NOTE, note)
            resultIntent.putExtra(NOTE_ACTION_TYPE, DELETE_NOTE)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        viewModel.noteDetailsLiveData.observe(this) { noteDetails ->
            intent.putExtra(NOTE, noteDetails)
            bindDataToViews()
        }
    }

    private fun bindDataToViews() {

        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
        }
        updatePinButtonStatus(note.isPin)
        imagesAdapter.submitList(note.images)
    }

    override fun initViews() {
        super.initViews()
        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
            rvImages.adapter = imagesAdapter
            rvFeatures.adapter = featureAdapter
            btnPin.isSelected = note.isPin
            updatePinButtonStatus(btnPin.isSelected)
        }

        StartSnapHelper().attachToRecyclerView(binding.rvImages)
        imagesAdapter.submitList(note.images)
        featureAdapter.submitList(getFeatures())
    }

    private fun getNoteFromUi(): Note {
        return Note(
            note.id,
            binding.edtNoteTitle.text!!.trim().toString(),
            binding.edtNoteDesc.text!!.trim().toString(),
            note.lastModified,
            binding.btnPin.isSelected,
            role = note.role,
            images = imagesAdapter.currentList
        )
    }

    private fun updatePinButtonStatus(isPin: Boolean) {
        val isPinDrawable =
            if (isPin) R.drawable.ic_baseline_push_pinned_24
            else R.drawable.ic_baseline_push_pin_24
        binding.btnPin.setImageResource(isPinDrawable)
    }
}