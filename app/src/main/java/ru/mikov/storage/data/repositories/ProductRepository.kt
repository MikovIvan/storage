package ru.mikov.storage.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.mikov.storage.App
import ru.mikov.storage.data.PrefManager
import ru.mikov.storage.data.local.room.DbManager.db
import ru.mikov.storage.data.local.room.dao.ProductsDao
import ru.mikov.storage.data.local.room.entities.Product
import ru.mikov.storage.data.local.sql.ProductsSQLiteOpenHelper
import ru.mikov.storage.data.local.sql.TABLE_NAME


object ProductRepository {

    private val preferences = PrefManager
    private val roomDao: ProductsDao = db.productsDao()
    private val sqlDao: ProductsDao = ProductsSQLiteOpenHelper(App.applicationContext())

    private fun currentDao(): LiveData<String> = preferences.currentDao
    private fun currentOrder(): LiveData<String> = preferences.currentOrder

    private val daoFlow = MutableSharedFlow<ProductsDao>(replay = 1).shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(1000L),
        replay = 1
    ).onSubscription {
        currentDao().asFlow().collect {
            emit(
                when (it) {
                    "room" -> roomDao
                    "sql" -> sqlDao
                    else -> roomDao
                }
            )
            Log.d("TAG", "collect: $it")
        }
    }

    private val filterFlow = MutableSharedFlow<String>(replay = 1).shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(1000L),
        replay = 1
    ).onSubscription {
        currentOrder().asFlow().collect {
            emit(
                it
            )
        }
    }

    private val dao
        get() = when (currentDao().value) {
            "room" -> roomDao
            "sql" -> sqlDao
            else -> roomDao
        }

    val data =
        combine(daoFlow, filterFlow) { dao, orderValue ->
            getOrProd(dao, orderValue)
        }.flatMapMerge { it }

    private fun getOrProd(dao: ProductsDao, orderValue: String): Flow<List<Product>> {
        return dao.getProducts(SimpleSQLiteQuery(toQuery(orderValue)))
    }

    suspend fun saveProduct(product: Product) = dao.add(product)

    suspend fun delete(product: Product) {
        dao.delete(product)
        if (dao is ProductsSQLiteOpenHelper) {
            (dao as ProductsSQLiteOpenHelper).getProducts(SimpleSQLiteQuery(toQuery(currentOrder().value.toString())))
        }
    }

    suspend fun update(product: Product) = dao.update(product)

}


fun toQuery(orderValue: String): String {
    val qb = QueryBuilder()
    qb.table(TABLE_NAME)

    qb.orderBy(orderValue, false)
    return qb.build()
}


class QueryBuilder {
    private var table: String? = null
    private var selectColumns: String = "*"
    private var order: String? = null

    fun build(): String {
        check(table != null) { "table must be not null" }
        val strBuilder = StringBuilder("SELECT ")
            .append("$selectColumns ")
            .append("FROM $table ")
        if (order != null) strBuilder.append(order)
        return strBuilder.toString()
    }

    fun table(table: String): QueryBuilder {
        this.table = table
        return this
    }

    fun orderBy(column: String, isDesc: Boolean = true): QueryBuilder {
        order = "ORDER BY $column ${if (isDesc) "DESC" else "ASC"}"
        return this
    }

}
