package ru.mikov.storage.data.delegates

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PrefLiveDelegate<T>(
    private val fieldKey: String,
    private val defaultValue: T,
    private val preferences: SharedPreferences
) : ReadOnlyProperty<Any?, LiveData<T>> {
    private var storedValue: LiveData<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): LiveData<T> {
        if (storedValue == null) {
            storedValue = SharedPreferenceLiveData(preferences, fieldKey, defaultValue)
        }
        return storedValue!!
    }
}

internal class SharedPreferenceLiveData<T>(
    var sharedPrefs: SharedPreferences,
    var key: String,
    var defValue: T? = null,
) : LiveData<T>() {
    private val preferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
            if (shKey == key) {
                value = readValue(defValue)
            }
        }

    override fun onActive() {
        super.onActive()
        value = readValue(defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readValue(defaultValue: T?): T? = when (defValue) {
        is Boolean -> sharedPrefs.getBoolean(key, defaultValue as Boolean) as T
        is String -> sharedPrefs.getString(key, defaultValue as String) as T
        is Float -> sharedPrefs.getFloat(key, defaultValue as Float) as T
        is Int -> sharedPrefs.getInt(key, defaultValue as Int) as T
        is Long -> sharedPrefs.getLong(key, defaultValue as Long) as T
        else -> error("Value must be primitive type in Preferences")
    }
}