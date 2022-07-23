package com.okihita.accenture.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.okihita.accenture.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    fun searchUsers(searchQuery: String) = repository
        .getSearchResultFlow(searchQuery)
        .cachedIn(viewModelScope)
}