package com.okihita.accenture.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.GITHUB_STARTING_PAGE_INDEX
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import com.okihita.accenture.util.ResultException
import retrofit2.HttpException
import java.io.IOException

class GitHubPagingSource(
    private val api: GitHubApi,
    private val searchQuery: String
) : PagingSource<Int, GitHubUser>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubUser> {

        val remotePageToLoad = params.key ?: GITHUB_STARTING_PAGE_INDEX

        return try {

            val response = api.getUsers(searchQuery, params.loadSize, remotePageToLoad)
            val users = response.users

            if (users.isEmpty()) {
                if (params is LoadParams.Append) {
                    return LoadResult.Error(ResultException.NoMoreResultException())
                } else if (params is LoadParams.Refresh) {
                    return LoadResult.Error(ResultException.EmptyResultException())
                }
            }

            val nextKey = if (users.isEmpty()) {
                null
            } else {
                remotePageToLoad + (params.loadSize / PAGE_SIZE_PER_REQUEST)
            }

            LoadResult.Page(
                data = users,
                prevKey = if (remotePageToLoad == GITHUB_STARTING_PAGE_INDEX) null else (remotePageToLoad - 1),
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GitHubUser>): Int? {
        // Reminder: Anchor position is either the top-most item when scrolling up,
        // or bottom-most item when scrolling down
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}