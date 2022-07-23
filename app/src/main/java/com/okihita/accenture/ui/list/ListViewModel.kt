package com.okihita.accenture.ui.list

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
class ListViewModel @Inject constructor(private val api: GitHubApi) : ViewModel() {

    private val _users = MutableLiveData<List<GitHubUser>>()
    var users: LiveData<List<GitHubUser>> = _users

    fun searchUsers(searchQuery: String) {
        viewModelScope.launch {
            try {
                _users.value = api.getUsers(searchQuery).users
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}