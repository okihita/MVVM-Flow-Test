package com.okihita.accenture.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import coil.load
import com.okihita.accenture.R
import com.okihita.accenture.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val detailsVM: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        val userId = args.userId // from the Navigation Arguments

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            detailsVM.getUserDetails(userId).collectLatest { result ->
                try {
                    val user = result.getOrThrow()
                    binding.apply {
                        tvUsername.text = user.name
                        tvBio.text = user.bio
                        ivAvatar.load(user.avatar_url)
                    }
                } catch (exception: Exception) {
                    binding.tvUsername.text = exception.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}