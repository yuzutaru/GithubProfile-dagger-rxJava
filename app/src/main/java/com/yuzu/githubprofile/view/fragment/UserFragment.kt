package com.yuzu.githubprofile.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.utils.ARGUMENT_LOGIN
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

        viewModel.fragment = this

        viewModel.getUser()
        viewModel.userDataLive().observe(viewLifecycleOwner, { viewModel.userResponse(it) })
        viewModel.userDBDataLive().observe(viewLifecycleOwner, {viewModel.userDBResponse(it)})
        viewModel.loginDataLive().observe(viewLifecycleOwner, { viewModel.itemClickedRes(it) })
    }

    fun setListUser() {
        val adapter = UserListAdapter(viewModel, requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.loading.value = false
    }

    fun userDetail(login: String) {
        Log.e(LOG_TAG, "Login : $login")
        val bundle = bundleOf(ARGUMENT_LOGIN to login)
        (activity as MainActivity).replaceFragment(R.id.main_content, ProfileFragment(), bundle)
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