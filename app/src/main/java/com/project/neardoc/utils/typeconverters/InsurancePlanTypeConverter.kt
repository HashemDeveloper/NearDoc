package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.InsurancePlan
import java.lang.reflect.Type

class InsurancePlanTypeConverter {
    @TypeConverter
    fun stringToInsurancePlan(json: String): InsurancePlan{
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<InsurancePlan>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun insurancePlanToString(insurancePlan: InsurancePlan): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<InsurancePlan>(){}.type
        return gson.toJson(insurancePlan, type)
    }
}