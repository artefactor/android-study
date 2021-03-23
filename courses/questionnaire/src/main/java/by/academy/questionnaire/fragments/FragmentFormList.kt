package by.academy.questionnaire.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.adapters.FormListItemsAdapter
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.databinding.FormListBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.FormStatus
import by.academy.questionnaire.domain.convertToFormStatus
import by.academy.questionnaire.viewmodel.FormsViewModel

class FragmentFormList : Fragment(R.layout.form_list) {
    private lateinit var binding: FormListBinding
    private lateinit var fragmentManager: AppFragmentManager
    private lateinit var viewModel: FormsViewModel
    private val formListAdapter by lazy {
        FormListItemsAdapter(this::onCheckVisibility, this::onItemClicked)
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        binding.noItems.visibility = View.INVISIBLE
    } else {
        binding.noItems.visibility = View.VISIBLE
    }

    private fun onItemClicked(item: FormQuestionStatus) {
        // тут варианты такие
        when (convertToFormStatus(item)) {
            // 1. не начат - открываем на старт
            FormStatus.NOT_STARTED -> {
                val userId = 1L
                val resultId = fragmentManager.getQUseCase().startTest(item.formId, userId)
                val furContext = FURContext(item.formId, userId, resultId)
                fragmentManager.showFormFragment(furContext, addToBackStack = true)
            }
            // 2. есть активное прохождение  - открываем его
            FormStatus.IN_PROCESS -> {
                val furContext = FURContext(item.formId, item.userId, item.mainResultId)
                fragmentManager.showFormFragment(furContext, addToBackStack = true)
            }
            FormStatus.FINISHED -> {
                // 3. пройден, нет активных  - открываем на результат
                val furContext = fragmentManager.getQUseCase().findLastResult(item.formId)
                fragmentManager.showFormResultFragment(furContext)
            }
        }
    }

    private fun onClearAll() {
        fragmentManager.getQUseCase().clearAllAnswers()
        formListAdapter.clearAnswers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentFormList#onViewCreated")

        fragmentManager = requireActivity() as AppFragmentManager
        binding = FormListBinding.bind(view)
                .also {
                    setToolbarSettings(it.toolBarList)
                    it.recyclerView.apply {
                        adapter = formListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }
        // получается она одна на фрагмент, при каждом переходе на другой фрагмент - все данные перечитываются.
        // может это и неплохо. Но можно их и не перечитывать, а точечно обновлять в моем случае.
        // там же ничего не меняется. Максимум данные от отдного теста
        // и в памяти пусть висят эти модели?
        // или очищаются? как лучше?
        // создавать каждый раз все заново только для одного экрана
        // или вычитывать и сохранять в оперативную память?
        viewModel = ViewModelProvider(this, fragmentManager.getModelFactory()).get(FormsViewModel::class.java).also {
            it.formListLiveData.observe(viewLifecycleOwner, this::showFormsList)
            it.errorLiveData.observe(viewLifecycleOwner, this::showError)
            it.infoLiveData.observe(viewLifecycleOwner, this::onDbInfo)
            it.fetchForms()
        }
    }


    private fun setToolbarSettings(toolbar: Toolbar) {
        with(toolbar) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clearAll -> onClearAll()
//                    R.id.userManagement -> onUserMgmt()
                    R.id.dbInfo -> viewModel.onDbInfo()
                }
                true
            }

            val searchView = menu.findItem(R.id.search)?.actionView as SearchView
            searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(text: String?) = false

                    override fun onQueryTextChange(text: String?): Boolean {
                        formListAdapter.filter(text)
                        return false
                    }
                })
            }
        }
    }

    private fun onDbInfo(info: String) {
        val myDialogFragment = DialogFragment("Статистика", info)
        val manager = requireActivity().supportFragmentManager
        myDialogFragment.show(manager, "myDialog")
    }

    private fun onUserMgmt() {
        val myDialogFragment = DialogFragment("", "")
        val manager = requireActivity().supportFragmentManager
        myDialogFragment.show(manager, "myDialog")
    }


    private fun showFormsList(data: List<FormQuestionStatus>) {
        Log.i("model", "Model:$data")
        hideError()
        formListAdapter.items = data
        formListAdapter.allItems = data
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }

    fun hideError() {
        fragmentManager.hideError()
    }

}