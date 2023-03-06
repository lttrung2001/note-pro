package com.lttrung.notepro.ui.addnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.ui.base.AddImagesActivity
import com.lttrung.notepro.ui.notedetails.adapters.ImagesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteActivity : AddImagesActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapter()
    }

    private fun initAdapter() {
        imagesAdapter = ImagesAdapter()
        binding.rcvImages.apply {
            adapter = imagesAdapter
            layoutManager =
                LinearLayoutManager(this@AddNoteActivity, LinearLayoutManager.HORIZONTAL, false)
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_pin -> {
                if (item.isChecked) {
                    item.isChecked = false
                    item.icon.setTint(resources.getColor(R.color.black, theme))
                } else {
                    item.isChecked = true
                    item.icon.setTint(resources.getColor(R.color.primary, theme))
                }
                true
            }
            R.id.action_save -> {
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