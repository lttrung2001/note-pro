package com.lttrung.notepro.ui.activities.viewprofile

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : BaseViewModel() {
    internal val profileLiveData by lazy {
        MutableLiveData<UserInfo>()
    }

    internal fun getProfile() {
        launch {
            val userInfo = userRepositories.getProfile()
            profileLiveData.postValue(userInfo)
        }
    }
}