package us.bojie.a_great_day.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.di.TodayDate
import us.bojie.a_great_day.di.UserUID
import us.bojie.a_great_day.ui.time_task.TimeTaskViewModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseManager @Inject constructor(
    private val firebaseDB: FirebaseFirestore,
    private val gson: Gson,
    @UserUID private val userUID: String,
    @TodayDate private val formattedDate: String
) {

    suspend fun updateTask(task: Task): Boolean {
        return suspendCancellableCoroutine { continuation ->
            firebaseDB.collection("tasks").document("${userUID}/${formattedDate}/${task.name}")
                .set(task)
                .addOnSuccessListener {
                    continuation.resume(true, null)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun deleteTask(task: Task): Boolean {
        return suspendCancellableCoroutine { continuation ->
            firebaseDB.collection("tasks").document("${userUID}/${formattedDate}/${task.name}")
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
        return suspendCancellableCoroutine { continuation ->
            val tasks = mutableListOf<Task>()
            firebaseDB.collection("tasks/${userUID}/${formattedDate}").get()
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