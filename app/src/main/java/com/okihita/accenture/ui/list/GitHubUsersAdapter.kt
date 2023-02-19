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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GitHubUserVH(
        ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GitHubUserVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class GitHubUserVH(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: GitHubUser) = with(binding) {
            tvUsername.text = user.login
            ivAvatar.load(user.avatar_url)
            root.setOnClickListener { onItemClick(user) }
        }
    }

    object UserComparator : DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(old: GitHubUser, new: GitHubUser) = old.id == new.id

        override fun areContentsTheSame(old: GitHubUser, new: GitHubUser) = old == new
    }
}
