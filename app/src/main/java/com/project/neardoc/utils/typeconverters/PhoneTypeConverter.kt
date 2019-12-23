package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Phone
import java.lang.reflect.Type

class PhoneTypeConverter {
    @TypeConverter
    fun stringToPhone(json: String): List<Phone> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Phone>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun phoneToString(list: List<Phone>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Phone>>(){}.type
        return gson.toJson(list, type)
    }
}