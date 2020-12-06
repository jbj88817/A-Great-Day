package us.bojie.a_great_day.ui.time_task

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.*
import java.util.*

class TimeTaskViewModel @ViewModelInject constructor() : ViewModel() {

    private val _countDownLiveData = MutableLiveData<String>()
    val countDownLiveData: LiveData<String> = _countDownLiveData


    fun startTimer() {
        val millisToGo = getEndOfDayInMillis()

        object : CountDownTimer(millisToGo, 1000) {
            override fun onTick(millis: Long) {
                val seconds = (millis / 1000).toInt() % 60
                val minutes = (millis / (1000 * 60) % 60).toInt()
                val hours = (millis / (1000 * 60 * 60) % 24).toInt()
                val text =
                    String.format("%02d hours, %02d minutes, %02d seconds", hours, minutes, seconds)
                _countDownLiveData.value = text
            }

            override fun onFinish() {}
        }.start()
    }

    private fun getEndOfDayInMillis(): Long {
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(
            ZonedDateTime.now(
                ZoneId.systemDefault()
            ).offset
        ).toEpochMilli() -
                System.currentTimeMillis()
    }
}