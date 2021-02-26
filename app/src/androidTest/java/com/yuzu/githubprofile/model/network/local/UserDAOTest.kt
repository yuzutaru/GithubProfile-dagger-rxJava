package com.yuzu.githubprofile.model.network.local

import com.yuzu.githubprofile.model.data.UserData
import org.junit.Assert
import org.junit.Test

/**
 * Created by Yustar Pramudana on 26/02/2021
 */

class UserDAOTest: UserDBTest() {

    @Test
    fun getUserBySinceSingleInputTest() {
        db.userDAO().insert(UserData(0, 0))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet()
        Assert.assertEquals(userDataList.size, 1)
    }

    @Test
    fun getUserBySinceSingleInputOnebyOneTest() {
        db.userDAO().insert(UserData(0, 0))
        db.userDAO().insert(UserData(1, 0))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet()
        Assert.assertEquals(userDataList.size, 2)
    }

    @Test
    fun getUserBySinceListInputTest() {
        db.userDAO().insert(listOf(UserData(0, 0), UserData(1,0)))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet()
        Assert.assertEquals(userDataList.size, 2)
    }

    @Test
    fun insertUserDataTest() {
        db.userDAO().insert(UserData(0, 0))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet().size
        Assert.assertEquals(userDataList, 1)
    }

    @Test
    fun insertUserDataOnConflictTest() {
        db.userDAO().insert(UserData(0, 0))
        db.userDAO().insert(UserData(0, 0))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet().size
        Assert.assertEquals(userDataList, 1)
    }

    @Test
    fun insertUserDataListTest() {
        db.userDAO().insert(listOf(UserData(0, 0), UserData(1, 0)))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet().size
        Assert.assertEquals(userDataList, 2)
    }

    @Test
    fun insertUserDataListOnConflictTest() {
        db.userDAO().insert(listOf(UserData(0, 0), UserData(1, 0)))
        db.userDAO().insert(listOf(UserData(0, 0), UserData(1, 0)))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet().size
        Assert.assertEquals(userDataList, 2)
    }
}