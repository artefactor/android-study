package by.academy.questionnaire.database

interface AppFragmentManager {
    fun showError(error:String )
    fun hideError()

    fun getDatabaseInfo(): DatabaseInfo

    fun showFormResultFragment(formId: Long, userId: Long)
    fun showFormFragment(formId: Long, userId: Long = 1L, addToBackStack: Boolean = false)
    fun showFormListFragment()
}
