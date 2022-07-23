package com.okihita.accenture.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class GitHubRepository @Inject constructor(private val api: GitHubApi) {

    open fun getSearchResultFlow(searchQuery: String): Flow<PagingData<GitHubUser>> =
        Pager(
            config = PagingConfig(PAGE_SIZE_PER_REQUEST, enablePlaceholders = false),
            pagingSourceFactory = { GitHubPagingSource(api, searchQuery) }
        ).flow
}