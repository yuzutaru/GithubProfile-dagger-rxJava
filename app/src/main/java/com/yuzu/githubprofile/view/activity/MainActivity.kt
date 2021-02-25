package com.yuzu.githubprofile.view.activity

import android.os.Bundle
import com.yuzu.githubprofile.R
import com.yuzu.githubprofile.view.fragment.UserFragment

/**
 * Created by Yustar Pramudana on 23/08/2020
 */

class MainActivity: BaseViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content,
                            UserFragment()
                    ).commit()
        }
    }
}