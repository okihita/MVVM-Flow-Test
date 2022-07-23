package com.okihita.accenture.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.model.GitHubUserSearchResponse
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
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val gitHubApi: GitHubApi = mock()
    private var listVM = ListViewModel(gitHubApi)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModelCallsSearchUsers_apiClientCallsGetUsers() = runTest {
        listVM.searchUsers("hello")
        advanceUntilIdle()
        verify(gitHubApi).getUsers(anyString(), anyInt(), anyInt())
    }

    @Test
    fun viewModelCallsSearchUsers_liveDataRetrieved() = runTest {

        val mockUsers = listOf(
            GitHubUser(1, "hello", "avatar", "gravatar"),
            GitHubUser(2, "hello2", "avatar", "gravatar"),
        )

        `when`(gitHubApi.getUsers("hello")).thenReturn(
            GitHubUserSearchResponse(false, mockUsers, 2)
        )

        listVM.searchUsers("hello")
        advanceUntilIdle()

        assertThat(listVM.users.value).hasSize(2)
    }
}