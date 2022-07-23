package com.okihita.accenture.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.databinding.ItemUserBinding

class GitHubUsersAdapter(
    val onItemClick: (GitHubUser) -> Unit
) : PagingDataAdapter<GitHubUser, GitHubUsersAdapter.GitHubUserVH>(UserComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubUserVH {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GitHubUserVH(view)
    }

    override fun onBindViewHolder(holder: GitHubUserVH, position: Int) {
        getItem(position)?.let { user ->
            holder.bind(user)
        }
    }

    inner class GitHubUserVH(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: GitHubUser) {
            binding.tvUsername.text = user.login
            binding.ivAvatar.load(user.avatar_url)

            binding.root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    object UserComparator : DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem == newItem
        }
    }
}
