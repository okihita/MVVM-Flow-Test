package com.okihita.accenture.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        listVM.users.observe(viewLifecycleOwner) { users ->
            binding.tvList.text = users.joinToString { user -> user.login }
        }

        binding.btSearch.setOnClickListener {
            binding.tvList.text = "Loading..."
            val searchQuery = binding.etSearchQuery.text.toString()
            listVM.searchUsers(searchQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}