package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StringTypeConverter {
    @TypeConverter
    fun stringToElement(json: String): List<String> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<String>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun elementToString(list: List<String>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<String>>(){}.type
        return gson.toJson(list, type)
    }
}