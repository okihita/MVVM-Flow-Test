package com.okihita.accenture.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.okihita.accenture.data.local.GitHubRemoteKey
import com.okihita.accenture.data.local.GitHubUserDatabase
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.GITHUB_STARTING_PAGE_INDEX
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import com.okihita.accenture.util.ResultException
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class GitHubUserRemoteMediator(
    private val searchQuery: String,
    private val api: GitHubApi,
    private val database: GitHubUserDatabase
) : RemoteMediator<Int, GitHubUser>() {

    private val userDao = database.userDao
    private val remoteKeyDao = database.remoteKeyDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GitHubUser>
    ): MediatorResult {

        return try {

            val pageToLoad: Int = when (loadType) {
                LoadType.REFRESH -> GITHUB_STARTING_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {

                    val lastUserLoaded = state.lastItemOrNull()
                    println("last user loaded: $lastUserLoaded")
                    if (lastUserLoaded == null) { // No item in RV, then the REFRESH result was empty
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        val nextKey: Int? = remoteKeyDao.getKeyById(lastUserLoaded.id)?.nextKey
                        println("next key: $nextKey")
                        nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }
            }

            val response = api.getUsers(searchQuery, PAGE_SIZE_PER_REQUEST, pageToLoad)
            val users =
                response.users.sortedBy { it.id } // Important to sort by ID to avoid flicker
            val isUserListEmpty = users.isEmpty()


            if (isUserListEmpty) {
                if (loadType == LoadType.REFRESH) return MediatorResult.Error(ResultException.EmptyResultException())
                else if (loadType == LoadType.APPEND) return MediatorResult.Error(ResultException.NoMoreResultException())
            }

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    database.clearAllTables()
                }

                val prevKey = if (pageToLoad == GITHUB_STARTING_PAGE_INDEX) null else pageToLoad - 1
                val nextKey = if (isUserListEmpty) null else pageToLoad + 1

                val keys = users.map { GitHubRemoteKey(it.id, it.login, prevKey, nextKey) }
                remoteKeyDao.insertAll(keys)
                userDao.insertAll(users)

                println("User size in db is now ${userDao.getAllUsers().size}")
            }

            MediatorResult.Success(endOfPaginationReached = isUserListEmpty)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: Exception) {
            exception.printStackTrace()
            MediatorResult.Error(exception)
        }
    }
}