package by.academy.questionnaire.fragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.QuestionListItemsComparingAdapter
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionListBinding
import by.academy.questionnaire.domain.FURContext


class FragmentFormComparing : Fragment(R.layout.question_list) {
    private lateinit var furContext: FURContext
    private lateinit var anotherFurContext: FURContext

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

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentFormComparing#onViewCreated")

        fragmentManager = requireActivity() as AppFragmentManager
        binding = QuestionListBinding.bind(view)
                .apply {
                    progressbar.visibility = GONE
                    recyclerView.apply {
                        adapter = questionListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    submit.apply {
                        visibility = VISIBLE
                        text = context.getString(R.string.navigation_back)
                        setOnClickListener { onSubmit() }
                    }
                    cancel.visibility = GONE

                    with(fragmentManager.getQUseCase()) {
                        val username1 = getUserName(furContext.userId)
                        val username2 = getUserName(anotherFurContext.userId)
                        val color1 = ContextCompat.getColor(root.context, R.color.red)
                        val color2 = ContextCompat.getColor(root.context, R.color.blue)
                        """${getString(R.string.compareTitle)}: <font color=$color1>$username1</font> 
                                    |${getString(R.string.and)} <font color=$color2>$username2</font>""".trimMargin()
                                .also {
                                    viewTextTitle.text = Html.fromHtml(it)
                                }

                    }

                    viewTextTitleRight.apply {
                        text = getString(R.string.comparing_filter)
                        setOnClickListener { onFilter(binding.viewTextTitleRight) }
                    }
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
        viewText.text = getString(questionListAdapter.toggleFilterSimilar())
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