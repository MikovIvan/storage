package ru.mikov.storage.data.local.sql

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mikov.storage.data.local.room.AppDb.Companion.DATABASE_NAME
import ru.mikov.storage.data.local.room.AppDb.Companion.DATABASE_VERSION
import ru.mikov.storage.data.local.room.dao.ProductsDao
import ru.mikov.storage.data.local.room.entities.Product


internal const val TABLE_NAME = "product"
private const val CODE_COLUMN = "code"
private const val ID_COLUMN = "id"
private const val TITLE_COLUMN = "title"
private const val AMOUNT_COLUMN = "amount"
private const val CREATE_TABLE_SQL =
    "CREATE TABLE IF NOT EXISTS $TABLE_NAME (_$ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT, $TITLE_COLUMN VARCHAR(50), $CODE_COLUMN INTEGER, $AMOUNT_COLUMN INTEGER);"

class ProductsSQLiteOpenHelper(context: Context) : ProductsDao, SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    private val list = MutableStateFlow(emptyList<Product>())

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_TABLE_SQL)
        } catch (exception: SQLException) {
            Log.e("LOG_TAG", "Exception while trying to create database", exception)
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    private fun getAllProductsOrderBy(query: SupportSQLiteQuery): Cursor {
        return readableDatabase.rawQuery(query.sql, null)
    }

    override suspend fun add(product: Product) {
        readableDatabase.execSQL("INSERT INTO $TABLE_NAME ($TITLE_COLUMN, $AMOUNT_COLUMN, $CODE_COLUMN) VALUES ('${product.title}', '${product.amount}', '${product.code}')")
    }

    override suspend fun delete(product: Product) {
        readableDatabase.execSQL("DELETE FROM $TABLE_NAME WHERE $ID_COLUMN = '${product.id}'")
    }

    override suspend fun update(product: Product) {
        writableDatabase.execSQL(
            "UPDATE $TABLE_NAME " +
                    "SET $TITLE_COLUMN = '${product.title}', " +
                    "$AMOUNT_COLUMN = '${product.amount}', " +
                    "$CODE_COLUMN = '${product.code}' WHERE $ID_COLUMN = '${product.id}' "
        )
    }

    override fun getProducts(query: SimpleSQLiteQuery): Flow<List<Product>> {
        val listOfTopics = mutableListOf<Product>()
        getAllProductsOrderBy(query).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN))
                    val title = cursor.getString(cursor.getColumnIndex(TITLE_COLUMN))
                    val amount = cursor.getInt(cursor.getColumnIndex(AMOUNT_COLUMN))
                    val code = cursor.getInt(cursor.getColumnIndex(CODE_COLUMN))
                    listOfTopics.add(Product(id = id, title = title, amount = amount, code = code))
                } while (cursor.moveToNext())
            }
        }
        list.value = listOfTopics
        return list
    }
}