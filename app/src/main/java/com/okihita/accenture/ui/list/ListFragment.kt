package com.okihita.accenture.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.okihita.accenture.R
import com.okihita.accenture.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private val listVM: ListViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        val adapter = GitHubUsersAdapter {
            findNavController().navigate(R.id.action_listFragment_to_detailsFragment)
        }
        binding.rvUsers.adapter = adapter

        listVM.users.observe(viewLifecycleOwner) { users ->
            binding.pbLoading.visibility = View.GONE
            adapter.submitData(users)
        }

        binding.btSearch.setOnClickListener {
            binding.pbLoading.visibility = View.VISIBLE
            val searchQuery = binding.etSearchQuery.text.toString()
            listVM.searchUsers(searchQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}