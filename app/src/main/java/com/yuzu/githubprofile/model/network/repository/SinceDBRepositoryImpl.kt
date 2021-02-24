package com.yuzu.githubprofile.model.network.repository

import com.yuzu.githubprofile.model.data.SinceData
import com.yuzu.githubprofile.model.network.local.SinceDAO
import io.reactivex.Single
import java.util.concurrent.Executor

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

class SinceDBRepositoryImpl(private val dao: SinceDAO, private val exec: Executor): SinceDBRepository {
    override fun getAllSince(): Single<List<SinceData>> {
        return dao.getAllSince()
    }

    override fun getSince(since: Int): Single<SinceData> {
        return dao.getSince(since)
    }

    override fun insert(data: SinceData) {
        exec.execute {
            dao.insert(data)
        }
    }

    override fun insert(list: List<SinceData>) {
        exec.execute {
            dao.insert(list)
        }
    }

    override fun deleteAllSinces() {
        exec.execute {
            dao.deleteAllSinces()
        }
    }

    override fun deleteAllAndInsert(list: List<SinceData>) {
        exec.execute {
            dao.deleteAllAndInsert(list)
        }
    }
}