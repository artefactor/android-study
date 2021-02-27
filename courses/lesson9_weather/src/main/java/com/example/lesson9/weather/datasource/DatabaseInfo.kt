package com.example.lesson9.weather.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val TAG_DATABASE = "TAG_DATABASE"

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
internal abstract class DatabaseInfo : RoomDatabase() {

    abstract fun getCityInfoDAO(): CityInfoDAO

    /* TODO Denis,
       этот код должен один раз отрабатывать или для каждого фрагмента свой?
       У меня получается, что он несколько раз срабатывает при старте приложения и переключения между экранами
       Получается, что вызывается много раз. Я думаю, что это неправильно, ведь у нас одна БД на приложение
       Подскажи пожалуйста, как нужно делать

     */
    companion object {
        fun init(context: Context) =
                lazy {
                    Log.i(TAG_DATABASE, "init database")
                    Room.databaseBuilder(context, DatabaseInfo::class.java, "database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
    }

}