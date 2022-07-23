package com.okihita.accenture.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val api: GitHubApi) : ViewModel() {

    private val _user = MutableLiveData<GitHubUser>()
    val user: LiveData<GitHubUser> = _user

    fun getProfile(userId: Int) {
        viewModelScope.launch {
            try {
                _user.value = api.getUserById(userId)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}