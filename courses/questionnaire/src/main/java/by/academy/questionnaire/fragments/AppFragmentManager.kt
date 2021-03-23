package by.academy.questionnaire.fragments

import androidx.lifecycle.ViewModelProvider
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase
import kotlin.reflect.KFunction0

interface AppFragmentManager {
    fun showConfirmDialog(question: String, yesFunction: () -> Unit)
    fun showError(error:String )
    fun hideError()

    fun getQUseCase(): QUseCase
    fun getModelFactory(): ViewModelProvider.Factory

    fun showFormResultFragment(furContext: FURContext, forward: Boolean = true)
    fun showFormFragment(furContext: FURContext, addToBackStack: Boolean = false)
    fun showFormFragmentInCompareMode(furContext: FURContext, anotherFurContext: FURContext)
    fun showFormListFragment()
}
