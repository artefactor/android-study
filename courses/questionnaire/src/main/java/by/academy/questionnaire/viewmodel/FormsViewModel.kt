package by.academy.questionnaire.viewmodel

import androidx.lifecycle.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.domain.DbStat
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

const val LOG_TAG = "model"

class FormsViewModel(
        private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
        private val useCase: QUseCase,
) : ViewModel() {

    val formListMutableLiveData = MutableLiveData<List<FormQuestionStatus>>()
    val formListLiveData: LiveData<List<FormQuestionStatus>> = formListMutableLiveData

    val formMutableLiveData = MutableLiveData<List<AnswerQuestion>>()
    val formLiveData: LiveData<List<AnswerQuestion>> = formMutableLiveData

    val formResultsMutableLiveData = MutableLiveData<List<ResultUser>>()
    val formResultsLiveData: LiveData<List<ResultUser>> = formResultsMutableLiveData

    val formComparingMutableLiveData = MutableLiveData<List<Pair<AnswerQuestion, AnswerQuestion>>>()
    val formComparingLiveData: LiveData<List<Pair<AnswerQuestion, AnswerQuestion>>> = formComparingMutableLiveData

    private val infoMutableLiveData = MutableLiveData<DbStat>()
    val infoLiveData: LiveData<DbStat> = infoMutableLiveData

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

    override fun onCleared() {
        super.onCleared()
        Log.i("useCase", "onCleared ${Thread.currentThread()}")
        compositeDisposable.clear()
    }

}