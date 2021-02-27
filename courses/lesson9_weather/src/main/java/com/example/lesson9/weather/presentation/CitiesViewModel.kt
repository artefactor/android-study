package com.example.lesson9.weather.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.datasource.CityEntity
import com.example.lesson9.weather.domain.WeatherUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CitiesViewModel(
        private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
        private val mapper: (CityEntity) -> CityItem = CityItemMapper(),
        private val mapperBack: (CityItem) -> CityEntity = CityBackItemMapper(),
        private val weatherUseCase: WeatherUseCase,
) : ViewModel() {

    private val mutableCitiesListLiveData = MutableLiveData<List<CityItem>>()
    val citiesListLiveData: LiveData<List<CityItem>> = mutableCitiesListLiveData

    private val mutableCityLiveData = MutableLiveData<CityItem>()
    val cityLiveData: LiveData<CityItem> = mutableCityLiveData

    private val mutableErrorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = mutableErrorLiveData


    fun init(context: Context) {
        weatherUseCase.init(context)
    }

    fun fetchCity(cityId: Long) {
        Log.i("useCase", "fetchCity ${Thread.currentThread()}")
        weatherUseCase.getCity(cityId)
                .map { item -> mapper(item) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data ->
                            run {
                                Log.i("useCase", "fetchCityOnsuccess ${Thread.currentThread()}")
                                mutableCityLiveData.value = data
                            }
                        },
                        { error ->
                            Log.e(LOG_TAG, "exception during fetch data", error)
                            mutableErrorLiveData.value = (error.toString())
                        }
                ).also { compositeDisposable.add(it) }
    }

    fun updateCity(city: CityItem) {
        Log.i("useCase", "updateCity ${Thread.currentThread()}")
        weatherUseCase.updateCity(mapperBack.invoke(city))
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            run {
                                Log.i("useCase", "updateCitySuccess ${Thread.currentThread()}")
                                fetchCities()
                            }
                        },
                        { error ->
                            Log.e("useCase", "exception during update data", error)
                            mutableErrorLiveData.value = (error.toString())
                        }
                ).also { compositeDisposable.add(it) }
    }

    fun fetchCities() {
        Log.i("useCase", "fetchCities ${Thread.currentThread()}")
        weatherUseCase.getAllCities()
                .map { data -> data.map { item -> mapper(item) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data ->
                            run {
                                Log.i("useCase", "fetchCitiesOnsuccess ${Thread.currentThread()}")
                                mutableCitiesListLiveData.value = data
                            }
                        },
                        { error ->
                            Log.e(LOG_TAG, "exception during fetch data", error)
                            mutableErrorLiveData.value = (error.toString())
                        }
                ).also { compositeDisposable.add(it) }
    }

    fun addCity(city: String, country: String, lat: String, lon: String) {
        Log.i("useCase", "addCity $city " + Thread.currentThread())
        val addCity: Single<Long> = weatherUseCase.addCity(city, country, lat, lon)

        addCity
                /**
                 * TODO Денис,
                 * вот тут нужно при получения результата  - обновить список городов (в потоке ИО)
                 * правильно реализовал?
                 *
                 * Пришла такая мысль, может быть два сабскрайба сделать?
                 * один на ио - в котором вызовется fetchCities() (а в случае ошибки как тогда ошибку в майне показать?
                 * второй на main, в котором при ошибке отобразится ошибка в майне
                 *
                 * ИЛи мне майн здесь совсем не нужен, потому что здесь лайфдата обновляется,
                 * а она тоже обзервабле, и ее будут слушать в другом месте
                 *
                 * * В общем, я запутался здесь. помоги пожалуйтста.
                 */
//                .observeOn(AndroidSchedulers.mainThread())  //??
                .observeOn(Schedulers.io())  // ??
                .subscribe(
                        {
                            run {
                                Log.i("useCase", "addCitySuccess ${Thread.currentThread()}")
                                fetchCities()
                            }
                        },
                        { error ->
                            Log.e("useCase", "exception during fetch data", error)
                            mutableErrorLiveData.value = (error.toString())
                        }
                )
                .also { compositeDisposable.add(it) }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("useCase", "onCleared ${Thread.currentThread()}")
        compositeDisposable.clear()
    }
}