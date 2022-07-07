package com.yuzu.githubprofile.repository.model.local

import com.yuzu.githubprofile.repository.data.NotesData
import org.junit.Assert
import org.junit.Test

/**
 * Created by Yustar Pramudana on 26/02/2021
 */

class NotesDAOTest: NotesDBTest() {
    //GetAll
    @Test
    fun getAllSingleInputTest() {
        db.notesDAO().insert(NotesData(0))
        val notesDataList = db.notesDAO().getAll().blockingGet()
        Assert.assertEquals(notesDataList.size, 1)
    }

    @Test
    fun getAllSingleInputOneByOneTest() {
        db.notesDAO().insert(NotesData(0))
        db.notesDAO().insert(NotesData(1))
        val notesDataList = db.notesDAO().getAll().blockingGet()
        Assert.assertEquals(notesDataList.size, 2)
    }

    @Test
    fun getAllSingleInputOnConflictTest() {
        db.notesDAO().insert(NotesData(0))
        db.notesDAO().insert(NotesData(0))
        val notesDataList = db.notesDAO().getAll().blockingGet()
        Assert.assertEquals(notesDataList.size, 1)
    }

    @Test
    fun getAllListInputTest() {
        db.notesDAO().insert(listOf(NotesData(0), NotesData(1)))
        val notesDataList = db.notesDAO().getAll().blockingGet()
        Assert.assertEquals(notesDataList.size, 2)
    }

    //Get(login)
    @Test
    fun getSingleInputTest() {
        db.notesDAO().insert(NotesData(0, "yuzutaru"))
        val notesData = db.notesDAO().get("yuzutaru").blockingGet()
        Assert.assertNotNull(notesData)
    }

    @Test
    fun getSingleInputOneByOneTest() {
        db.notesDAO().insert(NotesData(0, "yuzutaru"))
        db.notesDAO().insert(NotesData(1, "yustar"))
        val notesData = db.notesDAO().get("yuzutaru").blockingGet()
        Assert.assertNotNull(notesData)
    }

    @Test
    fun getSingleInputOnConflictTest() {
        db.notesDAO().insert(NotesData(0, "yuzutaru"))
        db.notesDAO().insert(NotesData(0, "yustar"))
        val notesData = db.notesDAO().get("yustar").blockingGet()
        Assert.assertNotNull(notesData)
    }

    @Test
    fun getListInputTest() {
        db.notesDAO().insert(listOf(NotesData(0, "yuzutaru"), NotesData(1, "yustar")))
        val notesData = db.notesDAO().get("yustar").blockingGet()
        Assert.assertNotNull(notesData)
    }

    //insert
    @Test
    fun insertSingleInputTest() {
        db.notesDAO().insert(NotesData(0))
        val notesDataList = db.notesDAO().getAll().blockingGet().size
        Assert.assertEquals(notesDataList, 1)
    }

    @Test
    fun insertSingleInputOneByOneTest() {
        db.notesDAO().insert(NotesData(0))
        db.notesDAO().insert(NotesData(1))
        val notesDataList = db.notesDAO().getAll().blockingGet().size
        Assert.assertEquals(notesDataList, 2)
    }

    @Test
    fun insertSingleInputOnConflictTest() {
        db.notesDAO().insert(NotesData(0))
        db.notesDAO().insert(NotesData(0))
        val notesDataList = db.notesDAO().getAll().blockingGet().size
        Assert.assertEquals(notesDataList, 1)
    }

    @Test
    fun insertListInputTest() {
        db.notesDAO().insert(listOf(NotesData(0), NotesData(1)))
        val notesDataList = db.notesDAO().getAll().blockingGet().size
        Assert.assertEquals(notesDataList, 2)
    }
}