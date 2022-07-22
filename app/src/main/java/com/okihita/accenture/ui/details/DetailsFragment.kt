package com.okihita.accenture.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.okihita.accenture.R
import com.okihita.accenture.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}