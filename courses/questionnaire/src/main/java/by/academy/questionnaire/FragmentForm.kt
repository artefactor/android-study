package by.academy.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.database.AppFragmentManager
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.databinding.QuestionListBinding
import by.academy.questionnaire.logic.ResultCalculatorFactory

class FragmentForm : Fragment(R.layout.question_list) {
    private val resultCalculator: ResultCalculatorFactory = ResultCalculatorFactory()
    internal var formId: Long = -1
    internal var userId: Long = 1
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

    private fun onItemClicked(answerQuestion: AnswerQuestion) {
        fragmentManager.hideError()
        val answerEntity = answerQuestion.answerEntity!!
        val answerDAO = fragmentManager.getDatabaseInfo().getAnswerDAO()
        answerEntity.userId = userId
        if (answerEntity.getId() == 0L) {
            val res: Long = answerDAO.add(answerEntity)
            // не самый лучший вариант пока. Нам нужно обновить id ответа, чтобы если пользователь выберет другой ответ,
            // то это было уже обновление а не добавление
            // В базу в отдельном потоке добавлять или нет
            /* TODO денис,
                    я обновляю только одно значение в базу. в таблице ответов в принципе может быть
                    50 тестов на 60 вопросов, даже если каждый проходить по 3 раза, то
                    10000 записей. Нужно ли тут делать в отдельном потоке?
                    если так. то нужно как-то отслеживать момент когда пользователь быстро поменял ответ
                    чтобы если мы сохраняем ответ в базу, а в адаптере еще не обновили то чтобы он не успел засабмитать новый ответ
             */
            answerEntity.setId(res)
            questionListAdapter.update(answerQuestion)
            onItemAdded()
        } else {
            answerDAO.update(answerEntity)
        }
    }

    private fun onItemAdded() {
        // show progress
        binding.progressbar.progress = binding.progressbar.progress + 1
    }

    private fun onSubmit() {
        val answers: List<AnswerEntity> = questionListAdapter.unbindAnswers()
        if (answers.size == questionListAdapter.allItems.size) {
            //todo show confirm dialog
            // todo make this in coroutine
            val resultEntity: ResultEntity = resultCalculator.calculateResult(formId, userId, answers)
            fragmentManager.getDatabaseInfo().getResultDAO().delete(formId, userId)
            fragmentManager.getDatabaseInfo().getResultDAO().add(resultEntity)
            fragmentManager.showFormResultFragment(formId, userId)
        } else {
            // TODO show dialog instead
            //почему-то не показывается, а только со второго нажатия..
            showError("Please fill all questions")
            questionListAdapter.filterUnanswered()
            showError("Please fill all questions")
        }
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
                    it.submit.setOnClickListener { onSubmit() }

                    val username = fragmentManager.getDatabaseInfo().getUserDAO().getInfo(userId).name
                    it.viewTextTitle.setText("В процессе: $username ")
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
        val questions: List<AnswerQuestion> = fragmentManager.getDatabaseInfo().getAnswerDAO().getLastAttemptListWithAnswersByFormId(formId, userId)
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