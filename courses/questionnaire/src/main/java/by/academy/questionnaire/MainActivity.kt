package by.academy.questionnaire

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import by.academy.questionnaire.database.AppFragmentManager
import by.academy.questionnaire.database.DatabaseInfo
import com.google.android.material.snackbar.Snackbar

const val LOG_TAG = "LOG_TAG"

class MainActivity : AppCompatActivity(), AppFragmentManager {

    //TODO use navigation?
    private val fragmentFormList by lazy { FragmentFormList() }
    private val fragmentForm by lazy { FragmentForm() }
    private val fragmentFormResult by lazy { FragmentFormResult() }

    private lateinit var databaseInfo: DatabaseInfo
    private lateinit var sharedPreferences: SharedPreferences

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseInfo = DatabaseInfo.init(this).value
        sharedPreferences = getSharedPreferences("checkedForm", Context.MODE_PRIVATE)
        if (savedInstanceState == null) {
            loadFragment()
        }
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragmentFormList, "FormList")
                .addToBackStack("FormList")
                .commit()
    }


    override fun showFormFragment(formId: Long, userId: Long,  addToBackStack: Boolean) {
        fragmentForm.formId = formId
        fragmentForm.userId = userId
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentForm, "Form").also {
                    if(addToBackStack){
                        it.addToBackStack("Form")
                    }
                    it.commit()
                }
    }

    override fun showFormResultFragment(formId: Long, userId: Long) {
        fragmentFormResult.formId = formId
        fragmentFormResult.userId = userId
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentFormResult, "FormResult")
                .addToBackStack("FormResult")
                .commit()
    }

    override fun showFormListFragment() {
        supportFragmentManager.popBackStack("FormList", 0)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
//            super.onBackPressed()
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun showError(error: String) {
        val baseContext: View = findViewById(R.id.fragmentContainer)
        snackbar = Snackbar.make(baseContext, error, Snackbar.LENGTH_INDEFINITE).also { it.show() }
    }

    override fun hideError() {
        snackbar?.dismiss()
    }

    override fun getDatabaseInfo() = databaseInfo


}