package com.lttrung.notepro.ui.base.adapters.member

import com.lttrung.notepro.domain.data.networks.models.Member

interface MemberListener {
    fun onClick(member: Member)
}