package com.lttrung.notepro.ui.addnote

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityAddNoteBinding
import com.lttrung.notepro.ui.bottomsheetdialogfragment.MenuBottomSheetDialogFragment
import com.lttrung.notepro.utils.AppConstant

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding

    private val openBottomSheetDialogListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            openBottomSheetMenu()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == AppConstant.READ_EXTERNAL_STORAGE_REQUEST &&
            permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            openBottomSheetMenu()
        } else if (requestCode == AppConstant.CAMERA_REQUEST &&
            permissions.contains(Manifest.permission.CAMERA)
        ) {
            openCamera()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun openBottomSheetMenu() {
        val bottomSheet = MenuBottomSheetDialogFragment()
        bottomSheet.show(supportFragmentManager, MenuBottomSheetDialogFragment.toString())
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheetDialogListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
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
            R.id.action_save -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}