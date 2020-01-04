package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Claim
import com.project.neardoc.model.StreetAddress
import java.lang.reflect.Type

class StreetAddressTypeConverter {
    @TypeConverter
    fun stringToStreetAddress(json: String): StreetAddress {
        val gson = Gson()
        val type: Type = object : TypeToken<StreetAddress>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun streetAddressToString(streetAddress: StreetAddress): String {
        val gson = Gson()
        val type: Type = object : TypeToken<StreetAddress>(){}.type
        return gson.toJson(streetAddress, type)
    }
}