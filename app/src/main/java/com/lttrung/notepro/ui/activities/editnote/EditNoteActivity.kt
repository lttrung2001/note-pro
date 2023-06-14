package com.lttrung.notepro.ui.activities.editnote

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.viewgallery.ViewGalleryActivity
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.activities.viewmembers.ViewMembersActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.FeatureId
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    val tempList = imagesAdapter.currentList.toMutableList()
                    tempList.addAll(images)
                    imagesAdapter.submitList(tempList)
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
                    putExtra(AppConstant.IMAGES_JSON, Gson().toJson(imagesAdapter.currentList))
                })
            }

            override fun onDelete(image: Image) {
                editNoteViewModel.deleteImage(image)
            }
        })
    }
    private val featureAdapter by lazy {
        FeatureAdapter(object : FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {
                when (item.id) {
                    FeatureId.CAMERA -> {
                        if (requestPermissionToOpenCamera()) {
                            openCamera()
                        }
                    }

                    FeatureId.GALLERY -> {
                        if (requestPermissionToReadGallery()) {
                            openGallery()
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
        initViews()
        initListeners()
        initObservers()
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
            openCamera()
        } else if (requestCode == AppConstant.READ_EXTERNAL_STORAGE_REQUEST && permissions.contains(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openGallery()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(this@EditNoteActivity, ViewGalleryActivity::class.java)
        launcher.launch(galleryIntent)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }

    private fun requestPermissionToReadGallery(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                AppConstant.READ_EXTERNAL_STORAGE_REQUEST
            )
            return false
        } else {
            return true
        }
    }

    private fun requestPermissionToOpenCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                AppConstant.CAMERA_REQUEST
            )
            return false
        } else {
            return true
        }
    }

    private fun getFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.CAMERA, R.drawable.ic_baseline_camera_alt_24),
            Feature(FeatureId.GALLERY, R.drawable.ic_baseline_photo_album_24),
            Feature(FeatureId.MEMBERS, R.drawable.ic_baseline_groups_24),
            Feature(FeatureId.CHAT, R.drawable.ic_baseline_message_24),
            Feature(FeatureId.CALL, R.drawable.ic_baseline_call_24)
        )
    }

    // if (note.isRemoved) {
    //                    Snackbar.make(this, binding.root, "No permission!", Snackbar.LENGTH_LONG).show()
    //                } else {
    //                    val note = getNoteFromUi()
    //                    note.isArchived = !note.isArchived
    //                    editNoteViewModel.editNote(note)
    //                }

    // val note = intent.getSerializableExtra(NOTE) as Note
    //                val showConservationIntent =
    //                    Intent(this@EditNoteActivity, ChatActivity::class.java)
    //                showConservationIntent.putExtra(AppConstant.ROOM_ID, note.id)
    //                showConservationIntent.putExtra(NOTE, note)
    //                startActivity(showConservationIntent)

    // if (note.isRemoved) {
    //                    Snackbar.make(this, binding.root, "No permission!", Snackbar.LENGTH_LONG).show()
    //                } else {
    //                    // Save note
    //                    editNoteViewModel.editNote(getNoteFromUi())
    //                }

    override fun initListeners() {

    }

    override fun initObservers() {
        editNoteViewModel.editNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EDITED_NOTE, resource.data)
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
                    resultIntent.putExtra(DELETED_NOTE, note)
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
    }

    private fun bindDataToViews() {
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)
        imagesAdapter.submitList(note.images)
    }

    override fun initViews() {
        setContentView(binding.root)
        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
        }

        PagerSnapHelper().attachToRecyclerView(binding.rvImages)
        binding.rvImages.adapter = imagesAdapter
        imagesAdapter.submitList(note.images)

        binding.rvFeatures.adapter = featureAdapter
        featureAdapter.submitList(getFeatures())

//        if (note.isRemoved) {
//            binding.edtNoteTitle.isEnabled = false
//            binding.edtNoteDesc.isEnabled = false
//            binding.btnOpenBottomSheet.visibility = View.INVISIBLE
//            if (note.isOwner()) {
//                binding.btnRestore.visibility = View.VISIBLE
//                binding.btnDeleteNote.visibility = View.VISIBLE
//            }
//        } else if (note.isOwner()) {
//            binding.btnDeleteNote.visibility = View.VISIBLE
//        }
    }

    private fun getNoteFromUi(): Note {
        return Note(
            note.id,
            binding.edtNoteTitle.text!!.trim().toString(),
            binding.edtNoteDesc.text!!.trim().toString(),
            note.lastModified,
            false,
            false,
            false,
            role = note.role,
            images = imagesAdapter.currentList
        )
    }
}