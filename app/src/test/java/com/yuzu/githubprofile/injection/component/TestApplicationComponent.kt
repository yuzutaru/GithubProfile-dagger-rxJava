package com.yuzu.githubprofile.injection.component

import com.yuzu.githubprofile.injection.AppRepositoryInjectTest
import com.yuzu.githubprofile.injection.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Yustar Pramudana on 25/02/2021
 */

@Singleton
@Component(modules = [AppModule::class])
interface TestApplicationComponent {
    fun into(test: AppRepositoryInjectTest)
}