package by.academy.questionnaire.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.academy.questionnaire.logic.BarChartModel
import io.reactivex.disposables.CompositeDisposable

class BarChartViewModel : ViewModel() {

    val chartMutableLiveData = MutableLiveData<BarChartModel>()
    val chartLiveData: LiveData<BarChartModel> = chartMutableLiveData


    override fun onCleared() {
        super.onCleared()
        Log.i("useCase", "onCleared ${Thread.currentThread()}")
    }
}