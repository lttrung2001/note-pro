package com.lttrung.notepro.ui.base.adapters.member

import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.databinding.LayoutMemberBinding

class MemberViewHolder(val binding: LayoutMemberBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(member: Member) {
        binding.tvFullName.text = member.fullName
        binding.tvRole.text = member.role
        binding.tvEmail.text = member.email
        binding.tvPhoneNumber.text = member.phoneNumber
    }
}