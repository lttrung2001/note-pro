package com.lttrung.notepro.ui.editnote

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.chat.ChatActivity
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.viewimagedetails.ViewImageDetailsActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Converter
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("InflateParams")
@AndroidEntryPoint
class EditNoteActivity : AddImagesActivity() {
    override val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    val tempList = imagesAdapter.currentList.toMutableList()
                    tempList.addAll(images)
                    imagesAdapter.submitList(tempList)
                    bottomSheet.dismiss()
                }
            }
        }

    private lateinit var menu: Menu
    private lateinit var socketService: ChatSocketService

    private val binding: ActivityEditNoteBinding by lazy {
        ActivityEditNoteBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImagesAdapter by lazy {
         ImagesAdapter(imageListener)
    }
    private val imageListener by lazy {
        object : ImagesAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
                startActivity(
                    Intent(
                        this@EditNoteActivity,
                        ViewImageDetailsActivity::class.java
                    ).apply {
                        putExtra(AppConstant.IMAGES_JSON, Gson().toJson(imagesAdapter.currentList))
                    }
                )
            }

            override fun onDelete(image: Image) {
                editNoteViewModel.deleteImage(image)
            }
        }
    }
    private val editNoteViewModel: EditNoteViewModel by viewModels()
    private val note: Note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private val alertDialog by lazy {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        builder.create()
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
        initData()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        menu?.let {
            this.menu = menu

            val pinButton = menu.getItem(0)
            val archiveButton = menu.getItem(1)

            setStatusForButtons(pinButton, archiveButton)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_pin -> {
                if (note.isRemoved) {
                    Snackbar.make(this, binding.root, "No permission!", Snackbar.LENGTH_LONG).show()
                } else {
                    if (item.isChecked) {
                        item.icon.setTint(resources.getColor(R.color.black, theme))
                    } else {
                        item.icon.setTint(resources.getColor(R.color.primary, theme))
                    }
                    item.isChecked = !item.isChecked
                }
            }

            R.id.action_archive -> {
                if (note.isRemoved) {
                    Snackbar.make(this, binding.root, "No permission!", Snackbar.LENGTH_LONG).show()
                } else {
                    val note = getNoteFromUi()
                    note.isArchived = !note.isArchived
                    editNoteViewModel.editNote(note)
                }
            }

            R.id.action_show_conservation -> {
                val note = intent.getSerializableExtra(NOTE) as Note
                val showConservationIntent =
                    Intent(this@EditNoteActivity, ChatActivity::class.java)
                showConservationIntent.putExtra(AppConstant.ROOM_ID, note.id)
                showConservationIntent.putExtra(NOTE, note)
                startActivity(showConservationIntent)
            }

            R.id.action_save -> {
                if (note.isRemoved) {
                    Snackbar.make(this, binding.root, "No permission!", Snackbar.LENGTH_LONG).show()
                } else {
                    // Save note
                    editNoteViewModel.editNote(getNoteFromUi())
                }
            }

            else -> {
                finish()
            }
        }
        return true
    }

    private fun setStatusForButtons(pinButton: MenuItem, archiveButton: MenuItem) {
        pinButton.isChecked = note.isPin
        val pinIcon = if (note.isPin) resources.getColor(
            R.color.primary,
            theme
        ) else resources.getColor(R.color.black, theme)
        pinButton.icon.setTint(pinIcon)

        val archiveIconResource =
            if (note.isArchived) {
                archiveButton.isChecked = true
                R.drawable.ic_baseline_unarchive_24
            } else {
                archiveButton.isChecked = false
                R.drawable.ic_baseline_archive_24
            }
        archiveButton.setIcon(archiveIconResource)
    }

    private fun initData() {
        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
            tvLastModified.text = Converter.longToDate(note.lastModified)
        }
        imagesAdapter.submitList(note.images)
    }

    private fun initListeners() {
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
        binding.btnDeleteNote.setOnClickListener {
            if (note.isRemoved) {
                editNoteViewModel.deleteNote(note)
            } else {
                note.isRemoved = true
                editNoteViewModel.editNote(note)
            }
        }
        binding.btnRestore.setOnClickListener {
            note.isRemoved = false
            editNoteViewModel.editNote(note)
        }
    }

    private fun initObservers() {
        editNoteViewModel.editNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }

                is Resource.Success -> {
                    alertDialog.dismiss()
                    val resultIntent = Intent()
                    resultIntent.putExtra(EDITED_NOTE, resource.data)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.deleteNoteLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }

                is Resource.Success -> {
                    alertDialog.dismiss()
                    val roomId = note.id

                    socketService.sendDeleteNoteMessage(roomId)

                    val resultIntent = Intent()
                    resultIntent.putExtra(DELETED_NOTE, note)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.noteDetailsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }

                is Resource.Success -> {
                    alertDialog.dismiss()
                    val note = resource.data
                    intent.putExtra(NOTE, note)
                    bindDataToViews()
                }

                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun bindDataToViews() {
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)
        binding.tvLastModified.text = Converter.longToDate(note.lastModified)
        imagesAdapter.submitList(note.images)
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CardSnapHelper().attachToRecyclerView(binding.rcvImages)
        binding.rcvImages.adapter = imagesAdapter

        if (note.isRemoved) {
            binding.edtNoteTitle.isEnabled = false
            binding.edtNoteDesc.isEnabled = false
            binding.btnOpenBottomSheet.visibility = View.INVISIBLE
            if (note.isOwner()) {
                binding.btnRestore.visibility = View.VISIBLE
                binding.btnDeleteNote.visibility = View.VISIBLE
            }
        } else if (note.isOwner()) {
            binding.btnDeleteNote.visibility = View.VISIBLE
        }
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