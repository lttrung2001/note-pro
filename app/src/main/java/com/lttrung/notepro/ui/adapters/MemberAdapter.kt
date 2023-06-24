package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.LoadingItemBinding
import com.lttrung.notepro.databinding.MemberItemBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging

class MemberAdapter(
    private val listener: MemberListener
) : ListAdapter<Member, ViewHolder>(memberItemCallback) {
    companion object {
        private val memberItemCallback = object : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem == newItem
            }
        }
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        (holder as MemberViewHolder).bind(member, listener)
    }

    interface MemberListener {
        fun onClick(member: Member)
    }

    class MemberViewHolder(val binding: MemberItemBinding) :
        ViewHolder(binding.root) {
        fun bind(member: Member, listener: MemberListener) {
            binding.tvFullName.text = member.fullName
            binding.tvRole.text = member.role
            binding.tvEmail.text = member.email
            binding.tvPhoneNumber.text = member.phoneNumber
            if (member.role != "owner") {
                binding.root.setOnClickListener { listener.onClick(member) }
            }
        }
    }
}