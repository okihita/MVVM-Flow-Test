package com.okihita.accenture.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.okihita.accenture.data.local.GitHubUserDatabase
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
open class GitHubRepository @Inject constructor(
    private val api: GitHubApi,
    private val database: GitHubUserDatabase
) {
    open fun getSearchResultFlow(searchQuery: String): Flow<PagingData<GitHubUser>> {

        return Pager(
            config = PagingConfig(
                PAGE_SIZE_PER_REQUEST,
                enablePlaceholders = false,
                prefetchDistance = 2 * PAGE_SIZE_PER_REQUEST,
                initialLoadSize = 5 * PAGE_SIZE_PER_REQUEST
            ),
            remoteMediator = GitHubUserRemoteMediator(searchQuery, api, database),
            pagingSourceFactory = { database.userDao.getAllUsersAsPagingSource() }
        ).flow
    }
}