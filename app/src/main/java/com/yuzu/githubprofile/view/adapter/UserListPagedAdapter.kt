package com.yuzu.githubprofile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.State
import com.yuzu.githubprofile.viewmodel.UserViewModel

/**
 * Created by Yustar Pramudana on 23/02/2021
 */

class UserListPagedAdapter(private val viewModel: UserViewModel, private val retry: () -> Unit):
        PagedListAdapter<UserData, RecyclerView.ViewHolder>(UserDiffCallback) {
    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2
    private var state = State.LOADING

    companion object {
        val UserDiffCallback = object : DiffUtil.ItemCallback<UserData>() {
            override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem.login == newItem.login
            }

            override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user_list, parent, false)

        return if (viewType == DATA_VIEW_TYPE) UserListPagedViewHolder(viewModel, view).create(parent) else SkeletonUserListViewHolder.create(retry, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            (holder as UserListPagedViewHolder).bind(getItem(position))
        else (holder as SkeletonUserListViewHolder).bind(state)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    override fun submitList(pagedList: PagedList<UserData>?) {
        lateinit var newPagedList: PagedList<UserData>
        if (pagedList != null) {
            if (pagedList.size > 5) {
                for (i in pagedList.indices) {
                    newPagedList.add(i, pagedList[i])
                }

                super.submitList(newPagedList)

            } else {
                super.submitList(pagedList)
            }
        } else {
            super.submitList(pagedList)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR)
    }

    fun setState(state: State) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }
}