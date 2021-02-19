package com.yuzu.githubprofile.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.viewmodel.UserViewModel


/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserListViewHolder constructor(private val viewModel: UserViewModel?, itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemUserListBinding.bind(itemView)

    fun bind(i: Int, data: List<UserData>) {
        viewModel!!.setImage(i, data, itemView, binding)

        binding.login.text = data[i].login
        binding.repos.text = data[i].reposUrl
    }
}