package com.lttrung.notepro.ui.base.adapters.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.LayoutLoadingBinding
import com.lttrung.notepro.databinding.LayoutMemberBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.ui.base.adapters.LoadingViewHolder

class MemberAdapter(
    private val listener: MemberListener
) : ListAdapter<Member, ViewHolder>(memberItemCallback) {
    private var hasPreviousPage = false
    private var hasNextPage = false

    fun setPaging(paging: Paging<Member>) {
        hasPreviousPage = paging.hasPreviousPage
        hasNextPage = paging.hasNextPage
        submitList(paging.data)
    }

    fun getPaging(): Paging<Member> {
        return Paging(hasPreviousPage, hasNextPage, currentList)
    }

    fun showLoading() {
        val members = currentList.toMutableList()
        members.add(Member("", "", "", "", ""))
        submitList(members)
    }

    fun hideLoading() {
        val members = currentList.toMutableList()
        members.removeAt(members.size - 1)
        submitList(members)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MEMBER) {
            val binding =
                LayoutMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MemberViewHolder(binding)
        } else {
            val binding =
                LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        if (getItemViewType(position) == MEMBER) {
            holder as MemberViewHolder
            holder.bind(member)
            holder.binding.root.setOnClickListener {
                listener.onClick(member)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == "") {
            LOADING
        } else {
            MEMBER
        }
    }

    companion object {
        private val memberItemCallback = object : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem == newItem
            }
        }

        private const val LOADING = 1
        private const val MEMBER = 2
    }
}