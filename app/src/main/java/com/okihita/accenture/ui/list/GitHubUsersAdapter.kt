package com.okihita.accenture.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.databinding.ItemUserBinding

class GitHubUsersAdapter(
    val onItemClick: (GitHubUser) -> Unit
) : RecyclerView.Adapter<GitHubUsersAdapter.GitHubUserVH>() {

    private var users: MutableList<GitHubUser> = mutableListOf()

    fun submitData(newUsers: List<GitHubUser>) {
        this.users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubUserVH {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GitHubUserVH(view)
    }

    override fun onBindViewHolder(holder: GitHubUserVH, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
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
}