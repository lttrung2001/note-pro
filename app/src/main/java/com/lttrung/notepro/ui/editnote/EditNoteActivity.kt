package com.lttrung.notepro.ui.editnote

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.ui.notedetails.adapters.ImagesAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private var menu: Menu? = null
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

    }

    private fun initObservers() {
        editNoteViewModel.editNote.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(NOTE, resource.data)
                    setResult(1, resultIntent)
                    finishActivity(1)
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
        this.menu = menu
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_pin -> {
                val pinnedDrawable =
                    resources.getDrawable(R.drawable.ic_baseline_push_pinned_24, theme)
                val unPinDrawable = resources.getDrawable(R.drawable.ic_baseline_push_pin_24, theme)
                item.icon = pinnedDrawable
                true
            }
            R.id.action_show_members -> {
                // Start show members activity
                startActivity(Intent(this, ShowMembersActivity::class.java))
                true
            }
            R.id.action_save -> {
                // Save note
                val noteViewed = (intent.getSerializableExtra(NOTE) as Note)
                val note = Note(
                    noteViewed.id,
                    binding.edtNoteTitle.text!!.trim().toString(),
                    binding.edtNoteDesc.text!!.trim().toString(),
                    noteViewed.lastModified,
                    menu!!.getItem(0).isChecked,
                    noteViewed.role,
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
}