package by.academy.questionnaire.database

import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase

interface AppFragmentManager {
    fun showError(error:String )
    fun hideError()

    fun getQUseCase(): QUseCase

    fun showFormResultFragment(furContext: FURContext, forward: Boolean = true)
    fun showFormFragment(furContext: FURContext, addToBackStack: Boolean = false)
    fun showFormFragmentInCompareMode(furContext: FURContext, anotherFurContext: FURContext)
    fun showFormListFragment()
}
