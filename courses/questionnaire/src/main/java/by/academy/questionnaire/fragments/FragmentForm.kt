package by.academy.questionnaire.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.QuestionListItemsAdapter
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionListBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.viewmodel.FormsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

class FragmentForm : Fragment(R.layout.question_list) {
    internal var furContext: FURContext = FURContext(0, 1, 0)

    private lateinit var binding: QuestionListBinding
    private lateinit var fragmentManager: AppFragmentManager
    private lateinit var viewModel: FormsViewModel
    private val questionListAdapter by lazy {
        QuestionListItemsAdapter(this::onCheckVisibility, this::onItemClicked)
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        binding.noItems.visibility = View.INVISIBLE
    } else {
        binding.noItems.visibility = View.VISIBLE
    }

    private fun onItemClicked(answerQuestion: AnswerQuestion, option: Int) {
        fragmentManager.hideError()
        fragmentManager.getQUseCase().handleAnswer(answerQuestion, option, furContext, this::onItemAdded)
    }

    private fun onItemAdded() {
        // show progress
        val progress = binding.progressbar.progress
        binding.progressbar.progress = progress + 1
        binding.progressbarText.text = (progress + 1).toString()
    }

    private fun onSubmitConfirm() {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_finish)) { onSubmit() }
    }

    private fun onSubmit() {
        val answers: List<AnswerQuestion> = questionListAdapter.allItems
        // TODO Денис. праваильно ли я использую такую конструкцию? ПОчему варнинг пишет что возвращаемое значение нужно как-то использовать?
        fragmentManager.getQUseCase().submitTest(furContext, answers)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        fragmentManager.showFormResultFragment(furContext)
                    } else {
                        // можно показать диалог вместо этого
                        questionListAdapter.filterUnanswered()
                        //почему-то не показывается, а только со второго нажатия..
                        showError(getString(R.string.warning_fill))
                    }

                }, { error ->
                    Log.e(LOG_TAG, "exception during fetch data", error)
                })
    }

    private fun onCancelConfirm() {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_abort)) { onCancel() }
    }

    private fun onCancel() {
        fragmentManager.getQUseCase().deleteAttempt(furContext.resultId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fragmentManager.showFormListFragment() }, { error ->
                    Log.e(LOG_TAG, "exception during fetch data", error)
                })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentForm#onViewCreated")
        furContext = requireArguments().getParcelable("furContext")!!
        questionListAdapter.allItems = emptyList()
        fragmentManager = requireActivity() as AppFragmentManager
        binding = QuestionListBinding.bind(view)
                .apply {
                    progressbar.progress = 0
                    progressbarText.text = "0"
                    recyclerView.apply {
                        adapter = questionListAdapter
                        layoutManager = LinearLayoutManager(context)
                        startAnimation(AnimationUtils.loadAnimation(context, R.anim.placeholder))
                    }
                    submit.apply {
                        submit.visibility = VISIBLE
                        text = getString(R.string.button_finish)
                        setOnClickListener { onSubmitConfirm() }
                    }
                    cancel.apply {
                        visibility = VISIBLE
                        setOnClickListener { onCancelConfirm() }
                    }
                    val username = fragmentManager.getQUseCase().getUserName(furContext.userId)
                    viewTextTitle.text = getString(R.string.in_processTitle, username)
                }

        viewModel = ViewModelProvider(this, fragmentManager.getModelFactory()).get(FormsViewModel::class.java).also {
            it.formLiveData.observe(viewLifecycleOwner, this::showForm)
            it.errorLiveData.observe(viewLifecycleOwner, this::showError)
            it.fetchForm(furContext)
        }
    }

    private fun showForm(data: List<AnswerQuestion>) {
        Log.i(LOG_TAG, "Model:$data")
        hideError()
        binding.recyclerView.clearAnimation()
        val passedCount = data.count { answerQuestion -> answerQuestion.answerEntity != null }
        questionListAdapter.items = data
        questionListAdapter.allItems = data
        binding.progressbar.max = 0
        binding.progressbar.progress = 0
        binding.progressbar.max = data.size
        binding.progressbar.progress = passedCount
        binding.progressbarText.text = passedCount.toString()
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }

    fun hideError() {
        fragmentManager.hideError()
    }

}