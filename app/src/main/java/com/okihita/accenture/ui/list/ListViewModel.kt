package com.okihita.accenture.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class ListViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    var searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    val searchResultUsersFlow: Flow<PagingData<GitHubUser>> = searchQuery.flatMapLatest {
        repository.getSearchResultFlow(searchQuery.value)
    }.cachedIn(viewModelScope)

    fun updateSearchQuery(newQuery: String) {
        searchQuery.value = newQuery
    }
}