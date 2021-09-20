package ru.mikov.storage.data.local.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mikov.storage.App.Companion.applicationContext
import ru.mikov.storage.BuildConfig
import ru.mikov.storage.data.local.room.dao.ProductsDao
import ru.mikov.storage.data.local.room.entities.Product

object DbManager {
    val db = Room.databaseBuilder(
        applicationContext(),
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()
}

@Database(
    entities = [Product::class],
    version = AppDb.DATABASE_VERSION
)

abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun productsDao(): ProductsDao
}