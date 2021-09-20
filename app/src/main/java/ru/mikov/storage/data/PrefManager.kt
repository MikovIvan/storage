package ru.mikov.storage.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.mikov.storage.App
import ru.mikov.storage.data.delegates.PrefLiveDelegate


object PrefManager {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    val currentDao: LiveData<String> by lazy {
        val dao by PrefLiveDelegate("select_db", "", preferences)
        dao
    }

    val currentOrder: LiveData<String> by lazy {
        val orderValue by PrefLiveDelegate("filter", "id", preferences)
        orderValue
    }

}


