package com.yuzu.githubprofile.model.network.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import java.io.IOException

/**
 * Created by Yustar Pramudana on 26/02/2021
 */

abstract class UserDBTest {
    lateinit var db: UserDB

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                UserDB::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}