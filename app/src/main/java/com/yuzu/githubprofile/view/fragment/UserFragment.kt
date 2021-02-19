package com.yuzu.githubprofile.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.viewmodel.UserViewModel

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserFragment: Fragment() {
    private val LOG_TAG = "User"
    private lateinit var viewDataBinding: FragmentUserBinding
    private lateinit var viewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentUserBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}