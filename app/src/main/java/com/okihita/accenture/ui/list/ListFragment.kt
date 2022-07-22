package com.okihita.accenture.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.okihita.accenture.R
import com.okihita.accenture.data.remote.GitHubApi
import com.okihita.accenture.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    @Inject
    lateinit var api: GitHubApi

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        binding.btSearch.setOnClickListener {
            val searchQuery = binding.etSearchQuery.text.toString()
            searchUser(searchQuery)
        }
    }

    private fun searchUser(searchQuery: String) {
        lifecycleScope.launch {
            binding.tvList.text = api.getUsers(searchQuery).users.joinToString { it.login }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}