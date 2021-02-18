package com.yuzu.githubprofile

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.yuzu.githubprofile.injection.component.AppComponent
import com.yuzu.githubprofile.injection.component.DaggerAppComponent
import com.yuzu.githubprofile.injection.module.AppModule

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class GithubProfileApplication: Application() {
    lateinit var component: AppComponent

    fun getAppComponent(): AppComponent {
        return component
    }

    companion object {
        lateinit var instance: GithubProfileApplication private set
    }
    operator fun get(context: Context): GithubProfileApplication {
        return context.applicationContext as GithubProfileApplication
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }


    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()
        instance = this
        //
        // DI
        //
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        component.inject(this)
        //initTextSize()
    }
}