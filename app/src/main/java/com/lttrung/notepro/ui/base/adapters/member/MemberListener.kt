package com.lttrung.notepro.ui.base.adapters.member

import com.lttrung.notepro.database.data.networks.models.Member

interface MemberListener {
    fun onClick(member: Member)
}