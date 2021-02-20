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
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.FragmentUserDetailBinding
import com.yuzu.githubprofile.view.activity.MainActivity
import com.yuzu.githubprofile.viewmodel.UserDetailViewModel

/**
 * Created by Yustar Pramudana on 20/02/2021
 */

class UserDetailFragment: Fragment() {
    private val LOG_TAG = "UserDetail"
    lateinit var binding: FragmentUserDetailBinding
    private lateinit var viewModel: UserDetailViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(UserDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        backOnClickListener()
        onBackPressed()

        viewModel.getLogin(arguments)
        viewModel.userDataLive().observe(viewLifecycleOwner, Observer { viewModel.userDetail(this, it) })
    }

    private fun backOnClickListener() {
        binding.back.setOnClickListener {
            (activity as MainActivity).replaceFragment(R.id.main_content, UserFragment(), null)
        }
    }

    private fun onBackPressed() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus();
        requireView().setOnKeyListener { _, p1, _ ->
            if (p1 == KeyEvent.KEYCODE_BACK)
                (activity as MainActivity).replaceFragment(R.id.main_content, UserFragment(), null)

            true
        }
    }
}