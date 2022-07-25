package com.okihita.accenture.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.verify

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
    fun viewModelCallsSearchUsers_repoCallsSearchUsers() = runTest {

        val repository: GitHubRepository = mock()
        val listVM = ListViewModel(repository)

        // Use TestScope() in tests to replace viewModelScope
        `when`(repository.getSearchResultFlow("hello"))
            .thenReturn(flowOf(PagingData.from(generateUsers(0))).cachedIn(TestScope()))

        listVM.searchUsers("hello")
        advanceUntilIdle()
        verify(repository).getSearchResultFlow("hello")
    }

    @Test
    fun viewModelCallsSearchUsers_flowOfPagingDataReturned() = runTest {

        val repository: GitHubRepository = mock()
        val listVM = ListViewModel(repository)

        `when`(repository.getSearchResultFlow("hello"))
            .thenReturn(flowOf(PagingData.from(generateUsers(3))))

        val result = listVM.searchUsers("hello").first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MyDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(result)
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