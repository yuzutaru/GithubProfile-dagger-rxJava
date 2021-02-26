package com.yuzu.githubprofile.model.network.local

import com.yuzu.githubprofile.model.data.UserData
import org.junit.Assert
import org.junit.Test

/**
 * Created by Yustar Pramudana on 26/02/2021
 */

class UserDAOTest: UserDBTest() {

    //GetUserBySince
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
    fun getUserBySinceListInputOneByOneTest() {
        db.userDAO().insert(listOf(UserData(0, 0), UserData(1,0)))
        db.userDAO().insert(listOf(UserData(2, 0), UserData(3,0)))
        val userDataList = db.userDAO().getUserBySinceId(0).blockingGet()
        Assert.assertEquals(userDataList.size, 4)
    }

    //GetUserBySearch
    @Test
    fun getUserBySearchSingleInputTest() {
        db.userDAO().insert(UserData(0, 0, "Yuzutaru"))
        val userDataList = db.userDAO().getUsersBySearch("Yuz").blockingGet()
        Assert.assertEquals(userDataList.size, 1)
    }

    //GetUserBySearch
    @Test
    fun getUserBySearchSingleInputOneByOneTest() {
        db.userDAO().insert(UserData(0, 0, "Yuzutaru"))
        db.userDAO().insert(UserData(1, 2, "Yustar"))
        val userDataList = db.userDAO().getUsersBySearch("Yu").blockingGet()
        Assert.assertEquals(userDataList.size, 2)
    }

    @Test
    fun getUserBySearchSingleInputOnConflictTest() {
        db.userDAO().insert(UserData(0, 0, "Yuzutaru"))
        db.userDAO().insert(UserData(0, 2, "Yustar"))
        val userDataList = db.userDAO().getUsersBySearch("Yus").blockingGet()
        Assert.assertEquals(userDataList.size, 1)
    }

    @Test
    fun getUserBySearchListInputTest() {
        db.userDAO().insert(listOf(UserData(0, 0, "Yuzutaru"), UserData(1, 2, "Yustar")))
        val userDataList = db.userDAO().getUsersBySearch("Yuz").blockingGet()
        Assert.assertEquals(userDataList.size, 1)
    }

    //InsertUser
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