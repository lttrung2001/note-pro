package com.lttrung.notepro.ui.addnote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.ui.addimages.AddImagesFragment
import com.lttrung.notepro.ui.notedetails.adapters.ImagesAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.CAMERA_REQUEST
import com.lttrung.notepro.utils.AppConstant.Companion.PICK_IMAGES_REQUEST
import com.lttrung.notepro.utils.AppConstant.Companion.READ_EXTERNAL_STORAGE_REQUEST
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST && permissions.contains(Manifest.permission.CAMERA)) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityIfNeeded(cameraIntent, CAMERA_REQUEST)
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST && permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityIfNeeded(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGES_REQUEST
            );
        }
    }

    private val openBottomSheetDialogListener: View.OnClickListener by lazy {
        View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                openBottomSheetMenu()
            }
        }
    }

    private fun openBottomSheetMenu() {
        val bottomSheet = AddImagesFragment()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGES_REQUEST -> {
                Log.i("INFO", data.toString())
            }
        }
    }
}