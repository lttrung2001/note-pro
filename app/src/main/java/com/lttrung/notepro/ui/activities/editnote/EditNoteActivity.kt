package com.lttrung.notepro.ui.activities.editnote

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.activities.viewmembers.ViewMembersActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.common.StartSnapHelper
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.DELETE_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDIT_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ACTION_TYPE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.FeatureId
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.openGallery
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import com.lttrung.notepro.utils.requestPermissionToReadGallery
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity

@AndroidEntryPoint
class EditNoteActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    val tempList = note.images.toMutableList()
                    tempList.addAll(images)
                    note.images = tempList
                    editNoteViewModel.noteDetailsLiveData.postValue(Resource.Success(note))
                }
            }
        }

    private lateinit var socketService: ChatSocketService

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
                editNoteViewModel.deleteImage(note, image)
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
                        startActivity(chatIntent)
                    }

                    FeatureId.CALL -> {
                        editNoteViewModel.getCurrentUser()
                    }

                    FeatureId.DELETE -> {
                        editNoteViewModel.deleteNote(getNoteFromUi())
                    }

                    else -> {

                    }
                }
            }
        })
    }

    private val editNoteViewModel: EditNoteViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ChatSocketService.LocalBinder
                socketService = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editNoteViewModel.getNoteDetails(note.id)
    }

    override fun onStart() {
        super.onStart()
        Intent(this@EditNoteActivity, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Service.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstant.CAMERA_REQUEST && permissions.contains(Manifest.permission.CAMERA)) {
            openCamera(launcher)
        } else if (requestCode == AppConstant.READ_EXTERNAL_STORAGE_REQUEST && permissions.contains(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openGallery(launcher, this@EditNoteActivity)
        }
    }

    private fun getFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.CAMERA, R.drawable.ic_baseline_camera_alt_24),
            Feature(FeatureId.GALLERY, R.drawable.ic_baseline_photo_album_24),
            Feature(FeatureId.MEMBERS, R.drawable.ic_baseline_groups_24),
            Feature(FeatureId.CHAT, R.drawable.ic_baseline_message_24),
            Feature(FeatureId.CALL, R.drawable.ic_baseline_call_24),
            Feature(FeatureId.DELETE, R.drawable.ic_baseline_delete_outline_24)
        )
    }

    override fun initListeners() {
        binding.fabSave.setOnClickListener {
            // Save note
            editNoteViewModel.editNote(getNoteFromUi())
        }
    }

    override fun initObservers() {
        editNoteViewModel.editNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(NOTE, resource.data)
                    resultIntent.putExtra(NOTE_ACTION_TYPE, EDIT_NOTE)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.deleteNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val roomId = note.id

                    socketService.sendDeleteNoteMessage(roomId)

                    val resultIntent = Intent()
                    resultIntent.putExtra(NOTE, note)
                    resultIntent.putExtra(NOTE_ACTION_TYPE, DELETE_NOTE)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.noteDetailsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val note = resource.data
                    intent.putExtra(NOTE, note)
                    bindDataToViews()
                }

                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.currentUserLiveData.observe(this@EditNoteActivity) { currentUser ->
            if (currentUser != null) {
                val roomId = note.id
                socketService.call(note.id)
                val options = JitsiHelper.createOptions(roomId, currentUser)
                JitsiMeetActivity.launch(this, options)
            }
        }
    }

    private fun bindDataToViews() {
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)
        imagesAdapter.submitList(note.images)
    }

    override fun initViews() {
        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
            rvImages.adapter = imagesAdapter
            rvFeatures.adapter = featureAdapter
        }

        StartSnapHelper().attachToRecyclerView(binding.rvImages)
        StartSnapHelper().attachToRecyclerView(binding.rvFeatures)
        imagesAdapter.submitList(note.images)
        featureAdapter.submitList(getFeatures())
    }

    private fun getNoteFromUi(): Note {
        return Note(
            note.id,
            binding.edtNoteTitle.text!!.trim().toString(),
            binding.edtNoteDesc.text!!.trim().toString(),
            note.lastModified,
            false,
            role = note.role,
            images = imagesAdapter.currentList
        )
    }
}