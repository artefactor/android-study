package com.example.lesson9.weather.datasource.network

import com.example.lesson9.weather.datasource.WeatherDataSource
import com.example.lesson9.weather.datasource.WeatherRawDataOneCallRoot
import com.example.lesson9.weather.datasource.WeatherRawDataRoot
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherMapApiController : WeatherDataSource {

    // TODO Denis, а зачем в твоем примере нужен был объект RetrofitHolder, если можно так?
    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                /* TODO Денис, вопрос: есть еще метод createAsync(). Мы же асинхронные запросы используем.
                 Немного запутался.
                    createAsync() или create()?
                 */
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }


    override fun getCurrentWeatherData(city: String, lang: String): Single<WeatherRawDataRoot> =
            retrofit.create(OpenWeatherMapApi::class.java)
                    .getCurrentWeatherData(city, lang = lang)
                    .subscribeOn(Schedulers.io())

    override fun getHourlyForecast2days(lat: String, lon: String, lang: String): Single<WeatherRawDataOneCallRoot> =
            retrofit.create(OpenWeatherMapApi::class.java)
                    .getHourlyForecast2days(lat, lon, lang = lang)
                    .subscribeOn(Schedulers.io())

}
