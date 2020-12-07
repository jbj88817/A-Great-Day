package us.bojie.a_great_day.ui.time_task

import android.os.CountDownTimer
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import us.bojie.a_great_day.data.Task
import java.text.SimpleDateFormat
import java.time.*
import java.util.*


class TimeTaskViewModel @ViewModelInject constructor(
    private val firebaseDB: FirebaseFirestore,
    private val gson: Gson
) : ViewModel() {

    private val _countDownLiveData = MutableLiveData<String>()
    val countDownLiveData: LiveData<String> = _countDownLiveData

    private val _todayTasksLiveData = MutableLiveData<List<Task>>()
    val todayTasksLiveData: LiveData<List<Task>> = _todayTasksLiveData


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

    fun addFireBaseTestData() {
        val task = Task("test1", "2h")
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedDate: String = df.format(Date())

        firebaseDB.collection("tasks").document("jbj88817/${formattedDate}/${task.name}")
            .set(task)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->
            }
    }

    fun refreshFirebaseData() {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedDate: String = df.format(Date())
        val tasks = mutableListOf<Task>()
        firebaseDB.collection("tasks/jbj88817/${formattedDate}").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val jsonElement = gson.toJsonTree(document.data)
                    tasks.add(gson.fromJson(jsonElement, Task::class.java))
                }
                _todayTasksLiveData.value = tasks
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun getEndOfDayInMillis(): Long {
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(
            ZonedDateTime.now(
                ZoneId.systemDefault()
            ).offset
        ).toEpochMilli() -
                System.currentTimeMillis()
    }

    companion object {
        const val TAG = "TimeTaskViewModel"
    }
}