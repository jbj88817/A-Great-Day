package us.bojie.a_great_day.data.firebase

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.di.NextDate
import us.bojie.a_great_day.di.TodayDate
import us.bojie.a_great_day.ui.MainActivity.Companion.USER_UID
import us.bojie.a_great_day.ui.time_task.TimeTaskViewModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseManager @Inject constructor(
    private val firebaseDB: FirebaseFirestore,
    private val gson: Gson,
    private val pref: SharedPreferences,
    @TodayDate private val formattedToday: String,
    @NextDate private val formattedNextDay: String
) {

    suspend fun updateTask(
        task: Task,
        formattedDate: String = formattedToday,
        oldTaskName: String? = null,
    ): Boolean {
        val userUID = pref.getString(USER_UID, "-1") ?: "-1"
        oldTaskName?.also { deleteTaskName(oldTaskName) }
        return suspendCancellableCoroutine { continuation ->
            firebaseDB.collection("tasks")
                .document("${userUID}/${formattedDate}/${task.name.hashCode()}")
                .set(task)
                .addOnSuccessListener {
                    continuation.resume(true, null)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun updateTaskToNextDay(task: Task, needDelete: Boolean = true): Boolean {
        if (needDelete) {
            deleteTask(task)
        }
        return updateTask(task, formattedNextDay)
    }

    suspend fun deleteTask(task: Task): Boolean {
        val userUID = pref.getString(USER_UID, "-1") ?: "-1"
        return suspendCancellableCoroutine { continuation ->
            firebaseDB.collection("tasks")
                .document("${userUID}/${formattedToday}/${task.name.hashCode()}")
                .delete()
                .addOnSuccessListener {
                    continuation.resume(true, null)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    private suspend fun deleteTaskName(taskName: String): Boolean {
        val userUID = pref.getString(USER_UID, "-1") ?: "-1"
        return suspendCancellableCoroutine { continuation ->
            firebaseDB.collection("tasks")
                .document("${userUID}/${formattedToday}/${taskName.hashCode()}")
                .delete()
                .addOnSuccessListener {
                    continuation.resume(true, null)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun getTodayTasks(): List<Task> {
        val userUID = pref.getString(USER_UID, "-1") ?: "-1"
        return suspendCancellableCoroutine { continuation ->
            val tasks = mutableListOf<Task>()
            firebaseDB.collection("tasks/${userUID}/${formattedToday}").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TimeTaskViewModel.TAG, "${document.id} => ${document.data}")
                        val jsonElement = gson.toJsonTree(document.data)
                        tasks.add(gson.fromJson(jsonElement, Task::class.java))
                    }
                    continuation.resume(tasks, null)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}