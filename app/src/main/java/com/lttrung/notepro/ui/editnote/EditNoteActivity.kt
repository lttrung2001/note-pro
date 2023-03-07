package com.lttrung.notepro.ui.editnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.ui.base.adapters.image.ImagesAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : AddImagesActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var menu: Menu
    private val editNoteViewModel: EditNoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initData()
        initListeners()
        initObservers()
    }

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        binding.edtNoteTitle.setText(note.title)
        binding.edtNoteDesc.setText(note.content)
        binding.tvLastModified.text = note.lastModified.toString()
        imagesAdapter = ImagesAdapter()
        binding.rcvImages.adapter = imagesAdapter
    }

    private fun initListeners() {
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
    }

    private fun initObservers() {
        editNoteViewModel.editNote.observe(this) { resource ->
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

    private fun initViews() {
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
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
            R.id.action_show_members -> {
                // Start show members activity
                startActivity(Intent(this, ShowMembersActivity::class.java))
                true
            }
            R.id.action_save -> {
                // Save note
                val noteDetails = intent.getSerializableExtra(NOTE) as Note
                val note = Note(
                    noteDetails.id,
                    binding.edtNoteTitle.text!!.trim().toString(),
                    binding.edtNoteDesc.text!!.trim().toString(),
                    noteDetails.lastModified,
                    menu.getItem(0)?.isChecked ?: false,
                    noteDetails.role,
                    emptyList()
                )
                editNoteViewModel.editNote(note, emptyList())
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
                Log.i("OK", result.toString())
            }
        }
}