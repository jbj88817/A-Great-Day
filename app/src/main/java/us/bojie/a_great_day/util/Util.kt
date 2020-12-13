package us.bojie.a_great_day.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object Util {

    fun getEndOfDayInMillis(): Long {
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(
            ZonedDateTime.now(
                ZoneId.systemDefault()
            ).offset
        ).toEpochMilli() -
                System.currentTimeMillis()
    }
}