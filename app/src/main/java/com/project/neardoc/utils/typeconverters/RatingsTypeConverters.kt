package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Rating
import java.lang.reflect.Type

class RatingsTypeConverters {
    @TypeConverter
    fun stringToRatings(json: String): List<Rating> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Rating>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun ratingsToString(ratingList: List<Rating>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Rating>>(){}.type
        return gson.toJson(ratingList, type)
    }
}