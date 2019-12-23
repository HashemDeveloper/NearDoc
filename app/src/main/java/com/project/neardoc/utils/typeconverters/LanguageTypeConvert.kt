package com.project.neardoc.utils.typeconverters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.neardoc.model.Language
import java.lang.reflect.Type

class LanguageTypeConvert {
    @TypeConverter
    fun stringToLanguage(json: String): List<Language> {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Language>>(){}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun languageToString(languageList: List<Language>): String {
        val gson: Gson = Gson()
        val type: Type = object : TypeToken<List<Language>>(){}.type
        return gson.toJson(languageList, type)
    }
}