package com.okihita.accenture.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.okihita.accenture.data.local.GitHubUserDatabase
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.model.GitHubUserDetails
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    open fun getUserDetails(userId: Int): Flow<Result<GitHubUserDetails>> = flow {
        try {
            val dao = database.userDetailsDao
            val dbUserDetails = dao.getUserById(userId)
            if (dbUserDetails != null) {
                emit(Result.success(dbUserDetails))
            } else {
                api.getUserById(userId).let { dao.insertUser(it) }
                dao.getUserById(userId)?.let { emit(Result.success(it)) }
            }
        } catch (exception: Exception) {
            emit(Result.failure(exception))
        }
    }
}