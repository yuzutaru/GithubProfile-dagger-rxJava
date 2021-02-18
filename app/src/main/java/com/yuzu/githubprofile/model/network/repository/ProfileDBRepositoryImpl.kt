package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.UserData
import com.yuzu.githubprofile.model.network.local.UserDAO
import io.reactivex.Single
import java.util.concurrent.Executor

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

class ProfileDBRepositoryImpl(private val dao: UserDAO, private val exec: Executor): ProfileDBRepository {
    override fun getAllUsers(): Single<List<UserData>> {
        return dao.getAllUsers()
    }

    override fun getUser(): Single<UserData> {
        return dao.getUser()
    }

    override fun insert(userData: UserData) {
        exec.execute {
            dao.insert(userData)
        }
    }

    override fun insert(userDataList: List<UserData>) {
        exec.execute {
            dao.insert(userDataList)
        }
    }

    override fun deleteAllLogin() {
        exec.execute {
            dao.deleteAllLogin()
        }
    }
}