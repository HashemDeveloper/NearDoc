package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Insurance
import java.lang.reflect.Type

class InsuranceTypeConverters {
    @TypeConverter
    fun stringToInsurance(json: String): List<Insurance> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Insurance>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun insuranceToString(insuranceList: List<Insurance>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Insurance>>(){}.type
        return gson.toJson(insuranceList, type)
    }
}