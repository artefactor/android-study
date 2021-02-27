package com.example.lesson9.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.lesson9.weather.datasource.network.API_KEY
import com.example.lesson9.weather.datasource.network.OpenWeatherMapApi
import com.example.lesson9.weather.datasource.WeatherRawDataRoot

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val LOG_TAG = "WEATHER_TAG"

class SimpleExampleActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_activity_main)
    }


    override fun onResume() {
        super.onResume()
        val currentWeatherData = retrofit.create(OpenWeatherMapApi::class.java)
                .getCurrentWeatherDataCall("Minsk", API_KEY)
        /*  TODO DENIS, когда  возвращает просто Call, а не  Single -
              можно было посмотреть урл, что он генировал.
         А как сделать когда Single?*/
        Log.d(LOG_TAG, currentWeatherData.request().url().toString())

        currentWeatherData.enqueue(object : Callback<WeatherRawDataRoot> {
            override fun onResponse(call: Call<WeatherRawDataRoot>, response: Response<WeatherRawDataRoot>) {
                val view = findViewById<TextView>(R.id.main)
                val body = response.body()
                if (response.isSuccessful) {
                    showResponse(body, view)
                } else {
                    view.text = response.code().toString()
                }
                Log.d(LOG_TAG, body.toString())
                Log.d(LOG_TAG, Thread.currentThread().name)
            }

            override fun onFailure(call: Call<WeatherRawDataRoot>, t: Throwable) {
                Log.d(LOG_TAG, t.toString())
                Log.d(LOG_TAG, Thread.currentThread().name)
                val view = findViewById<TextView>(R.id.main)
                view.text = t.message
            }
        })
    }

    private fun showResponse(body: WeatherRawDataRoot?, view: TextView) {
        view.text = " ${body?.city}  ${body?.city} ${body?.city}"
    }


}