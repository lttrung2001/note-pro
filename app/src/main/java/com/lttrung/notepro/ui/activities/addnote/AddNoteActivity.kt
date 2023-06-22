package com.lttrung.notepro.ui.activities.addnote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.ADD_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE_ACTION_TYPE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.FeatureId
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.openGallery
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import com.lttrung.notepro.utils.requestPermissionToReadGallery
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    imagesAdapter.submitList(images)
                }
            }
        }

    override val binding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }
    private val addNoteViewModel: AddNoteViewModel by viewModels()
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(object : ImageAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
                startActivity(Intent(
                    this@AddNoteActivity, ViewImageDetailsActivity::class.java
                ).apply {
                    putExtra(AppConstant.LIST_IMAGE, ListImage(imagesAdapter.currentList))
                })
            }

            override fun onDelete(image: Image) {
                val currentList = imagesAdapter.currentList.toMutableList()
                currentList.remove(image)
                imagesAdapter.submitList(currentList)
            }
        })
    }
    private val featureAdapter by lazy {
        FeatureAdapter(object : FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {
                when (item.id) {
                    FeatureId.CAMERA -> {
                        if (requestPermissionToOpenCamera(this@AddNoteActivity)) {
                            openCamera(launcher)
                        }
                    }

                    FeatureId.GALLERY -> {
                        if (requestPermissionToReadGallery(this@AddNoteActivity)) {
                            openGallery(launcher, this@AddNoteActivity)
                        }
                    }

                    else -> {

                    }
                }
            }
        })
    }
    private lateinit var socketService: ChatSocketService

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
    }

    override fun onStart() {
        super.onStart()
        Intent(this@AddNoteActivity, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    private fun getFeatures(): List<Feature> {
        return listOf(
            Feature(FeatureId.CAMERA, R.drawable.ic_baseline_camera_alt_24),
            Feature(FeatureId.GALLERY, R.drawable.ic_baseline_photo_album_24)
        )
    }

    override fun initViews() {
        binding.apply {
            rvImages.adapter = imagesAdapter
            rvFeatures.adapter = featureAdapter
        }
        featureAdapter.submitList(getFeatures())
    }

    override fun initListeners() {
        binding.fabSave.setOnClickListener {
            val note = Note(
                String(),
                binding.edtNoteTitle.text?.trim().toString(),
                binding.edtNoteDesc.text?.trim().toString(),
                0L,
                false,
                role = String(),
                images = imagesAdapter.currentList,
            )
            addNoteViewModel.addNote(note)
        }
    }

    override fun initObservers() {
        addNoteViewModel.addNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val resultIntent = Intent()
                    val note = resource.data
                    resultIntent.putExtra(NOTE, note)
                    resultIntent.putExtra(NOTE_ACTION_TYPE, ADD_NOTE)
                    setResult(RESULT_OK, resultIntent)

                    socketService.sendAddNoteMessage(note.id)

                    finish()
                }

                is Resource.Error -> {
                    Snackbar.make(binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}