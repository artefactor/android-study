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
import by.academy.questionnaire.logic.ResultCalculatorFactory


class FragmentFormResult : Fragment(R.layout.result) {
    private val resultCalculator: ResultCalculatorFactory = ResultCalculatorFactory()
    internal var formId: Long = -1
    internal var userId: Long = 1
    private lateinit var binding: ResultBinding
    private var hasAnyOtherUser: Boolean = false
    private lateinit var fragmentManager: AppFragmentManager
    private val resultListAdapter by lazy {
        ResultListItemsAdapter(this::onCheckVisibility, this::onItemClicked, this::onItemCompareClicked)
    }
    //    private val viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

    private fun onItemClicked(newUserId: Long) {
        userId = newUserId
        fillInfo()
        resultListAdapter.notifyDataSetChanged()
    }

    private fun onItemCompareClicked(newUserId: Long) {
        fillInfoInComparedMode(newUserId)
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

    private fun setButtonBehavior() {
        (binding).also {
            it.button1Home.setOnClickListener { onHomeButton() }
            it.button3Repeat.visibility = GONE
            it.button3Repeat.setOnClickListener { onRepeatButton() }
            it.button4RepeatFried.setOnClickListener { onRepeatFriendButton() }

            //   если несолько пользователей, то тогда кнопка ведет себя как рестарт
            if (hasAnyOtherUser) {
                it.button2Restart.setOnClickListener { onRepeatFriendButton() }
                it.button4RepeatFried.visibility = GONE
            } else {
                it.button2Restart.setOnClickListener { onRestartButton() }
                it.button4RepeatFried.visibility = VISIBLE
            }
        }
    }

    /**
     * все будет пока onRestart
     */
    private fun onRestartButton() {
        // clear last answers
        fragmentManager.getDatabaseInfo().getAnswerDAO().deleteByFormId(formId)
        // show form fragment
        // выбор пользователя если уже их несколько
        //   fragmentManager.showFormFragment(formId, userNotNUll.getId())
        fragmentManager.showFormFragment(formId)
    }

    /**
     * все будет пока onRestart
     */
    @Deprecated("method will be used in next iteration")
    private fun onRepeatButton() {
        // show form fragment
        // выбор пользователя если уже их несколько
//        fragmentManager.showFormFragment(formId, userNotNUll.getId())
        fragmentManager.showFormFragment(formId)
    }

    private fun onRepeatFriendButton() {
        // open dialog for friend name
        // show form fragment
        showSelectUserDialog()
    }

    private fun showSelectUserDialog() {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(this.requireContext()).create()
        val dialogView: View = layoutInflater.inflate(R.layout.custom_dialog, null)
        with(dialogView) {
            val editText = findViewById<View>(R.id.edt_comment) as EditText
            val descriptionView = findViewById<View>(R.id.textView) as TextView

            val choseUser = findViewById<View>(R.id.userChoose) as RadioGroup
            val userDAO = fragmentManager.getDatabaseInfo().getUserDAO()
            val users = userDAO.getAll()
            users.forEach { user ->
                val newRadioButton: AppCompatRadioButton = AppCompatRadioButton(context)
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
                val userNotNUll: UserEntity

                val checkedId = choseUser.checkedRadioButtonId
                if (checkedId == -1 ){
                    showError("Выберите вариант")
                    return@setOnClickListener
                }
                if (checkedId == newRadioButton.id) {
                    val userName = editText.text.toString()

                    // если уже такой существует - то можно либо переспросить, либо добавить
                    val user = userDAO.findByUserName(userName)

                    userNotNUll = if (user == null) {
                        val id = userDAO.add(UserEntity(0, userName))
                        UserEntity(id, userName)
                    } else {
//                        user
                        showError("Такой уже есть, выберите другое имя")
                        return@setOnClickListener
                    }
                } else {
                    val userName = choseUser.findViewById<RadioButton>(checkedId).text.toString()
                    userNotNUll = userDAO.findByUserName(userName)!!
                }

                dialogBuilder.dismiss()
                // clear last answers

                fragmentManager.getDatabaseInfo().getAnswerDAO().deleteByFormId(formId, 1, userNotNUll.getId())
                // show form fragment
                fragmentManager.showFormFragment(formId, userNotNUll.getId())
            }

            dialogBuilder.setView(this)
        }
        dialogBuilder.show()
    }


    private fun fetchForms() {
        fillInfo()
        val resultDAO = fragmentManager.getDatabaseInfo().getResultDAO()
        val data: List<ResultUser> = resultDAO.getAllByFormId(formId)
        hasAnyOtherUser = (data.size > 1 || data.size == 1 && data[0].resultEntity.userId != 1L)
        setButtonBehavior()

        resultListAdapter.items = data
        resultListAdapter.allItems = data
    }

    private fun fillInfo() {
        resultListAdapter.currentUserId = userId
        resultListAdapter.userIdInCompere = -1
        val resultDAO = fragmentManager.getDatabaseInfo().getResultDAO()
        val resultUser = resultDAO.getInfo(formId, userId)
        val resultInfo = resultCalculator.parseResult(resultUser.resultEntity.result, formId)
        binding.viewTextTitle.text = "Результаты  ${resultUser.userName}"
        binding.viewTextDescription.text = """Вы прошли тест. Результаты ниже
$resultInfo""".trimMargin().trimIndent().trimStart().trim()
    }

    private fun fillInfoInComparedMode(newUserId: Long) {
        resultListAdapter.currentUserId = userId
        resultListAdapter.userIdInCompere = newUserId

        val resultDAO = fragmentManager.getDatabaseInfo().getResultDAO()
        //todo можно оптимизировать, т.к. мы уже имеем результаты на текущего юзера
        // более того, если будет юзер тыкаться между другими - то можно их в кэш фрагмента ложить
        val resultUser1 = resultDAO.getInfo(formId, userId)
        val resultUser2 = resultDAO.getInfo(formId, newUserId)

        val resultInfo = resultCalculator.parseResults(
                resultUser1.resultEntity.result,
                resultUser2.resultEntity.result,
                formId
        )

        binding.viewTextTitle.text = "Результаты  ${resultUser1.userName} vs ${resultUser2.userName}"
        binding.viewTextDescription.text = """Вы прошли тест. Сравниваем результаты
$resultInfo
""".trimMargin().trimIndent()
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


