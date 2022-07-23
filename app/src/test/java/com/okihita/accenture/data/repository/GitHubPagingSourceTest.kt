package com.okihita.accenture.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.model.GitHubUserSearchResponse
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import com.okihita.accenture.util.ResultException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.HttpException

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class GitHubPagingSourceTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun generateUsers(size: Int): List<GitHubUser> {
        val users = mutableListOf<GitHubUser>()
        (1..size).forEach {
            users.add(GitHubUser(it, "hello$it", "avatar", "gravatar"))
        }
        return users.toList()
    }

    private val mockApi: GitHubApi = mock()
    private val pagingSource = GitHubPagingSource(mockApi, "hello")

    @Test
    fun onRefreshCall_apiSuccessEmptyResult_throwEmptyResultException() = runTest {

        val mockResponse = GitHubUserSearchResponse(
            incomplete_results = false,
            users = generateUsers(0),
            total_count = 0
        )
        `when`(mockApi.getUsers(query = "hello", perPage = 10, page = 1))
            .thenReturn(mockResponse)

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE_PER_REQUEST,
                placeholdersEnabled = false
            )
        )

        assertThat(actual).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        assertThat((actual as PagingSource.LoadResult.Error).throwable)
            .isInstanceOf(ResultException.EmptyResultException::class.java)
    }

    @Test
    fun onAppendCall_apiSuccessEmptyResult_throwNoMoreResultException() = runTest {

        val mockResponse = GitHubUserSearchResponse(
            incomplete_results = false,
            users = generateUsers(0),
            total_count = 0
        )
        `when`(mockApi.getUsers(query = "hello", perPage = 10, page = 5))
            .thenReturn(mockResponse)

        val actual = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 5,
                loadSize = PAGE_SIZE_PER_REQUEST,
                placeholdersEnabled = false
            )
        )

        assertThat(actual).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        assertThat((actual as PagingSource.LoadResult.Error).throwable)
            .isInstanceOf(ResultException.NoMoreResultException::class.java)
    }

    @Test
    fun onRefreshCall_apiSuccess_loadReturnsPageOne() = runTest {

        val numOfUsers = PAGE_SIZE_PER_REQUEST - 2
        val mockResponse =
            GitHubUserSearchResponse(
                incomplete_results = false,
                users = generateUsers(numOfUsers),
                total_count = numOfUsers
            )

        `when`(mockApi.getUsers(query = "hello", perPage = 10, page = 1))
            .thenReturn(mockResponse)

        val expected = PagingSource.LoadResult.Page(
            data = mockResponse.users,
            prevKey = null,
            nextKey = 2
        )

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE_PER_REQUEST,
                placeholdersEnabled = false
            )
        )

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun onAppendCallPageTwo_apiSuccess_loadReturnsPageTwo() = runTest {

        val mockResponsePageOne = // Ten returned results
            GitHubUserSearchResponse(
                incomplete_results = false,
                users = generateUsers(10),
                total_count = 10
            )
        val mockResponsePageTwo = // Just five returned results
            GitHubUserSearchResponse(
                incomplete_results = false,
                users = generateUsers(5),
                total_count = 5
            )

        `when`(mockApi.getUsers("hello", perPage = 10, page = 1))
            .thenReturn(mockResponsePageOne)
        `when`(mockApi.getUsers("hello", perPage = 10, page = 2))
            .thenReturn(mockResponsePageTwo)

        val expectedPageTwo = PagingSource.LoadResult.Page(
            data = mockResponsePageTwo.users,
            prevKey = 1,
            nextKey = 3
        )

        // Documentation Note: It is valid for PagingSource.load to return a LoadResult that has
        // a different number of items than the requested load size.
        val actualPageTwo: PagingSource.LoadResult<Int, GitHubUser> = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = PAGE_SIZE_PER_REQUEST,
                placeholdersEnabled = false
            )
        )

        advanceUntilIdle()
        verify(mockApi).getUsers("hello", 10, 2)
        assertThat(expectedPageTwo).isEqualTo(actualPageTwo)
        assertThat(expectedPageTwo.data.size).isNotEqualTo(mockResponsePageOne.users.size)
    }

    @Test
    fun onHttpExceptionThrown_loadReturnsError() = runTest {

        `when`(mockApi.getUsers("hello"))
            .thenThrow(HttpException::class.java)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE_PER_REQUEST,
                placeholdersEnabled = false
            )
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        val throwable = (result as PagingSource.LoadResult.Error).throwable
        assertThat(throwable).isInstanceOf(HttpException::class.java)
    }
}