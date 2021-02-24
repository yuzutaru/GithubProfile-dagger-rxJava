package com.yuzu.githubprofile.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.SkeletonItemUserListBinding
import com.yuzu.githubprofile.model.network.State

/**
 * Created by Yustar Pramudana on 23/02/2021
 */

class SkeletonUserListViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun create(retry: () -> Unit, parent: ViewGroup): SkeletonUserListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.skeleton_item_user_list, parent, false)
            return SkeletonUserListViewHolder(view)
        }
    }

    private val binding = SkeletonItemUserListBinding.bind(view)

    fun bind(status: State?) {
        binding.skeletonLayout.visibility = if (status == State.LOADING || status == State.ERROR) View.VISIBLE else View.INVISIBLE
        //binding.txtError.visibility = if (status == State.ERROR) View.VISIBLE else View.INVISIBLE
        //binding.progressBar.visibility = if (status == State.LOADING) View.VISIBLE else View.INVISIBLE
        //binding.txtError.visibility = if (status == State.ERROR) View.VISIBLE else View.INVISIBLE
    }
}