package com.lttrung.notepro.ui.base.adapters.member

import com.lttrung.notepro.database.data.locals.entities.Member

interface MemberListener {
    fun onClick(member: Member)
}