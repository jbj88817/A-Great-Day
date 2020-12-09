package us.bojie.a_great_day.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import us.bojie.a_great_day.ui.MainActivity
import java.text.SimpleDateFormat
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
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class UserUID

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class TodayDate