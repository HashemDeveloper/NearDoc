package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.InsuranceProvider
import java.lang.reflect.Type

class InsuranceProviderTypeConverter {
    @TypeConverter
    fun stringToInsuranceProvider(json: String): InsuranceProvider {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<InsuranceProvider>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun insuranceProvderToString(insuranceProvider: InsuranceProvider): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<InsuranceProvider>(){}.type
        return gson.toJson(insuranceProvider, type)
    }
}