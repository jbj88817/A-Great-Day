package us.bojie.a_great_day.app_widget

import android.os.CountDownTimer


open class CountDown(
    millisInFuture: Long,
    countDownInterval: Int,
    val callback: (String) -> Unit
) : CountDownTimer(millisInFuture, countDownInterval.toLong()) {
    override fun onFinish() {}
    override fun onTick(millisUntilFinished: Long) {
        val seconds = (millisUntilFinished / 1000).toInt() % 60
        val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()
        val hours = (millisUntilFinished / (1000 * 60 * 60) % 24).toInt()
        val text =
            String.format("%02d hours, %02d minutes, %02d seconds", hours, minutes, seconds)
        callback(text)
    }
}