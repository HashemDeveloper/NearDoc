package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Insurance
import java.lang.reflect.Type

class InsuranceListTypeConverter {
    @TypeConverter
    fun stringToInsuranceList(json: String): List<Insurance> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Insurance>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun insuranceListToString(list: List<Insurance>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Insurance>>(){}.type
        return gson.toJson(list, type)
    }
}