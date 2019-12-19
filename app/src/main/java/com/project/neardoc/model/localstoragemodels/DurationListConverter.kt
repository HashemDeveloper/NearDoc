package com.project.neardoc.model.localstoragemodels

import androidx.room.TypeConverter

class DurationListConverter {
    @TypeConverter
    fun floatToDurationList(duration: Float): DurationList {
        val durationList: List<Float> = arrayListOf(duration)
        return DurationList(durationList)
    }
    @TypeConverter
    fun durationListToFloat(durationList: DurationList): Float {
        var value: Float? = 0.0f
        for (duration in durationList.list) {
            value = duration
        }
        return value!!
    }
}