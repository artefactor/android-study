package by.academy.questionnaire.fragments

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.ResultListItemsAdapter
import by.academy.questionnaire.adapters.toFurContext
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import by.academy.questionnaire.databinding.CustomDialogBinding
import by.academy.questionnaire.databinding.ResultBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.viewmodel.BarChartViewModel
import by.academy.questionnaire.logic.ResultCalculatorFactory
import by.academy.questionnaire.viewmodel.FormsViewModel

class FragmentFormResult : Fragment(R.layout.result) {
    private val barChartFragment by lazy { BarChartFragment() }
    private val resultCalculator: ResultCalculatorFactory = ResultCalculatorFactory()
    internal var furContext: FURContext = FURContext(0, 1, 0)
    private lateinit var binding: ResultBinding
    private lateinit var fragmentManager: AppFragmentManager
    private lateinit var viewModel: FormsViewModel
    lateinit var barChartViewModel: BarChartViewModel
    private var resetCurrentResultId: Boolean = false

    private val resultListAdapter by lazy {
        ResultListItemsAdapter(
                this::onCheckVisibility,
                this::onItemClicked,
                this::onItemCompareClicked,
                this::onDeleteClickedConfirm,
                this.requireContext()
        )
    }

    private fun onDeleteClickedConfirm(furContext: FURContext) {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_delete)) { onDelete(furContext) }
    }

    private fun onDelete(furContext: FURContext) {
        resetCurrentResultId = this.furContext.resultId == furContext.resultId
        viewModel.deleteAttempt(furContext.formId, furContext.resultId)
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

    override fun onDetach() {
        requireActivity().supportFragmentManager.beginTransaction()
                .remove(barChartFragment)
                .commit()
        super.onDetach()
    }

    //TODO DENIS. Здесь я хотел подключить фрагмент во фрагмент
    //где мне его лучше хранить и где подключать?
    // я сделать что он в креатед создается, а в детаче отключается
    // но может раз, в детаче - то тогда в аттаче подключить?
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentResultList#onViewCreated")

        requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.chartFragmentContainer, barChartFragment, "chart")
                .commit()
        fragmentManager = requireActivity() as AppFragmentManager
        barChartViewModel = ViewModelProvider(requireActivity()).get(BarChartViewModel::class.java)
        binding = ResultBinding.bind(view)
                .also {
                    it.recyclerView.apply {
                        adapter = resultListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }

        viewModel = ViewModelProvider(this, fragmentManager.getModelFactory()).get(FormsViewModel::class.java).also {
            it.formResultsLiveData.observe(viewLifecycleOwner, this::updateScreen)
            it.errorLiveData.observe(viewLifecycleOwner, this::showError)
            it.fetchFormResult(furContext.formId)
        }
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
                it.button2Restart.text = getString(R.string.button_restart)
                it.button4RepeatFried.visibility = GONE
            } else {
                it.button3Repeat.setOnClickListener { onRepeatButton() }
                it.button2Restart.setOnClickListener { onRestartButtonConfirm() }
                it.button2Restart.text = getString(R.string.button_restart_simple)
                it.button4RepeatFried.visibility = VISIBLE
            }
        }
    }

    private fun onRestartButtonConfirm() {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_restart)) { onRestartButton() }
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
        val dialogView = CustomDialogBinding.inflate(layoutInflater)
        with(dialogView) {
            val users: List<UserEntity> = fragmentManager.getQUseCase().getAllUsers()
            users.forEach { user ->
                AppCompatRadioButton(context).apply {
                    text = "${user.name}"
                    textSize = 16f
                    setPadding(16, 1, 4, 1)
                    userChoose.addView(this)
                }
            }
            val newRadioButton = AppCompatRadioButton(context).apply {
                textSize = 16f
                setPadding(16, 1, 4, 1)
                text = context.getString(R.string.addNewUser)
                userChoose.addView(this)
                userChoose.setOnCheckedChangeListener { _, checkedId ->
                    // get the radio group checked radio button
                    if (checkedId == this.id) {
                        edtComment.visibility = VISIBLE
                        textView.visibility = VISIBLE
                    } else {
                        edtComment.visibility = GONE
                        textView.visibility = GONE
                    }
                }
            }

            buttonCancel.apply { setOnClickListener { dialogBuilder.dismiss() } }
            buttonSubmit.apply {
                setOnClickListener {
                    val checkedId = userChoose.checkedRadioButtonId
                    if (checkedId == -1) {
                        showError(context.getString(R.string.suggestion_choose_any))
                        return@setOnClickListener
                    }
                    val editTextText = edtComment.text.toString()
                    val checkedText = userChoose.findViewById<RadioButton>(checkedId).text.toString()
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
            dialogBuilder.setView(root)
        }
        dialogBuilder.show()
    }

    private fun updateScreen(data: List<ResultUser>) {
        val hasAnyOtherUser = data.count { r -> r.resultEntity.userId != 1L } > 0
        setButtonBehavior(hasAnyOtherUser)

        if (!checkOnEmptyList(resetCurrentResultId, data)) {
            fillInfo()
        }
        resetCurrentResultId = false
        resultListAdapter.items = data
        resultListAdapter.allItems = data
    }

    private fun fillInfo() {
        with(binding) {
            viewTextTitleCompare.text = ""
            viewTextTitleCompare.clearAnimation()
            resultListAdapter.currentResultId = furContext.resultId
            resultListAdapter.resultIdInCompare = -1
            val resultUser = fragmentManager.getQUseCase().getAttempt(furContext.resultId)
            val resultInfo = resultCalculator.parseResult(resultUser.resultEntity.result, furContext.formId)
            barChartViewModel.chartMutableLiveData.value = (resultInfo.second)
            viewTextTitle.text = getString(R.string.resultsTitle, resultUser.userName)
//            viewTextDescription.text = getString(R.string.resultsDescription, resultInfo)
            viewTextDescription.visibility = GONE
        }
    }

    private fun checkOnEmptyList(resetCurrentResultId: Boolean, data: List<ResultUser>): Boolean {
        if (resetCurrentResultId) {
            if (data.isEmpty()) {
                // no items
                with(binding) {
                    viewTextTitle.text = getString(R.string.no_results)
                    chartFragmentContainer.visibility = GONE
                    viewTextDescription.visibility = VISIBLE
                    viewTextDescription.text = getString(R.string.no_results_suggestion)
                }
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
        barChartViewModel.chartMutableLiveData.value = (resultInfo.second)
        with(binding) {
            viewTextTitleCompare.apply {
                text = getString(R.string.action_compare_answers)
                paintFlags = Paint.UNDERLINE_TEXT_FLAG
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.placeholder))
                setOnClickListener { onCompareAnswers(furContext, anotherFurContext) }
            }
            viewTextTitle.text = getString(R.string.resultsTwoTitle, resultUser1.userName, resultUser2.userName)
//            viewTextDescription.text = getString(R.string.resultsDescription, resultInfo)
            viewTextDescription.visibility = GONE
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


