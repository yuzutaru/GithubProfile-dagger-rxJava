package com.yuzu.githubprofile.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.model.data.UserData

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserListViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemUserListBinding.bind(itemView)

    fun bind(i: Int, data: List<UserData>) {
        Glide.with(itemView).load(data[i].avatarUrl).into(binding.avatar)
        binding.login.text = data[i].login
        binding.repos.text = data[i].reposUrl
    }
}