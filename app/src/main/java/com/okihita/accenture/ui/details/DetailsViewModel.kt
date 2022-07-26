package com.okihita.accenture.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.okihita.accenture.data.model.GitHubUserDetails
import com.okihita.accenture.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    suspend fun getUserDetails(userId: Int): Flow<Result<GitHubUserDetails>> =
        repository.getUserDetails(userId).stateIn(viewModelScope)
}