package com.yuzu.githubprofile.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.view.activity.MainActivity
import com.yuzu.githubprofile.view.adapter.UserListAdapter
import com.yuzu.githubprofile.viewmodel.UserViewModel

/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserFragment: Fragment() {
    private val LOG_TAG = "User"
    private lateinit var binding: FragmentUserBinding
    private lateinit var viewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onBackPressed()

        viewModel.getUser()
        viewModel.userDataLive().observe(viewLifecycleOwner, Observer { viewModel.userResponse(this, it) })
    }

    fun userSuccess() {
        val adapter = UserListAdapter(viewModel, requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.loading.value = false
    }

    private fun onBackPressed() {
        requireView().isFocusableInTouchMode = true;
        requireView().requestFocus();
        requireView().setOnKeyListener { _, p1, _ ->
            if (p1 == KeyEvent.KEYCODE_BACK)
                (activity as MainActivity).finish()

            true
        }
    }
}