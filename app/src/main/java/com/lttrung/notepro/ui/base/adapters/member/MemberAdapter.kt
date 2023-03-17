package com.lttrung.notepro.ui.base.adapters.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lttrung.notepro.database.data.locals.entities.Member
import com.lttrung.notepro.databinding.LayoutMemberBinding

class MemberAdapter(
    private val listener: MemberListener
) : ListAdapter<Member, MemberViewHolder>(memberItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = LayoutMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = getItem(position)
        holder.bind(member)
        holder.binding.root.setOnClickListener {
            listener.onClick(member)
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
    }
}