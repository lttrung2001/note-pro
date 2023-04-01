package com.lttrung.notepro.ui.editnote

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.chat.ChatActivity
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Converter
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : AddImagesActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var menu: Menu
    private val editNoteViewModel: EditNoteViewModel by viewModels()
    private lateinit var socketService: ChatSocketService

    private val connection: ServiceConnection by lazy {
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
        initAdapter()
        initData()
        initObservers()
        if (editNoteViewModel.noteDetails.value == null) {
            val note = intent.getSerializableExtra(NOTE) as Note
            editNoteViewModel.getNoteDetails(note.id)
        }
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

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        binding.apply {
            edtNoteTitle.setText(note.title)
            edtNoteDesc.setText(note.content)
            tvLastModified.text = Converter.longToDate(note.lastModified)
        }
        imagesAdapter.submitList(note.images)
    }

    private fun initAdapter() {
        imagesAdapter = ImagesAdapter(imageListener)
        binding.rcvImages.adapter = imagesAdapter
    }

    private fun initListeners() {
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
        binding.btnDeleteNote.setOnClickListener(deleteNoteListener)
    }

    private fun initObservers() {
        editNoteViewModel.editNote.observe(this) { resource ->
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
                    Log.e("ERROR", resource.message)
                    Snackbar.make(
                        binding.root, resource.message,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editNoteViewModel.deleteNote.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val note = intent.getSerializableExtra(NOTE) as Note
                    val roomId = note.id

                    socketService.sendDeleteNoteMessage(roomId)

                    val resultIntent = Intent()
                    resultIntent.putExtra(DELETED_NOTE, note)
                    setResult(RESULT_OK, resultIntent)
                    finish()
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

        editNoteViewModel.noteDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val note = resource.data
                    intent.putExtra(NOTE, note)
                    binding.edtNoteTitle.setText(note.title)
                    binding.edtNoteDesc.setText(note.content)
                    binding.tvLastModified.text = Converter.longToDate(note.lastModified)
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

    private fun initViews() {
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        binding.rcvImages.layoutManager = CardSliderLayoutManager(this)
        CardSnapHelper().attachToRecyclerView(binding.rcvImages)

        val note = intent.getSerializableExtra(NOTE) as Note
        if (note.isOwner()) {
            binding.btnDeleteNote.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
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
        when (item.itemId) {
            R.id.action_pin -> {
                if (item.isChecked) {
                    item.icon.setTint(resources.getColor(R.color.black, theme))
                } else {
                    item.icon.setTint(resources.getColor(R.color.primary, theme))
                }
                item.isChecked = !item.isChecked
            }
            R.id.action_show_conservation -> {
                val note = intent.getSerializableExtra(NOTE) as Note
                val showConservationIntent =
                    Intent(this@EditNoteActivity, ChatActivity::class.java)
                showConservationIntent.putExtra(NOTE, note)
                showConservationIntent.putExtra(ROOM_ID, note.id)
                startActivity(showConservationIntent)
            }
            R.id.action_save -> {
                // Save note
                val noteDetails = intent.getSerializableExtra(NOTE) as Note
                val note = Note(
                    noteDetails.id,
                    binding.edtNoteTitle.text!!.trim().toString(),
                    binding.edtNoteDesc.text!!.trim().toString(),
                    noteDetails.lastModified,
                    menu.getItem(0).isChecked,
                    noteDetails.role,
                    imagesAdapter.currentList
                )
                editNoteViewModel.editNote(note)
            }
            else -> {
                finish()
            }
        }
        return true
    }

    override val launcher: ActivityResultLauncher<Intent> =
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

    private val deleteNoteListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val note = intent.getSerializableExtra(NOTE) as Note
            editNoteViewModel.deleteNote(note.id)
        }
    }

    private val imageListener: ImagesAdapter.ImageListener by lazy {
        object : ImagesAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
            }

            override fun onDelete(image: Image) {
                editNoteViewModel.deleteImage(image)
            }
        }
    }
}