package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Media
import java.lang.reflect.Type

class MediaTypeConveter {
    @TypeConverter
    fun stringToMedia(json: String): List<Media> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Media>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun mediaToString(list: List<Media>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Media>>(){}.type
        return gson.toJson(list, type)
    }
}