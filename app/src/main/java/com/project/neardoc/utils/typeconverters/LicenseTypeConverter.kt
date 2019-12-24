package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.License
import java.lang.reflect.Type

class LicenseTypeConverter {
    @TypeConverter
    fun stringToLicence(json: String): List<License> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<License>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun licenseToString(insuranceList: List<License>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<License>>(){}.type
        return gson.toJson(insuranceList, type)
    }
}