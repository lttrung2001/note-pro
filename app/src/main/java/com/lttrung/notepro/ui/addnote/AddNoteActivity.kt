package com.lttrung.notepro.ui.addnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Resource
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteActivity : AddImagesActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var menu: Menu
    private val addNoteViewModel: AddNoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapter()
        initObservers()
    }

    private fun initObservers() {
        addNoteViewModel.addNote.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(NOTE, resource.data)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun initAdapter() {
        imagesAdapter = ImagesAdapter()
        binding.rcvImages.apply {
            adapter = imagesAdapter
            layoutManager = CardSliderLayoutManager(this@AddNoteActivity)
            CardSnapHelper().attachToRecyclerView(this)
        }
    }

    private fun initListeners() {
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
    }

    private fun initViews() {
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
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
        return when (item.itemId) {
            R.id.action_pin -> {
                if (item.isChecked) {
                    item.icon.setTint(resources.getColor(R.color.black, theme))
                } else {
                    item.icon.setTint(resources.getColor(R.color.primary, theme))
                }
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_save -> {
                val note = Note(
                    "",
                    binding.edtNoteTitle.text?.trim().toString(),
                    binding.edtNoteDesc.text?.trim().toString(),
                    0,
                    menu.getItem(0).isChecked,
                    "",
                    imagesAdapter.currentList
                )
                addNoteViewModel.addNote(note)
                true
            }
            else -> {
                onBackPressed()
                true
            }
        }
    }

    override val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val images = it.getSerializableExtra(SELECTED_IMAGES) as List<Image>
                    imagesAdapter.submitList(images)
                }
            }
        }
}