package by.academy.questionnaire

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.databinding.CustomDialogBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase
import by.academy.questionnaire.domain.QUseCaseImpl
import by.academy.questionnaire.fragments.AppFragmentManager
import by.academy.questionnaire.fragments.FragmentForm
import by.academy.questionnaire.fragments.FragmentFormComparing
import by.academy.questionnaire.fragments.FragmentFormList
import by.academy.questionnaire.fragments.FragmentFormResult
import by.academy.questionnaire.viewmodel.MyViewModelFactory
import com.google.android.material.snackbar.Snackbar

const val LOG_TAG = "LOG_TAG"
const val USE_CONFIRMS = true

class MainActivity : AppCompatActivity(), AppFragmentManager {

    private val fragmentFormList by lazy { FragmentFormList() }
    // нахер. Оно работает криво. Создаю заново фрагмент
//    private val fragmentForm by lazy { FragmentForm() }
    private val fragmentFormResult by lazy { FragmentFormResult() }
    private val fragmentFormComparing by lazy { FragmentFormComparing() }

    lateinit var previousFragment: Fragment
    var levelCount = 0

    private lateinit var usecase: QUseCase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val databaseInfo = DatabaseInfo.init(this).value
        usecase = QUseCaseImpl(databaseInfo)
        viewModelFactory = MyViewModelFactory(usecase)
        sharedPreferences = getSharedPreferences("checkedForm", Context.MODE_PRIVATE)
        if (savedInstanceState == null) {
            loadFragment()
        }
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragmentFormList, "FormList")
//                .addToBackStack("FormList")
                .commit()
        Log.i("FRAGMENT_TAG", "loadFragment(). $levelCount prev set to null  -> $fragmentFormList")
        previousFragment = fragmentFormList
    }

    override fun showFormFragment(furContext: FURContext, fromList: Boolean) {
//        fragmentForm.furContext = furContext
//        if (fromList) {
        levelCount++
//            previousFragment  = fragmentFormList
//        }
//        else{
        Log.i("FRAGMENT_TAG", "showFormFragment(). $levelCount prev set to $previousFragment  -> $fragmentFormList")
        previousFragment = fragmentFormList
//        }
        supportFragmentManager.commit(){
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            replace(R.id.fragmentContainer, FragmentForm::class.java, bundleOf("furContext" to furContext))
        }
    }

    override fun showFormFragmentInCompareMode(furContext: FURContext, anotherFurContext: FURContext) {
        levelCount++
        Log.i("FRAGMENT_TAG", "showFormFragmentInCompareMode(). $levelCount prev set to $previousFragment  -> $fragmentFormResult")
        previousFragment = fragmentFormResult

        fragmentFormComparing.setContexts(furContext, anotherFurContext)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentFormComparing, "FormCompare").also {
                    it.commit()

                }
    }

    override fun showFormResultFragment(furContext: FURContext, forward: Boolean) {
        val fragment = supportFragmentManager.fragments[0]
        if (forward) {
            levelCount++
            previousFragment = fragment
        } else {
            levelCount--
            previousFragment = fragmentFormList
        }
        Log.i("FRAGMENT_TAG", "showFormResultFragment(). $levelCount prev set to $previousFragment  -> $fragment")

        fragmentFormResult.furContext = furContext
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentFormResult, "FormResult")
//                .addToBackStack("FormResult")
                .commit()
    }

    override fun showFormListFragment() {
        levelCount--
//        supportFragmentManager.popBackStack("FormList", 0)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentFormList, "FormList")
                .commit()
        // мы только оттуда нажимаем эту кнопку
        Log.i("FRAGMENT_TAG", "showFormListFragment(). $levelCount prev set to $previousFragment  -> $fragmentFormResult")
        previousFragment = fragmentFormResult
    }

    override fun onBackPressed() {
        if (levelCount == 0) {
            super.onBackPressed()
            return
        }
        levelCount--
        val currentFragment = supportFragmentManager.fragments[0]
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, previousFragment, "FormList")
                .commit()

        if (currentFragment is FragmentForm || currentFragment == fragmentFormComparing || currentFragment == fragmentFormResult) {
            Log.i("FRAGMENT_TAG", "onBackPressed(). $levelCount prev set to $previousFragment  -> $fragmentFormList")
            previousFragment = fragmentFormList
        } else {
            Log.i("FRAGMENT_TAG", "onBackPressed(). $levelCount prev set to $previousFragment  -> $currentFragment")
            previousFragment = currentFragment
        }
//        super.onBackPressed()
//        val backStackEntryCount = supportFragmentManager.backStackEntryCount
//        if (backStackEntryCount <= 1) {
//            finish()
//            super.onBackPressed()
//            return
//        }
//        if (supportFragmentManager.fragments.isNotEmpty()) {
//            if (supportFragmentManager.fragments[0] == fragmentForm) {
//                supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragmentContainer, fragmentFormList, "FormList")
//                        .commit()
//                return
//            }
//        }
//        if (backStackEntryCount == 3) {
//            supportFragmentManager.beginTransaction().remove(fragmentForm).setReorderingAllowed(true).commit()
//            supportFragmentManager.popBackStack("FormList", POP_BACK_STACK_INCLUSIVE)
//            return
//        }
//        supportFragmentManager.popBackStack()
    }

    override fun getQUseCase() = usecase
    override fun getModelFactory() = viewModelFactory

    override fun showError(error: String) {
        val baseContext: View = findViewById(R.id.fragmentContainer)
        snackbar = Snackbar.make(baseContext, error, Snackbar.LENGTH_INDEFINITE).also { it.show() }
    }

    override fun hideError() {
        snackbar?.dismiss()
    }

    override fun showConfirmDialog(question: String, yesFunction: () -> Unit) {
        if (USE_CONFIRMS) {
            showSelectUserDialog(question, yesFunction)
        } else {
            yesFunction.invoke()
        }
    }

    private fun showSelectUserDialog(question: String, yesFunction: () -> Unit) {
        val dialogView = CustomDialogBinding.inflate(layoutInflater)
        val dialogBuilder: AlertDialog = AlertDialog.Builder(this).create()
        with(dialogView) {
            textViewTitle.apply {
                text = question
                (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    topMargin = 50
                    bottomMargin = 40
                }
            }
            buttonCancel.apply { setOnClickListener { dialogBuilder.dismiss() } }
            buttonSubmit.apply {
                text = context.getString(R.string.button_yes)
                setOnClickListener {
                    dialogBuilder.dismiss()
                    yesFunction.invoke()
                }
            }
            dialogBuilder.setView(root)
        }
        dialogBuilder.show()
    }

}