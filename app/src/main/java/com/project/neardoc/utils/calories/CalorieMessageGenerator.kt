package com.project.neardoc.utils.calories

import com.project.neardoc.model.WeekDays
import com.project.neardoc.model.WeekDaysType
import com.project.neardoc.utils.DayGetter
import java.util.*

class CalorieMessageGenerator {
    private val calendar: Calendar = Calendar.getInstance()
    private val date: Date = Date()
    private var dayGetter: DayGetter?= null
    init {
        this.calendar.time = date
        this.dayGetter = DayGetter(this.calendar)
    }

    fun getStringBasedOnWeekDays(): String {
        var todaysMessage = ""
        val today: WeekDays = this.dayGetter!!.getTodaysDay()
        when (today.weekDaysType) {
            WeekDaysType.MONDAY -> {
                todaysMessage = "Start of the week sweeter with burned calories! Check it out!"
            }
            WeekDaysType.TUESDAY -> {
                todaysMessage = "Happy Tuesday! Checkout how much calories you've burned!"
            }
            WeekDaysType.WEDNESDAY -> {
                todaysMessage = "Yea! You’ve made it half-way through the week! Now it's time to checkout your calories! don't you think?"
            }
            WeekDaysType.THURSDAY -> {
                todaysMessage = "Lovely Thursday! Wasn't it? Checkout your calories!"
            }
            WeekDaysType.FRIDAY -> {
                todaysMessage = "Finally the funday! Take a peek at your result for today!"
            }
            WeekDaysType.SATURDAY -> {
                todaysMessage = "Enjoying your weekend? Meanwhile, checkout how much calories you've burned!"
            }
            WeekDaysType.SUNDAY -> {
                todaysMessage = "Happy Sunday! Ready to see how much calories you've burned today?"
            }
        }
        return todaysMessage
    }
}