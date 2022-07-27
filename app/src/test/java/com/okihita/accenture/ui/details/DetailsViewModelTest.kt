package com.okihita.accenture.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.okihita.accenture.data.model.GitHubUserDetails
import com.okihita.accenture.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModelCallsGetUserById_apiClientCallsMethod() = runTest {

        val mockRepo: GitHubRepository = mock()
        val detailsVM = DetailsViewModel(mockRepo)

        `when`(mockRepo.getUserDetails(999))
            .thenReturn(flow {
                emit(Result.success(userPhilipp))
            })

        detailsVM.getUserDetails(999)
        advanceUntilIdle()
        verify(mockRepo).getUserDetails(999)
    }

    @Test
    fun viewModelCallsGetUserById_repoReturnsSuccess_viewModelGetsUserDetailsObject() = runTest {

        val mockRepo: GitHubRepository = mock()
        val newDetailsVM = DetailsViewModel(mockRepo)

        `when`(mockRepo.getUserDetails(1))
            .thenReturn(flowOf(Result.success(userPhilipp)))

        val result = newDetailsVM.getUserDetails(1).first()
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isNotNull()
        assertThat(result.getOrNull()).isEqualTo(userPhilipp)
    }

    @Test(expected = HttpException::class) // Because we're expecting and exception
    fun viewModelCallsGetUserById_repoReturnsHTTP404_viewModelGetsHttp404Exception() = runTest {

        val mockRepo: GitHubRepository = mock()
        val newDetailsVM = DetailsViewModel(mockRepo)
        val mockHttp404Response = Response.error<GitHubUserDetails>(
            404, "My 404 error".toResponseBody("plain/text".toMediaTypeOrNull())
        )
        `when`(mockRepo.getUserDetails(1))
            .thenThrow(HttpException(mockHttp404Response))

        newDetailsVM.getUserDetails(1)
    }

    @Test
    fun viewModelCallsGetUserById_repoReturnsHTTP404_viewModelGetsHttp404TryCatch() = runTest {

        val mockRepo: GitHubRepository = mock()
        val detailsVM = DetailsViewModel(mockRepo)
        val mockHttp404Response = Response.error<GitHubUserDetails>(
            404, "My 404 error".toResponseBody("plain/text".toMediaTypeOrNull())
        )
        `when`(mockRepo.getUserDetails(1))
            .thenReturn(flow { emit(Result.failure(HttpException(mockHttp404Response))) })

        val result = detailsVM.getUserDetails(1).first()
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull()).isInstanceOf(HttpException::class.java)
        val notFoundException = result.exceptionOrNull() as HttpException
        assertThat(notFoundException.code()).isEqualTo(404)
    }


    private val philippJson = """
        {
            "login": "philipplackner",
            "id": 53933333,
            "node_id": "MDQ6VXNlcjUzOTMzMzMz",
            "avatar_url": "https://avatars.githubusercontent.com/u/53933333?v=4",
            "gravatar_id": "",
            "url": "https://api.github.com/users/philipplackner",
            "html_url": "https://github.com/philipplackner",
            "followers_url": "https://api.github.com/users/philipplackner/followers",
            "following_url": "https://api.github.com/users/philipplackner/following{/other_user}",
            "gists_url": "https://api.github.com/users/philipplackner/gists{/gist_id}",
            "starred_url": "https://api.github.com/users/philipplackner/starred{/owner}{/repo}",
            "subscriptions_url": "https://api.github.com/users/philipplackner/subscriptions",
            "organizations_url": "https://api.github.com/users/philipplackner/orgs",
            "repos_url": "https://api.github.com/users/philipplackner/repos",
            "events_url": "https://api.github.com/users/philipplackner/events{/privacy}",
            "received_events_url": "https://api.github.com/users/philipplackner/received_events",
            "type": "User",
            "site_admin": false,
            "name": "Philipp Lackner",
            "company": null,
            "blog": "",
            "location": "Germany",
            "email": null,
            "hireable": null,
            "bio": "I post awesome Android stuff on my Instagram page @philipplackner_official and on my YouTube channel Philipp Lackner.",
            "twitter_username": null,
            "public_repos": 142,
            "public_gists": 4,
            "followers": 4878,
            "following": 1,
            "created_at": "2019-08-09T07:42:14Z",
            "updated_at": "2022-05-05T09:43:30Z"
        }
    """.trimIndent()

    private val userPhilipp = Gson().fromJson(philippJson, GitHubUserDetails::class.java)

}