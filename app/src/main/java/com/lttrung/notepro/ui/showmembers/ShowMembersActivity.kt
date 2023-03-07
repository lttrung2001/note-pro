package com.lttrung.notepro.ui.showmembers

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityShowMembersBinding
import com.lttrung.notepro.ui.base.adapters.member.MemberAdapter
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
        initListeners()
        initAdapters()
        initObservers()
        initData()
    }

    private val refreshListener: SwipeRefreshLayout.OnRefreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            initData()
        }
    }

    private fun initListeners() {
        binding.refreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun initAdapters() {
        memberAdapter = MemberAdapter()
        binding.rcvMembers.adapter = memberAdapter
    }

    private fun initObservers() {
        getMembersViewModel.getMembers.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    val currentList = memberAdapter.currentList.toMutableList()
                    currentList.addAll(resource.data.data)
                    memberAdapter.submitList(currentList)
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
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