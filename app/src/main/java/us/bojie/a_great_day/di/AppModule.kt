package us.bojie.a_great_day.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import us.bojie.a_great_day.data.TaskDatabase
import us.bojie.a_great_day.ui.MainActivity
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    private const val PREF_NAME = "a-great-day-prefs"

    @Provides
    @Singleton
    fun provideFirebaseCloudDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideGSON() = Gson()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideSharedPreference(app: Application): SharedPreferences =
        app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    @UserUID
    @Provides
    fun provideUserUID(pref: SharedPreferences): String =
        pref.getString(MainActivity.USER_UID, "-1") ?: "-1"
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class UserUID