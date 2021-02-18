package com.yuzu.githubprofile.injection.component

import android.app.Application
import com.yuzu.githubprofile.injection.module.AppModule
import dagger.Component

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: Application)
}