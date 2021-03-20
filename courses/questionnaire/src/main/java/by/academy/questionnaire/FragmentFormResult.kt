package by.academy.questionnaire

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
import by.academy.questionnaire.database.AppFragmentManager
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
                this::onDeleteClicked
        )
    }
    //    private val viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

    private fun onDeleteClicked(furContext: FURContext) {
        //todo confirm dialog
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

    private fun onHomeButton() {
        fragmentManager.showFormListFragment()
    }

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
        fragmentManager.getQUseCase().restartTest(furContext)
        // show form fragment
        // выбор пользователя если уже их несколько
        fragmentManager.showFormFragment(furContext)
    }

    private fun onRepeatButton() {
        // show form fragment
        // выбор пользователя если уже их несколько
        val newResultId = fragmentManager.getQUseCase().startNextAttemptTest(furContext)
        fragmentManager.showFormFragment(FURContext(furContext.formId,
                furContext.userId,
                newResultId
        ))
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
            val editText = findViewById<View>(R.id.edt_comment) as EditText
            val descriptionView = findViewById<View>(R.id.textView) as TextView

            val choseUser = findViewById<View>(R.id.userChoose) as RadioGroup

            val users: List<UserEntity> = fragmentManager.getQUseCase().getAllUsers()
            users.forEach { user ->
                val newRadioButton = AppCompatRadioButton(context)
                newRadioButton.text = "${user.name}"
                newRadioButton.textSize = 16f
                newRadioButton.setPadding(16, 1, 4, 1)
                choseUser.addView(newRadioButton)
            }
            val newRadioButton = AppCompatRadioButton(context)
            newRadioButton.textSize = 16f
            newRadioButton.setPadding(16, 1, 4, 1)
            newRadioButton.text = "Добавить нового"
            choseUser.addView(newRadioButton)
            choseUser.setOnCheckedChangeListener { _, checkedId ->
                // get the radio group checked radio button
                if (checkedId == newRadioButton.id) {
                    editText.visibility = VISIBLE
                    descriptionView.visibility = VISIBLE
                } else {
                    editText.visibility = GONE
                    descriptionView.visibility = GONE
                }
            }


            val buttonSubmit = findViewById<View>(R.id.buttonSubmit) as Button
            val buttonCancel = findViewById<View>(R.id.buttonCancel) as Button

            buttonCancel.setOnClickListener { dialogBuilder.dismiss() }
            buttonSubmit.setOnClickListener {
                val checkedId = choseUser.checkedRadioButtonId
                if (checkedId == -1) {
                    showError("Выберите вариант")
                    return@setOnClickListener
                }
                val editTextText = editText.text.toString()
                val checkedText = choseUser.findViewById<RadioButton>(checkedId).text.toString()
                val newFURContext: FURContext = fragmentManager.getQUseCase()
                        .startTestForUser(furContext.formId, checkedId, checkedText, newRadioButton.id, editTextText, repeat)
                if (newFURContext.userId <= 0) {
                    showError("Такой уже есть, выберите другое имя")
                    return@setOnClickListener
                }

                dialogBuilder.dismiss()
                // show form fragment
                fragmentManager.showFormFragment(newFURContext)
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
        binding.viewTextTitleCompare.text = ""
        resultListAdapter.currentResultId = furContext.resultId
        resultListAdapter.resultIdInCompare = -1
        val resultUser = fragmentManager.getQUseCase().getAttempt(furContext.resultId)
        val resultInfo = resultCalculator.parseResult(resultUser.resultEntity.result, furContext.formId)
        binding.viewTextTitle.text = "Результаты  ${resultUser.userName}"
        binding.viewTextDescription.text = """Вы прошли тест. Результаты ниже
$resultInfo""".trimMargin().trimIndent().trimStart().trim()
    }

    private fun checkOnEmptyList(resetCurrentResultId: Boolean, data: List<ResultUser>): Boolean {
        if (resetCurrentResultId) {
            if (data.isEmpty()) {
                // no items
                binding.viewTextTitle.text = "Результатов нет"
                binding.viewTextDescription.text = """Вы удалили все результаты. Пройдите заново"""
                return true
            } else {
                data[0].resultEntity.also {
                    furContext = FURContext(it.formId, it.userId, it.getId())
                }
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
        binding.viewTextTitleCompare.text = "Сравнить ответы"
        binding.viewTextTitleCompare.setOnClickListener { this.onCompareAnswers(furContext, anotherFurContext) }
        binding.viewTextTitle.text = "Результаты  ${resultUser1.userName} vs ${resultUser2.userName}"
        binding.viewTextDescription.text = """Вы прошли тест. Сравниваем результаты
$resultInfo
""".trimMargin().trimIndent()
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


