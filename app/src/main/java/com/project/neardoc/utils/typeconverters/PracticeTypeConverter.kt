package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Practice
import java.lang.reflect.Type

class PracticeTypeConverter {
    @TypeConverter
    fun stringToPractice(json: String): List<Practice> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Practice>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun practiceToString(list: List<Practice>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Practice>>(){}.type
        return gson.toJson(list, type)
    }
}