package com.yuzu.githubprofile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.viewmodel.UserViewModel

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserListAdapter(private val viewModel: UserViewModel?, private val context: Context):
    RecyclerView.Adapter<UserListViewHolder>() {
    private var lastPosition: Int = -1
    private var mPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user_list, parent, false)

        return UserListViewHolder(viewModel, view)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        mPosition++
        if (viewModel != null) {
            holder.bind(position, viewModel.userList)
        }

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        var result = 0
        if (viewModel != null)
            result = viewModel.userList.size

        return result
    }

    private fun setAnimation(viewToAnimate: View, i: Int) {
        if (i > lastPosition) {
            var animation: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            viewToAnimate.startAnimation(animation)
            lastPosition = i
        }
    }
}