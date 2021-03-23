package by.academy.questionnaire.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.academy.questionnaire.domain.QUseCase
import io.reactivex.disposables.CompositeDisposable

class MyViewModelFactory(val usecase: QUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormsViewModel::class.java)) {
            return FormsViewModel(
                    compositeDisposable = CompositeDisposable(),
                    useCase = usecase,
//                    mapper = ItemMapper()
            ) as T
        }
        throw IllegalArgumentException(
                "Unknown class fro the view model. Passed ${modelClass.canonicalName} " +
                        "but required NewsListViewModel"
        )
    }

}
