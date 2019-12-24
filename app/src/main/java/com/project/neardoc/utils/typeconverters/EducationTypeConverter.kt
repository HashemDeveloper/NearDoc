package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Education
import java.lang.reflect.Type

class EducationTypeConverter {
    @TypeConverter
    fun stringToEducation(json: String): List<Education> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Education>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun educationToString(educationList: List<Education>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Education>>(){}.type
        return gson.toJson(educationList, type)
    }
}