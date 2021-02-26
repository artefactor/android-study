package com.example.lesson9.weather.datasource.network


import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"
const val OPEN_WEATHER_IMAGE_URL = "https://openweathermap.org/img/w/"

/*TODO Денис, я убрал эти 2,5 в какую-то отдельную переменную.
 Норм? Или на уровне ретривера это можно сделать?
 Или какой-нибудь аннотацией над всем интерфейсом?
 */
const val VERSION = "/data/2.5"
const val API_KEY = "d26df6d433a1241d696e6610904a86b9"

/**
 *  @see 'https://openweathermap.org/'
 */
interface OpenWeatherMapApi {

    /*
    Я понимаю, что можно было реализовать одним запросом
    (openWeather дает одним ответом все что нужно для этого задания - сегодня и несколько дней вперед)
     хотел немного усложинить логику и посмотреть что изменится в коде, если будет  несколько запросов)
     */
    /**
     * api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
     *
     * @see 'https://openweathermap.org/current'
     */
    @GET("$VERSION/weather")
    fun getCurrentWeatherDataCall(
            @Query("q") city: String,
            @Query("appid") apiKey: String = API_KEY,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "ru",
    ): Call<WeatherRawDataRoot>


    /* TODO  я как-то могу сделать чтобы была возможность и Call и Single возвращать в одном апи?
       или дублировать методы нужно?
       или отдельный интерфейс для этого?
    */
    @GET("$VERSION/weather")
    fun getCurrentWeatherData(
            @Query("q") city: String,
            @Query("appid") apiKey: String = API_KEY,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "ru",
    ): Single<WeatherRawDataRoot>


    /**
     *  @see 'https://openweathermap.org/api/one-call-api'
     */
    @GET("$VERSION/onecall")
    fun getHourlyForecast2daysCall(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String = API_KEY,
            @Query("exclude") exclude: String = "hourly,minutely,alerts",
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "ru",
    ): Call<WeatherRawDataRoot>

    @GET("$VERSION/onecall")
    fun getHourlyForecast2days(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String = API_KEY,
            @Query("exclude") exclude: String = "hourly,minutely, alerts",
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "ru",
    ): Single<WeatherRawDataOneCallRoot>


    // test url
    private fun testUrl(city: String, apiKey: String) =
            "api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
}