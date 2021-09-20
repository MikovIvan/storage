package ru.mikov.storage.data.local.room.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import ru.mikov.storage.data.local.room.entities.Product

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Update
    suspend fun update(product: Product)

    @RawQuery(observedEntities = [Product::class])
    fun getProducts(query: SimpleSQLiteQuery): Flow<List<Product>>

}