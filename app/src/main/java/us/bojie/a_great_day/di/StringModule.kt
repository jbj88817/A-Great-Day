package us.bojie.a_great_day.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import us.bojie.a_great_day.ui.MainActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Qualifier

@Module
@InstallIn(ApplicationComponent::class)
class StringModule {

    @UserUID
    @Provides
    fun provideUserUID(pref: SharedPreferences): String =
        pref.getString(MainActivity.USER_UID, "-1") ?: "-1"

    @TodayDate
    @Provides
    fun provideTodayFormattedDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    @NextDate
    @Provides
    fun provideNextDayFormattedDate(@TodayDate todayDate: String): String =
        LocalDate.parse(todayDate).plusDays(1).toString()
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class UserUID

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class TodayDate

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class NextDate