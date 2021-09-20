package ru.mikov.storage.data.local.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: Int,
    val title: String,
    val amount: Int = 0
) : Parcelable
