package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Claim
import java.lang.reflect.Type

class ClaimTypeConverter {
    @TypeConverter
    fun stringToClaim(json: String): List<Claim> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Claim>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun claimToString(list: List<Claim>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Claim>>(){}.type
        return gson.toJson(list, type)
    }
}