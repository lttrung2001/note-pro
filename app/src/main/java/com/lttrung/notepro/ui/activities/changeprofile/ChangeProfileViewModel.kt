package com.lttrung.notepro.ui.activities.changeprofile

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeProfileViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : BaseViewModel() {
    internal val changeProfileLiveData by lazy {
        MutableLiveData<UserInfo>()
    }

    internal fun changeProfile(fullName: String, phoneNumber: String) {
        launch {
            val changeProfile = userRepositories.changeProfile(fullName, phoneNumber)
            changeProfileLiveData.postValue(changeProfile)
        }
    }
}