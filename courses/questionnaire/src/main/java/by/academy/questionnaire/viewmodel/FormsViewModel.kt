package by.academy.questionnaire.viewmodel

import androidx.lifecycle.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

const val LOG_TAG = "model"

class FormsViewModel(
        private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
        // example
//        private val mapper: (CityEntity) -> CityItem = CityItemMapper(),
//        private val mapperBack: (CityItem) -> CityEntity = CityBackItemMapper(),
        private val useCase: QUseCase,
) : ViewModel() {
// example
//    private val mutableCityLiveData = MutableLiveData<CityItem>()
//    val cityLiveData: LiveData<CityItem> = mutableCityLiveData

    val formListMutableLiveData = MutableLiveData<List<FormQuestionStatus>>()
    val formListLiveData: LiveData<List<FormQuestionStatus>> = formListMutableLiveData

    val formMutableLiveData = MutableLiveData<List<AnswerQuestion>>()
    val formLiveData: LiveData<List<AnswerQuestion>> = formMutableLiveData

    val formResultsMutableLiveData = MutableLiveData<List<ResultUser>>()
    val formResultsLiveData: LiveData<List<ResultUser>> = formResultsMutableLiveData

    val formComparingMutableLiveData = MutableLiveData<List<Pair<AnswerQuestion, AnswerQuestion>>>()
    val formComparingLiveData: LiveData<List<Pair<AnswerQuestion, AnswerQuestion>>> = formComparingMutableLiveData

    private val infoMutableLiveData = MutableLiveData<String>()
    val infoLiveData: LiveData<String> = infoMutableLiveData

    private val mutableErrorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = mutableErrorLiveData

    // example
    fun onDbInfo0() {
        useCase.getAppStatistics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    infoMutableLiveData.value = data
                }, { error ->
                    Log.e(LOG_TAG, "exception during fetch data", error)
                    mutableErrorLiveData.value = (error.toString())
                }).also { compositeDisposable.add(it) }
    }

    private fun <T> fetchData(f: Single<T>, mutableData: MutableLiveData<T>) {
        f.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data -> mutableData.value = data }, { error ->
                    Log.e(LOG_TAG, "exception during fetch data", error)
                    mutableErrorLiveData.value = (error.toString())
                }).also { compositeDisposable.add(it) }
    }

    fun onDbInfo() = fetchData(useCase.getAppStatistics(), infoMutableLiveData)
    fun fetchForms() = fetchData(useCase.getAllFormsInfo(), formListMutableLiveData)
    fun fetchForm(furContext: FURContext) = fetchData(useCase.getAttemptAnswers(furContext), formMutableLiveData)
    fun fetchFormResult(formId: Long) = fetchData(useCase.getResults(formId), formResultsMutableLiveData)
    fun fetchFormsComparing(furContext1: FURContext, furContext2: FURContext) {
        fetchData(useCase.getAttemptAnswers(furContext1, furContext2), formComparingMutableLiveData)
    }

    fun deleteAttempt(formId: Long, resultId: Long) {
        useCase.deleteAttempt(resultId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fetchFormResult(formId) }, { error ->
                    Log.e(LOG_TAG, "exception during fetch data", error)
                    mutableErrorLiveData.value = (error.toString())
                }).also { compositeDisposable.add(it) }
    }

// example
//    fun updateCity(city: CityItem) {
//        Log.i("useCase", "updateCity ${Thread.currentThread()}")
//        useCase.updateCity(mapperBack.invoke(city))
//                .observeOn(Schedulers.io())
//                .subscribe(
//                        {
//                            run {
//                                Log.i("useCase", "updateCitySuccess ${Thread.currentThread()}")
//                                fetchCities()
//                            }
//                        },
//                        { error ->
//                            Log.e("useCase", "exception during update data", error)
//                            mutableErrorLiveData.value = (error.toString())
//                        }
//                ).also { compositeDisposable.add(it) }
//    }
//
//    fun fetchCities() {
//        Log.i("useCase", "fetchCities ${Thread.currentThread()}")
//        useCase.getAllCities()
//                .map { data -> data.map { item -> mapper(item) } }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { data ->
//                            run {
//                                Log.i("useCase", "fetchCitiesOnsuccess ${Thread.currentThread()}")
//                                formListMutableLiveData.value = data
//                            }
//                        },
//                        { error ->
//                            Log.e(LOG_TAG, "exception during fetch data", error)
//                            mutableErrorLiveData.value = (error.toString())
//                        }
//                ).also { compositeDisposable.add(it) }
//    }
//
//    fun addCity(city: String, country: String, lat: String, lon: String) {
//        Log.i("useCase", "addCity $city " + Thread.currentThread())
//        val addCity: Single<Long> = useCase.addCity(city, country, lat, lon)
//
//        addCity
//                .observeOn(Schedulers.io())
//                .subscribe(
//                        {
//                            run {
//                                Log.i("useCase", "addCitySuccess ${Thread.currentThread()}")
//                                fetchCities()
//                            }
//                        },
//                        { error ->
//                            Log.e("useCase", "exception during fetch data", error)
//                            mutableErrorLiveData.value = (error.toString())
//                        }
//                )
//                .also { compositeDisposable.add(it) }
//    }
//

    override fun onCleared() {
        super.onCleared()
        Log.i("useCase", "onCleared ${Thread.currentThread()}")
        compositeDisposable.clear()
    }

}