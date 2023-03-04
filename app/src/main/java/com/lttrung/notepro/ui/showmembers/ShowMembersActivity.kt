package com.lttrung.notepro.ui.showmembers

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityShowMembersBinding
import com.lttrung.notepro.ui.showmembers.adapters.MemberAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowMembersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowMembersBinding
    private val getMembersViewModel: ShowMembersViewModel by viewModels()
    private lateinit var memberAdapter: MemberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initAdapters()
        initObservers()
        initData()
    }

    private fun initAdapters() {
        memberAdapter = MemberAdapter()
        binding.rcvMembers.adapter = memberAdapter
    }

    private fun initObservers() {
        getMembersViewModel.getMembers.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    memberAdapter.submitList(resource.data.data)
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        getMembersViewModel.getMembers(
            note.id,
            memberAdapter.currentList.size / PAGE_LIMIT,
            PAGE_LIMIT
        )
    }

    private fun initViews() {
        binding = ActivityShowMembersBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_member) {
            return true
        } else {
            onBackPressed()
            return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_show_members, menu)
        return true
    }
}