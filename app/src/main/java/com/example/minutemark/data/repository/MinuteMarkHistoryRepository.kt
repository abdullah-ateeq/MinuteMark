package com.example.minutemark.data.repository

import android.content.SharedPreferences
import com.example.minutemark.data.model.MinuteMarkHistory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalTime

class MinuteMarkHistoryRepository(
    private val preferences: SharedPreferences
) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .create()

    companion object {
        const val KEY_HISTORY = "minute_mark_history_list"
        const val MAX_HISTORY_ITEMS = 10
    }

    fun getHistory(): List<MinuteMarkHistory> {
        val json = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MinuteMarkHistory>>() {}.type
            gson.fromJson<List<MinuteMarkHistory>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addHistory(item: MinuteMarkHistory): List<MinuteMarkHistory> {
        val current = getHistory().toMutableList()
        // Deduplication: check if the topmost item matches
        if (current.isNotEmpty()) {
            val latest = current.first()
            if (latest.direction == item.direction &&
                latest.time == item.time &&
                latest.value == item.value &&
                latest.unit == item.unit
            ) {
                return current
            }
        }
        current.add(0, item)
        val trimmed = if (current.size > MAX_HISTORY_ITEMS) current.take(MAX_HISTORY_ITEMS) else current
        preferences.edit().putString(KEY_HISTORY, gson.toJson(trimmed)).apply()
        return trimmed
    }

    fun deleteHistoryItem(id: String): List<MinuteMarkHistory> {
        val updated = getHistory().filterNot { it.id == id }
        preferences.edit().putString(KEY_HISTORY, gson.toJson(updated)).apply()
        return updated
    }

    fun clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }
}
