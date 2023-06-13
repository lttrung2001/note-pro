package com.lttrung.notepro.ui.activities.editnote

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.ui.adapters.FeatureAdapter
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.Feature
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Converter
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

    private lateinit var menu: Menu
    private lateinit var socketService: ChatSocketService

    override val binding by lazy {
        ActivityEditNoteBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImageAdapter by lazy {
        ImageAdapter(imageListener)
    }
    private val imageListener by lazy {
        object : ImageAdapter.ImageListener {
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
        }
    }
    private val featureAdapter by lazy {
        FeatureAdapter(object: FeatureAdapter.FeatureListener {
            override fun onClick(item: Feature) {

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
            menu.getItem(0).isChecked,
            isArchived = menu.getItem(1).isChecked,
            isRemoved = note.isRemoved,
            role = note.role,
            images = imagesAdapter.currentList
        )
    }
}