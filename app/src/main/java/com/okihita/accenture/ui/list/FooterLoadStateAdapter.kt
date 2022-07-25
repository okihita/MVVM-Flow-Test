package com.okihita.accenture.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.okihita.accenture.R
import com.okihita.accenture.databinding.ItemFooterLoadStateBinding
import com.okihita.accenture.util.ResultException
import com.okihita.accenture.util.toUserFriendlyErrorMessage

class FooterLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<FooterLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view =
            ItemFooterLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: ItemFooterLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {

            binding.apply {

                pbAppendState.isVisible = loadState is LoadState.Loading
                tvAppendError.isVisible =
                    loadState !is LoadState.Loading && loadState is LoadState.Error

                loadState.let {
                    if (it is LoadState.Error) {
                        if (it.error is ResultException.NoMoreResultException) {
                            tvAppendError.text =
                                root.context.getString(R.string.listFragment_noMoreResult)
                        } else {
                            tvAppendError.text =
                                it.error.toUserFriendlyErrorMessage() + " Click here to retry."
                            tvAppendError.setOnClickListener { retry() }
                        }
                    }

                    if (it.endOfPaginationReached) {
                        tvAppendError.text =
                            root.context.getString(R.string.listFragment_noMoreResult)
                    }
                }
            }
        }
    }
}