package com.okihita.accenture.data.repository

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.local.GitHubUserDatabase
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.model.GitHubUserSearchResponse
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.generateUsers
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import com.okihita.accenture.util.ResultException
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import retrofit2.HttpException

@ExperimentalPagingApi
@RunWith(JUnit4::class) // TODO: change to androidJunit4
class GitHubUserRemoteMediatorTest {

    private val mockApi: GitHubApi = mock()
    private val mockDatabase: GitHubUserDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        GitHubUserDatabase::class.java
    ).build()

    @After
    fun teardown() {
        mockDatabase.clearAllTables()
        mockDatabase.close()
    }

    @Test
    fun refreshLoad_noResult_returnEmptyResultException() = runBlocking {

        val emptyRefreshResultMockResponse = GitHubUserSearchResponse(
            incomplete_results = false,
            users = generateUsers(0),
            total_count = 0
        )
        `when`(mockApi.getUsers("emptyResult"))
            .thenReturn(emptyRefreshResultMockResponse)
        val remoteMediator = GitHubUserRemoteMediator("emptyResult", mockApi, mockDatabase)
        val pagingState = PagingState<Int, GitHubUser>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(PAGE_SIZE_PER_REQUEST),
            leadingPlaceholderCount = 10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
        val exceptionType = (result as RemoteMediator.MediatorResult.Error).throwable
        assertThat(exceptionType).isInstanceOf(ResultException.EmptyResultException::class.java)
    }

    @Test
    fun refreshLoad_fewResults_returnSuccessPaginationNotEnd() = runBlocking {

        val someRefreshResultMockResponse = GitHubUserSearchResponse(
            incomplete_results = false,
            users = generateUsers(7),
            total_count = 7
        )
        `when`(mockApi.getUsers("fewResults"))
            .thenReturn(someRefreshResultMockResponse)
        val remoteMediator = GitHubUserRemoteMediator("fewResults", mockApi, mockDatabase)
        val pagingState = PagingState<Int, GitHubUser>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(PAGE_SIZE_PER_REQUEST),
            leadingPlaceholderCount = 10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        val endPagination = (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached
        assertThat(endPagination).isEqualTo(false)
    }

    @Test
    fun refreshLoad_httpError_returnMediatorResultError() = runBlocking {

        `when`(mockApi.getUsers("httpException"))
            .thenThrow(HttpException::class.java)
        val remoteMediator = GitHubUserRemoteMediator("httpException", mockApi, mockDatabase)
        val pagingState = PagingState<Int, GitHubUser>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(PAGE_SIZE_PER_REQUEST),
            leadingPlaceholderCount = 10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
        val throwable = (result as RemoteMediator.MediatorResult.Error).throwable
        assertThat(throwable).isInstanceOf(HttpException::class.java)
    }

}