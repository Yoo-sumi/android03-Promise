package com.boosters.promise

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class UserTypeConverter(private val gson: Gson) {

    @TypeConverter
    fun listToJson(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        return gson.fromJson(value, Array<String>::class.java)?.toList()
    }

}