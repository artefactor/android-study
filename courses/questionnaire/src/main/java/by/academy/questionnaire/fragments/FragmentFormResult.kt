package by.academy.questionnaire.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.ResultListItemsAdapter
import by.academy.questionnaire.adapters.toFurContext
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import by.academy.questionnaire.databinding.ResultBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.logic.ResultCalculatorFactory

class FragmentFormResult : Fragment(R.layout.result) {
    private val resultCalculator: ResultCalculatorFactory = ResultCalculatorFactory()
    internal var furContext: FURContext = FURContext(0, 1, 0)
    private lateinit var binding: ResultBinding
    private lateinit var fragmentManager: AppFragmentManager
    private val resultListAdapter by lazy {
        ResultListItemsAdapter(
                this::onCheckVisibility,
                this::onItemClicked,
                this::onItemCompareClicked,
                this::onDeleteClickedConfirm
        )
    }
    //    private val viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

    private fun onDeleteClickedConfirm(furContext: FURContext) {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_delete)) { onDelete(furContext) }
    }

    private fun onDelete(furContext: FURContext) {
        fragmentManager.getQUseCase().deleteAttempt(furContext.resultId)
        //maybe optimized
        val resetCurrentResultId = this.furContext.resultId == furContext.resultId
        fetchForms(resetCurrentResultId)
    }

    private fun onItemClicked(furContext: FURContext) {
        this.furContext = furContext
        fillInfo()
        resultListAdapter.notifyDataSetChanged()
    }

    private fun onItemCompareClicked(furContext: FURContext) {
        fillInfoInComparedMode(furContext)
        resultListAdapter.notifyDataSetChanged()
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        binding.noItems.visibility = INVISIBLE
    } else {
        binding.noItems.visibility = VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentResultList#onViewCreated")

        fragmentManager = requireActivity() as AppFragmentManager
        binding = ResultBinding.bind(view)
                .also {
                    it.recyclerView.apply {
                        adapter = resultListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }

                }
//        viewModel = ViewModelProvider(this, viewModelFactory).get(FormsViewModel::class.java)
//        viewModel.init(requireActivity().applicationContext)
//        with(viewModel) {
//            FormsListLiveData.observe(viewLifecycleOwner, Observer { data -> showFormsList(data) })
//            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
        fetchForms()
//        }
    }

    private fun onHomeButton() = fragmentManager.showFormListFragment()

    private fun setButtonBehavior(hasAnyOtherUser: Boolean) {
        (binding).also {
            it.button1Home.setOnClickListener { onHomeButton() }
            it.button4RepeatFried.setOnClickListener { onRepeatFriendButton() }

            //   если несолько пользователей, то тогда кнопка ведет себя как рестарт
            if (hasAnyOtherUser) {
                it.button3Repeat.setOnClickListener { onRepeatFriendButton() }
                it.button2Restart.setOnClickListener { onRepeatFriendButton(false) }
                it.button4RepeatFried.visibility = GONE
            } else {
                it.button3Repeat.setOnClickListener { onRepeatButton() }
                it.button2Restart.setOnClickListener { onRestartButton() }
                it.button4RepeatFried.visibility = VISIBLE
            }
        }
    }

    private fun onRestartButton() {
        // clear last answers
        val newResultId = fragmentManager.getQUseCase().restartTest(furContext)
        val (f, u) = furContext
        // show form fragment
        // выбор пользователя если уже их несколько
        fragmentManager.showFormFragment(FURContext(f, u, newResultId))
    }

    private fun onRepeatButton() {
        // show form fragment
        // выбор пользователя если уже их несколько
        val newResultId = fragmentManager.getQUseCase().startNextAttemptTest(furContext)
        val (f, u) = furContext
        fragmentManager.showFormFragment(FURContext(f, u, newResultId))
    }

    private fun onRepeatFriendButton(repeat: Boolean = true) {
        // open dialog for friend name
        // show form fragment
        showSelectUserDialog(repeat)
    }

    private fun showSelectUserDialog(repeat: Boolean) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(this.requireContext()).create()
        val dialogView: View = layoutInflater.inflate(R.layout.custom_dialog, null)
        with(dialogView) {
            val editText = findViewById<EditText>(R.id.edt_comment)
            val descriptionView = findViewById<TextView>(R.id.textView)

            val choseUser = findViewById<View>(R.id.userChoose) as RadioGroup

            val users: List<UserEntity> = fragmentManager.getQUseCase().getAllUsers()
            users.forEach { user ->
                AppCompatRadioButton(context).apply {
                    text = "${user.name}"
                    textSize = 16f
                    setPadding(16, 1, 4, 1)
                    choseUser.addView(this)
                }
            }
            val newRadioButton = AppCompatRadioButton(context).apply {
                textSize = 16f
                setPadding(16, 1, 4, 1)
                text = context.getString(R.string.addNewUser)
                choseUser.addView(this)
                choseUser.setOnCheckedChangeListener { _, checkedId ->
                    // get the radio group checked radio button
                    if (checkedId == this.id) {
                        editText.visibility = VISIBLE
                        descriptionView.visibility = VISIBLE
                    } else {
                        editText.visibility = GONE
                        descriptionView.visibility = GONE
                    }
                }
            }

            findViewById<Button>(R.id.buttonCancel).apply { setOnClickListener { dialogBuilder.dismiss() } }
            findViewById<Button>(R.id.buttonSubmit).apply {
                setOnClickListener {
                    val checkedId = choseUser.checkedRadioButtonId
                    if (checkedId == -1) {
                        showError(context.getString(R.string.suggestion_choose_any))
                        return@setOnClickListener
                    }
                    val editTextText = editText.text.toString()
                    val checkedText = choseUser.findViewById<RadioButton>(checkedId).text.toString()
                    fragmentManager.getQUseCase()
                            .startTestForUser(furContext.formId, checkedId, checkedText, newRadioButton.id, editTextText, repeat)
                            .also {
                                if (it.userId <= 0) {
                                    showError(context.getString(R.string.suggestion_choose_another))
                                    return@setOnClickListener
                                }

                                dialogBuilder.dismiss()
                                // show form fragment
                                fragmentManager.showFormFragment(it)
                            }

                }
            }
            dialogBuilder.setView(this)
        }
        dialogBuilder.show()
    }


    private fun fetchForms(resetCurrentResultId: Boolean = false) {
        val data: List<ResultUser> = fragmentManager.getQUseCase().getResults(furContext.formId, this::setButtonBehavior)
        if (!checkOnEmptyList(resetCurrentResultId, data)) {
            fillInfo()
        }
        resultListAdapter.items = data
        resultListAdapter.allItems = data
    }

    private fun fillInfo() {
        with(binding) {
            viewTextTitleCompare.text = ""
            resultListAdapter.currentResultId = furContext.resultId
            resultListAdapter.resultIdInCompare = -1
            val resultUser = fragmentManager.getQUseCase().getAttempt(furContext.resultId)
            val resultInfo = resultCalculator.parseResult(resultUser.resultEntity.result, furContext.formId)
            viewTextTitle.text = getString(R.string.resultsTitle, resultUser.userName)
            viewTextDescription.text = getString(R.string.resultsDescription, resultInfo)
//            viewTextDescription.text = """ ${getString(R.string.resultsDescription)}
//$resultInfo""".trimMargin().trimIndent().trimStart().trim()
        }
    }

    private fun checkOnEmptyList(resetCurrentResultId: Boolean, data: List<ResultUser>): Boolean {
        if (resetCurrentResultId) {
            if (data.isEmpty()) {
                // no items
                binding.viewTextTitle.text = getString(R.string.no_results)
                binding.viewTextDescription.text = getString(R.string.no_results_suggestion)
                return true
            } else {
                furContext = data[0].resultEntity.toFurContext()
            }
        }
        return false
    }

    private fun fillInfoInComparedMode(anotherFurContext: FURContext) {
        resultListAdapter.currentResultId = furContext.resultId
        resultListAdapter.resultIdInCompare = anotherFurContext.resultId

        //todo можно оптимизировать, т.к. мы уже имеем результаты на текущего юзера
        // более того, если будет юзер тыкаться между другими - то можно их в кэш фрагмента ложить
        val resultUser1 = fragmentManager.getQUseCase().getAttempt(furContext.resultId)
        val resultUser2 = fragmentManager.getQUseCase().getAttempt(anotherFurContext.resultId)

        val resultInfo = resultCalculator.parseResults(
                resultUser1.resultEntity.result,
                resultUser2.resultEntity.result,
                furContext.formId
        )
        with(binding) {
            viewTextTitleCompare.text = getString(R.string.action_compare_answers)
            viewTextTitleCompare.setOnClickListener { onCompareAnswers(furContext, anotherFurContext) }
            viewTextTitle.text = getString(R.string.resultsTwoTitle, resultUser1.userName, resultUser2.userName)
            viewTextDescription.text = getString(R.string.resultsDescription, resultInfo)
//            viewTextDescription.text = """Вы прошли тест. Сравниваем результаты
//$resultInfo
//""".trimMargin().trimIndent()
        }
    }

    private fun onCompareAnswers(furContext: FURContext, anotherFurContext: FURContext) {
        fragmentManager.showFormFragmentInCompareMode(furContext, anotherFurContext)
    }

    private fun showFormsList(data: List<FormQuestionStatus>) {
        Log.i(LOG_TAG, "Model:$data")
        hideError()
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }

    fun hideError() {
        fragmentManager.hideError()
    }

}

