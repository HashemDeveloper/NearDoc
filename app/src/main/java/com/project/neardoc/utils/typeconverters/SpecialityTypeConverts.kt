package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Specialty
import java.lang.reflect.Type

class SpecialityTypeConverts {
    @TypeConverter
    fun stringToSpeciality(json: String): List<Specialty> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Specialty>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun specialityToString(specialityList: List<Specialty>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Specialty>>(){}.type
        return gson.toJson(specialityList, type)
    }
}