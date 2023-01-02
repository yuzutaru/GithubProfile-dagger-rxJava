package com.yuzu.githubprofile.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.databinding.FragmentUserBinding
import com.yuzu.githubprofile.repository.data.ConnectionLiveData
import com.yuzu.githubprofile.utils.ARGUMENT_LOGIN
import com.yuzu.githubprofile.view.activity.MainActivity
import com.yuzu.githubprofile.view.adapter.UserListPagedAdapter
import com.yuzu.githubprofile.viewmodel.UserViewModel
import java.util.*


/**
 * Created by Yustar Pramudana on 19/02/2021
 */

class UserFragment: Fragment() {
    private val LOG_TAG = "User"
    private lateinit var binding: FragmentUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var userListAdapter: UserListPagedAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initState()
        searchTextChangeListener()
        onBackPressed()

        viewModel.fragment = this
        viewModel.connectionLiveData = ConnectionLiveData(requireContext())

        viewModel.connectionLiveData.observe(viewLifecycleOwner) { connection -> viewModel.connection(connection) }
        viewModel.loginDataLive().observe(viewLifecycleOwner) { viewModel.itemClickedRes(it) }
    }

    private fun initAdapter() {
        userListAdapter = UserListPagedAdapter(viewModel) { viewModel.retry() }
        binding.recyclerView.adapter = userListAdapter
        viewModel.search.value = ""
        viewModel.userPagedList.observe(viewLifecycleOwner) {
            try {
                Log.e("Paging ", "PageAll" + it.size)
                try {
                    //to prevent animation recyclerview when change the list
                    binding.recyclerView.setItemAnimator(null)
                    (Objects.requireNonNull(binding.recyclerView.getItemAnimator()) as SimpleItemAnimator).supportsChangeAnimations = false
                } catch (e: Exception) {
                    e.message?.let { it1 -> Log.e("devLog", it1) }
                }
                userListAdapter.submitList(it)
            } catch (e: Exception) {
                e.message?.let { it1 -> Log.e("devLog", it1) }
            }
        }
    }

    private fun initState() {
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            viewModel.recyclerViewVisibility(binding, state, userListAdapter)
            //footerBinding.progressBar.visibility = if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            //footerBinding.txtError.visibility = if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
        }
    }

    private fun searchTextChangeListener() {
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.search.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //viewModel.search.value = p0.toString()
            }
        })
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