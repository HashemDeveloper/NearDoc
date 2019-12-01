package com.project.neardoc.utils

import com.project.neardoc.model.WeekDays
import com.project.neardoc.model.WeekDaysType
import java.util.*

class DayGetter constructor(private val calendar: Calendar) {
    internal fun getTodaysDay(): WeekDays {
        var days: WeekDays?= null
        if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            days = WeekDays(WeekDaysType.MONDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            days = WeekDays(WeekDaysType.TUESDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            days = WeekDays(WeekDaysType.WEDNESDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            days = WeekDays(WeekDaysType.THURSDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            days = WeekDays(WeekDaysType.FRIDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            days = WeekDays(WeekDaysType.SATURDAY)
        } else if (this.calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            days = WeekDays(WeekDaysType.SUNDAY)
        }
        return days!!
    }
}