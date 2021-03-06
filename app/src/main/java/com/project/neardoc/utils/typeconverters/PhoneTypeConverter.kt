package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Phone
import java.lang.reflect.Type

class PhoneTypeConverter {
    @TypeConverter
    fun stringToPhone(json: String): Phone {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<Phone>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun phoneToString(phone: Phone): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<Phone>(){}.type
        return gson.toJson(phone, type)
    }
}