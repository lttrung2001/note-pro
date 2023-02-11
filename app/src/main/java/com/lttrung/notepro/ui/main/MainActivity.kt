package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.adapter.PinnedNoteAdapter
import com.lttrung.notepro.databinding.ActivityMainBinding
import com.lttrung.notepro.database.data.models.Note
import com.lttrung.notepro.ui.addnote.AddNoteActivity
import com.lttrung.notepro.ui.notedetails.NoteDetailsActivity
import com.lttrung.notepro.ui.setting.SettingActivity
import com.lttrung.notepro.utils.AppConstant
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pinnedNoteAdapter: PinnedNoteAdapter

    private val onClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val note = PinnedNoteAdapter.ViewHolder.bind(view)
            val intent = Intent(this, NoteDetailsActivity::class.java)
            val bundle = Bundle()

            bundle.putSerializable(AppConstant.NOTE, note)
            intent.putExtras(bundle)

            startActivityIfNeeded(intent, AppConstant.SHOW_NOTE_DETAIL_REQUEST)
        }
    }

    private val fabOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //                .setAnchorView(R.id.fab)
            //                .setAction("Action", null).show()
            startActivity(Intent(this, AddNoteActivity::class.java))
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

    private val btnSearchOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.setLogo(R.drawable.ic_baseline_sticky_note_2_24)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.fab.setOnClickListener(fabOnClickListener)
        binding.btnSearch.setOnClickListener(btnSearchOnClickListener)
        binding.scrollView.setOnScrollChangeListener(fabOnScrollChangeListener)

        pinnedNoteAdapter = PinnedNoteAdapter(onClickListener)
        binding.rcvPinnedNotes.adapter = pinnedNoteAdapter
        binding.rcvOtherNotes.adapter = pinnedNoteAdapter
        val tmpPinned = arrayListOf<Note>()
        for (i in 0 until 10) {
            tmpPinned.add(Note(i.toString(), i.toString(), i.toString(), i, true, "owner"))
        }
        pinnedNoteAdapter.submitList(tmpPinned)

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}