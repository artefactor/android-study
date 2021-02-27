package com.example.lesson9.weather

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import com.example.lesson9.weather.presentation.FragmentCities
import com.example.lesson9.weather.presentation.FragmentWeather
import com.example.lesson9.weather.presentation.PROP_CITY_KEY
import com.example.lesson9.weather.presentation.WeatherFragmentManager
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), WeatherFragmentManager {
    private val fragmentWeather by lazy { FragmentWeather() }
    private val fragmentCities by lazy { FragmentCities() }
    private lateinit var sharedPreferences: SharedPreferences

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("checkedCity", Context.MODE_PRIVATE)
        loadFragment();
    }

    /*
            TODO, Денис.
            У меня приложение открывается почему-то медленно, а не сразу
            И при повороте экрана можно подгадать момент, когда будет оба фрагмента будут одновременно
            присутствовать на экране, накладывая текст друг на друга.
            Я не совсем понимаю, как эту логику правильней сделать.
            По идее приложение нужно сделать так, чтобы при отрытии или повороте
             - рисовались например серые квадраты (просто бэкграунд временно поставить)
            ,
            а потом - показывался текст (бекграунд возвращался на белый)
            Фрагменты при переключении создаются заново? onView отрабатывает у меня опять.
            И вся эта инициализация с БД и прочими UI - происходит каждый раз заново?
            Если я хочу чтобы UI нарисовался один раз, а потом - просто обновлялся,
            то мне нужно фрагмент городов делать не replace а show поверх?

            А если хочу чтобы сохранялись все UI модели, то их создавать вне фрагмента. в активити может?



            Какой-то неправильный код по навигации фрагментов.
            При повороте экрана плодятся запросы (в логе увеличивается количество каждый раз)
            В чем тут дело, чего не хватает?
     */

    private fun loadFragment() {
        val sharedPreferences = getSharedPreferences("checkedCity", Context.MODE_PRIVATE)
        val selectedId = sharedPreferences.getLong(PROP_CITY_KEY, -1)
        if (selectedId < 0) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragmentCities, "CITY")
                    .commit()

        } else {
            supportFragmentManager.beginTransaction()
                    .add<FragmentWeather>(R.id.fragmentContainer, "WEATHER")
                    .commit()
        }
    }

    override fun showWeatherFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentWeather, "WEATHER")
                .commit()
    }

    override fun showChangeCityFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentCities, "CITY")
                .commit()
    }

    override fun showError(error: String) {
        val baseContext: View = this.findViewById(R.id.fragmentContainer)
        snackbar = Snackbar.make(baseContext, error, Snackbar.LENGTH_INDEFINITE)
                .also { it.show() }
    }

    override fun hideError() {
        snackbar?.dismiss()
    }

    override fun getStoredCityId(): Long = sharedPreferences.getLong(PROP_CITY_KEY, 0)

    override fun storeCityId(itemId: Long) {
        sharedPreferences.edit().putLong(PROP_CITY_KEY, itemId).apply()
    }

}