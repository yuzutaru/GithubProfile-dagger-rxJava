package com.yuzu.githubprofile.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.ItemUserListBinding
import com.yuzu.githubprofile.repository.data.UserData
import com.yuzu.githubprofile.viewmodel.UserViewModel

/**
 * Created by Yustar Pramudana on 23/02/2021
 */

class UserListPagedViewHolder(private val viewModel: UserViewModel, view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemUserListBinding.bind(view)

    fun create(parent: ViewGroup): UserListPagedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false)
        return UserListPagedViewHolder(viewModel, view)
    }

    fun bind(i: Int, data: UserData?) {
        if (data != null) {
            viewModel.setImage(i, data, binding)
            viewModel.setNote(binding, data)

            binding.login.text = data.login
            binding.repos.text = data.reposUrl

            binding.background.setOnClickListener {
                viewModel.itemClicked(data.login)
            }
        }
    }
}