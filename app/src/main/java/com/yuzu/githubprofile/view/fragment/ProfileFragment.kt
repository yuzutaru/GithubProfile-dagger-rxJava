package com.yuzu.githubprofile.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.FragmentProfileBinding
import com.yuzu.githubprofile.view.activity.MainActivity
import com.yuzu.githubprofile.viewmodel.ProfileViewModel

/**
 * Created by Yustar Pramudana on 20/02/2021
 */

class ProfileFragment: Fragment() {
    private val LOG_TAG = "UserDetail"
    lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        backOnClickListener()
        onBackPressed()

        viewModel.fragment = this
        viewModel.getLogin(arguments)
        viewModel.profileDBDataLive().observe(viewLifecycleOwner, { viewModel.profileDBRes(it)})
        viewModel.profileDataLive().observe(viewLifecycleOwner, { viewModel.profileRes(it) })
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
            /*if (p1 == KeyEvent.KEYCODE_BACK)
                (activity as MainActivity).replaceFragment(R.id.main_content, UserFragment(), null)*/

            true
        }
    }
}