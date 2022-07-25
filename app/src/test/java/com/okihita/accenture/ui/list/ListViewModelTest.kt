package com.okihita.accenture.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun generateUsers(size: Int): List<GitHubUser> {
        val users = mutableListOf<GitHubUser>()
        (1..size).forEach {
            users.add(
                GitHubUser(
                    id = it,
                    login = "hello$it",
                    avatar_url = "avatar",
                    gravatar_id = "gravatar"
                )
            )
        }
        return users.toList()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModelCallsSearchUsers_searchQueryUpdated() = runTest {

        val listVM = ListViewModel(mock())

        listVM.updateSearchQuery("helloOne")
        listVM.updateSearchQuery("helloTwo")
        listVM.updateSearchQuery("helloThree")
        listVM.updateSearchQuery("helloNine")
        advanceUntilIdle()

        val searchQuery: MutableStateFlow<String> = listVM.searchQuery
        assertThat(searchQuery.first()).isEqualTo("helloNine")
    }

    @Test
    fun viewModelCallsSearchUsers_flowOfPagingDataReturned() = runTest {

        val repository: GitHubRepository = mock()
        val listVM = ListViewModel(repository)

        `when`(repository.getSearchResultFlow("hello"))
            .thenReturn(flowOf(PagingData.from(generateUsers(3))))

        listVM.updateSearchQuery("hello")

        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(listVM.searchResultUsersFlow.first())
        advanceUntilIdle()

        assertThat(differ.snapshot().items).isEqualTo(generateUsers(3))
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class MyDiffCallback : DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem == newItem
        }
    }
}