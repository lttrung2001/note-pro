package com.lttrung.notepro.ui.addnote

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("InflateParams")
@AndroidEntryPoint
class AddNoteActivity : AddImagesActivity() {
    private val binding: ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }
    private val imagesAdapter: ImagesAdapter by lazy {
        val adapter = ImagesAdapter(imageListener)
        binding.rcvImages.let {
            it.adapter = adapter
            it.layoutManager = CardSliderLayoutManager(this@AddNoteActivity)
            CardSnapHelper().attachToRecyclerView(it)
        }
        adapter
    }
    private val addNoteViewModel: AddNoteViewModel by viewModels()
    private val alertDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        builder.create()
    }
    private lateinit var menu: Menu
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
        initObservers()
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

    private fun initObservers() {
        addNoteViewModel.addNote.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }
                is Resource.Success -> {
                    alertDialog.dismiss()
                    val resultIntent = Intent()
                    val note = resource.data
                    resultIntent.putExtra(NOTE, note)
                    setResult(RESULT_OK, resultIntent)

                    socketService.sendAddNoteMessage(note.id)

                    finish()
                }
                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(binding.root, resource.t.message.toString(), LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        this.menu = menu!!
        val pinButton = menu.getItem(0)
        pinButton.isChecked = false
        pinButton.icon.setTint(resources.getColor(R.color.black, theme))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menu
        when (item.itemId) {
            R.id.action_pin -> {
                if (item.isChecked) {
                    item.icon.setTint(resources.getColor(R.color.black, theme))
                } else {
                    item.icon.setTint(resources.getColor(R.color.primary, theme))
                }
                item.isChecked = !item.isChecked
            }
            R.id.action_save -> {
                val note = Note(
                    String(),
                    binding.edtNoteTitle.text?.trim().toString(),
                    binding.edtNoteDesc.text?.trim().toString(),
                    0L,
                    menu.getItem(0).isChecked,
                    isArchived = false,
                    isRemoved = false,
                    role = String(),
                    images = imagesAdapter.currentList,
                )
                addNoteViewModel.addNote(note)
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
                    imagesAdapter.submitList(images)
                    bottomSheet.dismiss()
                }
            }
        }

    private val imageListener: ImagesAdapter.ImageListener by lazy {
        object : ImagesAdapter.ImageListener {
            override fun onClick(image: Image) {
                // Start image details activity
            }

            override fun onDelete(image: Image) {
                val currentList = imagesAdapter.currentList.toMutableList()
                currentList.remove(image)
                imagesAdapter.submitList(currentList)
            }
        }
    }
}