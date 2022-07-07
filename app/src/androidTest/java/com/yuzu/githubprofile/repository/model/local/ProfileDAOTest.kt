package com.yuzu.githubprofile.repository.model.local

import com.yuzu.githubprofile.repository.data.ProfileData
import org.junit.Assert
import org.junit.Test

/**
 * Created by Yustar Pramudana on 26/02/2021
 */

class ProfileDAOTest: ProfileDBTest() {
    //getAllProfilesBySingle
    @Test
    fun getAllProfileSingleInputTest() {
        db.profileDAO().insert(ProfileData(0))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet()
        Assert.assertEquals(profileDataList.size, 1)
    }

    @Test
    fun getAllProfileSingleInputOneByOneTest() {
        db.profileDAO().insert(ProfileData(0))
        db.profileDAO().insert(ProfileData(1))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet()
        Assert.assertEquals(profileDataList.size, 2)
    }

    @Test
    fun getAllProfileSingleInputOnConflictTest() {
        db.profileDAO().insert(ProfileData(0))
        db.profileDAO().insert(ProfileData(0))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet()
        Assert.assertEquals(profileDataList.size, 1)
    }

    @Test
    fun getAllProfileListInputTest() {
        db.profileDAO().insert(listOf(ProfileData(0), ProfileData(1)))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet()
        Assert.assertEquals(profileDataList.size, 2)
    }

    //GetProfileByLogin
    @Test
    fun getProfileByLoginSingleInputTest() {
        db.profileDAO().insert(ProfileData(0, "Yuzutaru"))
        val profileData = db.profileDAO().getProfile("Yuzutaru").blockingGet()
        Assert.assertNotNull(profileData)
    }

    @Test
    fun getProfileByLoginSingleInputOneByOneTest() {
        db.profileDAO().insert(ProfileData(0, "Yuzutaru"))
        db.profileDAO().insert(ProfileData(1, "Yustar"))
        val profileData = db.profileDAO().getProfile("Yuzutaru").blockingGet()
        Assert.assertNotNull(profileData)
    }

    @Test
    fun getProfileByLoginSingleInputOnConflictTest() {
        db.profileDAO().insert(ProfileData(0, "Yuzutaru"))
        db.profileDAO().insert(ProfileData(0, "Yustar"))
        val profileData = db.profileDAO().getProfile("Yustar").blockingGet()
        Assert.assertNotNull(profileData)
    }

    @Test
    fun getProfileByLoginListInputTest() {
        db.profileDAO().insert(listOf(ProfileData(0, "Yuzutaru"), ProfileData(1, "Yustar")))
        val profileData = db.profileDAO().getProfile("Yuzutaru").blockingGet()
        Assert.assertNotNull(profileData)
    }

    @Test
    fun insertProfileDataTest() {
        db.profileDAO().insert(ProfileData(0, "yuzutaru"))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet().size
        Assert.assertEquals(profileDataList, 1)
    }

    @Test
    fun insertProfileDataOneByOneTest() {
        db.profileDAO().insert(ProfileData(0, "yuzutaru"))
        db.profileDAO().insert(ProfileData(1, "yustar"))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet().size
        Assert.assertEquals(profileDataList, 2)
    }

    @Test
    fun insertProfileDataOnConflictTest() {
        db.profileDAO().insert(ProfileData(0, "yuzutaru"))
        db.profileDAO().insert(ProfileData(0, "yustar"))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet().size
        Assert.assertEquals(profileDataList, 1)
    }

    @Test
    fun insertProfileDataListInputTest() {
        db.profileDAO().insert(listOf(ProfileData(0, "yuzutaru"), ProfileData(1, "yustar")))
        val profileDataList = db.profileDAO().getAllProfiles().blockingGet().size
        Assert.assertEquals(profileDataList, 2)
    }
}