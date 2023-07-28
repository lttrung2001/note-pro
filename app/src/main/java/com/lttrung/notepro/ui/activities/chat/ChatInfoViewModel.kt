package com.lttrung.notepro.ui.activities.chat

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.repositories.ThemeRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories,
    private val themeRepositories: ThemeRepositories
) : BaseViewModel() {
    internal val addMemberLiveData by lazy { MutableLiveData<Member>() }
    internal val leaveChatLiveData by lazy { MutableLiveData<Unit>() }
    internal val themeListLiveData by lazy { MutableLiveData<List<Theme>>() }
    internal val themeList by lazy { mutableListOf<Theme>() }
    internal var currentTheme: Theme? = null

    internal fun addMember(noteId: String, email: String, role: String) {
        launch {
            val addMember = memberRepositories.addMember(noteId, email, role)
            addMemberLiveData.postValue(addMember)
        }
    }

    internal fun leaveChat(roomId: String) {
        launch {
            val leaveChat = memberRepositories.deleteMember(roomId)
            leaveChatLiveData.postValue(leaveChat)
        }
    }

    internal fun getThemeList() {
        launch {
            val data = themeRepositories.getThemeList()
            themeList.clear()
            themeList.addAll(data)
            themeListLiveData.postValue(data)
        }
    }
}