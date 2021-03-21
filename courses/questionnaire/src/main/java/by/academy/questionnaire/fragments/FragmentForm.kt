package by.academy.questionnaire.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.QuestionListItemsAdapter
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionListBinding
import by.academy.questionnaire.domain.FURContext

class FragmentForm : Fragment(R.layout.question_list) {
    internal var furContext: FURContext = FURContext(0, 1, 0)

    private lateinit var binding: QuestionListBinding
    private lateinit var fragmentManager: AppFragmentManager
    private val questionListAdapter by lazy {
        QuestionListItemsAdapter(this::onCheckVisibility, this::onItemClicked)
    }
    //    private val viewModelFactory: ViewModelProvider.Factory = MyViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

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
        binding.progressbar.progress = binding.progressbar.progress + 1
    }

    private fun onSubmitConfirm() {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_finish)) { onSubmit() }
    }

    private fun onSubmit() {
        val answers: List<AnswerQuestion> = questionListAdapter.allItems
        if (fragmentManager.getQUseCase().submitTest(furContext, answers)) {
            fragmentManager.showFormResultFragment(furContext)
        } else {
            //почему-то не показывается, а только со второго нажатия..
            // TODO show dialog instead
            showError(getString(R.string.warning_fill))
            questionListAdapter.filterUnanswered()
            showError(getString(R.string.warning_fill))
        }
    }

    private fun onCancelConfirm() {
        fragmentManager.showConfirmDialog(getString(R.string.confirm_abort)) { onCancel() }
    }

    private fun onCancel() {
        fragmentManager.getQUseCase().deleteAttempt(furContext.resultId)
        fragmentManager.showFormListFragment()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentForm#onViewCreated")

        fragmentManager = requireActivity() as AppFragmentManager
        binding = QuestionListBinding.bind(view)
                .apply {
                    progressbar.progress = 0
                    recyclerView.apply {
                        adapter = questionListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    submit.apply {
                        submit.visibility = VISIBLE
                        text = getString(R.string.action_finish)
                        setOnClickListener { onSubmitConfirm() }
                    }
                    cancel.apply {
                        visibility = VISIBLE
                        setOnClickListener { onCancelConfirm() }
                    }
                    val username = fragmentManager.getQUseCase().getUserName(furContext.userId)
                    viewTextTitle.text = getString(R.string.in_processTitle, username)
                }
//        viewModel = ViewModelProvider(this, viewModelFactory).get(FormsViewModel::class.java)
//        viewModel.init(requireActivity().applicationContext)
//        with(viewModel) {
//            FormsListLiveData.observe(viewLifecycleOwner, Observer { data -> showFormsList(data) })
//            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
        fetchForm()
//        }
    }

    private fun fetchForm() {
        val questions: List<AnswerQuestion> = fragmentManager.getQUseCase().getAttemptAnswers(furContext)
        showFormsList(questions)
    }

    private fun showFormsList(data: List<AnswerQuestion>) {
        Log.i(LOG_TAG, "Model:$data")
        hideError()
        val passedCount = data.count { answerQuestion -> answerQuestion.answerEntity != null }
        binding.progressbar.max = data.size
        binding.progressbar.progress = passedCount
        questionListAdapter.items = data
        questionListAdapter.allItems = data
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }

    fun hideError() {
        fragmentManager.hideError()
    }

}