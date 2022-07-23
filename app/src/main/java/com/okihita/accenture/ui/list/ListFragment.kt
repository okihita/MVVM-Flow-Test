package com.okihita.accenture.ui.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.okihita.accenture.R
import com.okihita.accenture.databinding.FragmentListBinding
import com.okihita.accenture.util.ResultException
import com.okihita.accenture.util.toUserFriendlyErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private val listVM: ListViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var usersAdapter: GitHubUsersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        usersAdapter = GitHubUsersAdapter {
            val navAction = ListFragmentDirections.actionListFragmentToDetailsFragment(it.id)
            findNavController().navigate(navAction)
        }

        val concatAdapter =
            usersAdapter.withLoadStateFooter(FooterLoadStateAdapter { usersAdapter.retry() })

        binding.rvUsers.adapter = concatAdapter

        binding.btSearch.setOnClickListener {
            val searchQuery = binding.etSearchQuery.text.toString()

            if (searchQuery.isBlank()) {
                binding.tvError.apply {
                    isVisible = true
                    text = getString(R.string.listFragment_blankQuery)
                }
                binding.rvUsers.isVisible = false
            } else {
                searchUser(searchQuery)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            usersAdapter.loadStateFlow.collectLatest {

                it.refresh.let { refresh ->

                    binding.pbLoading.isVisible = refresh is LoadState.Loading
                    binding.rvUsers.isVisible = refresh !is LoadState.Loading
                    binding.tvError.isVisible = refresh is LoadState.Error


                    if (refresh is LoadState.Error) {
                        binding.rvUsers.isVisible = false
                        when (refresh.error) {
                            is ResultException.EmptyResultException ->
                                binding.tvError.text = getString(R.string.listFragment_emptyResult)
                            else -> {
                                binding.tvError.text = refresh.error.toUserFriendlyErrorMessage()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // On resume, the state and content of EditText will be restored. Use it to search.
        binding.etSearchQuery.text.toString().let {
            if (it.isNotBlank()) searchUser(it)
        }
    }

    private fun searchUser(searchQuery: String) {
        lifecycleScope.launch {
            listVM.searchUsers(searchQuery).collectLatest {
                usersAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}