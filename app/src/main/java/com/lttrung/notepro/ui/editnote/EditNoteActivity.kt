package com.lttrung.notepro.ui.editnote

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditNoteBinding
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
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
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}