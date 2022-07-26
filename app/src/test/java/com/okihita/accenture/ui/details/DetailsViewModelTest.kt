package com.okihita.accenture.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.remote.GitHubApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockApi: GitHubApi = mock()
    private val detailsVM = DetailsViewModel(mockApi)

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
        detailsVM.getUserDetails(1000)
        advanceUntilIdle()
        verify(mockApi).getUserById(1000)
    }

    @Test
    fun viewModelCallsGetUserById_liveDataRetrieved() = runTest {
        val mockUser = GitHubUser(
            id = 1000,
            login = "hello1000",
            avatar_url = "avatar",
            gravatar_id = "gravatar"
        )

        `when`(mockApi.getUserById(1000))
            .thenReturn(mockUser)

        detailsVM.getUserDetails(1000)
        advanceUntilIdle()

        assertThat(detailsVM.user.value).isEqualTo(mockUser)
    }
}