package by.academy.questionnaire

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.database.AppFragmentManager
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionListBinding
import by.academy.questionnaire.domain.FURContext


class FragmentFormComparing : Fragment(R.layout.question_list) {
    internal lateinit var furContext: FURContext
    internal lateinit var anotherFurContext: FURContext

    private lateinit var binding: QuestionListBinding
    private lateinit var fragmentManager: AppFragmentManager
    private val questionListAdapter by lazy {
        QuestionListItemsComparingAdapter(this::onCheckVisibility, this::onItemClicked)
    }
    //    private val viewModelFactory: ViewModelProvider.Factory = MyViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        binding.noItems.visibility = View.INVISIBLE
    } else {
        binding.noItems.visibility = View.VISIBLE
    }

    private fun onItemClicked(answerQuestion: AnswerQuestion, option: Int) {
    }

    private fun onCancel() {
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentForm#onViewCreated")

        fragmentManager = requireActivity() as AppFragmentManager
        binding = QuestionListBinding.bind(view)
                .also {
                    it.progressbar.progress = 0
                    it.recyclerView.apply {
                        adapter = questionListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    it.submit.visibility = VISIBLE
                    it.submit.text = "назад"
                    it.submit.setOnClickListener { onSubmit() }
                    it.cancel.visibility = GONE

                    val username1 = fragmentManager.getQUseCase().getUserName(furContext.userId)
                    val username2 = fragmentManager.getQUseCase().getUserName(anotherFurContext!!.userId)

                    val text = "Сравниваем: <font color=#ee0000>$username1</font> и  <font color=#0000ee>$username2</font>"
                    it.viewTextTitle.text = Html.fromHtml(text)
                    it.viewTextTitleRight.text = "Фильтр одинаковых"
                    it.viewTextTitleRight.setOnClickListener { onFilter(binding.viewTextTitleRight) }
                }
//        viewModel = ViewModelProvider(this, viewModelFactory).get(FormsViewModel::class.java)
//        viewModel.init(requireActivity().applicationContext)
//        with(viewModel) {
//            FormsListLiveData.observe(viewLifecycleOwner, Observer { data -> showFormsList(data) })
//            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
        fetchForm()
//        }
    }

    private fun onFilter(viewText: TextView) {
        viewText.text = questionListAdapter.toggleFilterSimilar()
    }

    private fun onSubmit() {
        fragmentManager.showFormResultFragment(furContext, false)
    }

    private fun fetchForm() {
        val questions1: List<AnswerQuestion> = fragmentManager.getQUseCase().getAttemptAnswers(furContext)
        val questions2: List<AnswerQuestion> = fragmentManager.getQUseCase().getAttemptAnswers(anotherFurContext)
        val questions = arrayListOf<Pair<AnswerQuestion, AnswerQuestion>>()
        for (i in 1..questions1.size) {
            questions.add(questions1[i - 1] to questions2[i - 1])
        }
        showFormsList(questions)
    }

    private fun showFormsList(data: List<Pair<AnswerQuestion, AnswerQuestion>>) {
        Log.i(LOG_TAG, "Model:$data")
        hideError()
//        binding.progressbar.max = data.size
//        binding.progressbar.progress = passedCount
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

    fun setContexts(furContext: FURContext, anotherFurContext: FURContext) {
        this.furContext = furContext
        this.anotherFurContext = anotherFurContext
    }

}