package com.lttrung.notepro.ui.notedetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityNoteDetailBinding
import com.lttrung.notepro.ui.editnote.EditNoteActivity
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    private val fabOnScrollChangeListener: View.OnScrollChangeListener by lazy {
        View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY == 0 && scrollY > 0) {
                binding.fab.shrink()
            } else if (scrollY == 0) {
                binding.fab.extend()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        binding.fab.setOnClickListener(fabOnClickListener)
        binding.fab.setOnScrollChangeListener(fabOnScrollChangeListener)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}