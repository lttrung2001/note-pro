package com.lttrung.notepro.ui.activities.addnote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.adapters.ImageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Resource
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
            }

            override fun onDelete(image: Image) {
                val currentList = imagesAdapter.currentList.toMutableList()
                currentList.remove(image)
                imagesAdapter.submitList(currentList)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val note = Note(
            String(),
            binding.edtNoteTitle.text?.trim().toString(),
            binding.edtNoteDesc.text?.trim().toString(),
            0L,
            false,
            isArchived = false,
            isRemoved = false,
            role = String(),
            images = imagesAdapter.currentList,
        )
        addNoteViewModel.addNote(note)
    }

    override fun initViews() {
        setContentView(binding.root)
    }

    override fun initListeners() {
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
                    setResult(RESULT_OK, resultIntent)

                    socketService.sendAddNoteMessage(note.id)

                    finish()
                }

                is Resource.Error -> {
//                    Snackbar.make(binding.root, resource.t.message.toString(), LENGTH_LONG).show()
                }
            }
        }
    }
}