package com.yuzu.githubprofile.model.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Yustar Pramudana on 24/02/2021
 */

@Entity
@Parcelize
data class SinceData(
    @PrimaryKey
    var id: Int,

    var listUser: List<UserData>
): Parcelable