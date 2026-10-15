package com.example.minutemark.testutil

import android.content.SharedPreferences

class InMemorySharedPreferences : SharedPreferences {

    private val map = mutableMapOf<String, Any?>()
    private val listeners = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): MutableMap<String, *> = HashMap(map)

    override fun getString(key: String?, defValue: String?): String? =
        map[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
        map[key] as? MutableSet<String> ?: defValues

    override fun getInt(key: String?, defValue: Int): Int =
        (map[key] as? Int) ?: defValue

    override fun getLong(key: String?, defValue: Long): Long =
        (map[key] as? Long) ?: defValue

    override fun getFloat(key: String?, defValue: Float): Float =
        (map[key] as? Float) ?: defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean =
        (map[key] as? Boolean) ?: defValue

    override fun contains(key: String?): Boolean = map.containsKey(key)

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.let { listeners.add(it) }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listeners.remove(listener)
    }

    private inner class EditorImpl : SharedPreferences.Editor {
        private val tempMap = mutableMapOf<String, Any?>()
        private val removedKeys = mutableSetOf<String>()
        private var clearFlag = false

        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            if (key != null) tempMap[key] = value
            return this
        }

        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
            if (key != null) tempMap[key] = values
            return this
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            if (key != null) tempMap[key] = value
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            if (key != null) tempMap[key] = value
            return this
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            if (key != null) tempMap[key] = value
            return this
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            if (key != null) tempMap[key] = value
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            if (key != null) removedKeys.add(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            clearFlag = true
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            if (clearFlag) {
                map.clear()
            }
            removedKeys.forEach { map.remove(it) }
            map.putAll(tempMap)
        }
    }
}
