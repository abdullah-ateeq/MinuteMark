package com.example.minutemark.data.repository

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalTime

class LocalTimeTypeAdapter : TypeAdapter<LocalTime>() {
    override fun write(out: JsonWriter, value: LocalTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): LocalTime? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val str = reader.nextString()
        return try {
            LocalTime.parse(str)
        } catch (e: Exception) {
            null
        }
    }
}
