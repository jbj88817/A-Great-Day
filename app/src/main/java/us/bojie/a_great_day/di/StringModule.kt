package us.bojie.a_great_day.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.threeten.bp.LocalDate
import us.bojie.a_great_day.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class StringModule {

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
annotation class TodayDate

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class NextDate