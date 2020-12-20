package us.bojie.a_great_day.util

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


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