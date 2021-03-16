package by.academy.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.academy.questionnaire.database.AppFragmentManager
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.databinding.FormListBinding

class FragmentFormList : Fragment(R.layout.form_list) {
    private lateinit var binding: FormListBinding
    private lateinit var fragmentManager: AppFragmentManager

    private val formListAdapter by lazy {
        FormListItemsAdapter(this::onCheckVisibility, this::onItemClicked)
    }

    //    private val viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    //    private lateinit var viewModel: FormsViewModel

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        binding.noItems.visibility = View.INVISIBLE
    } else {
        binding.noItems.visibility = View.VISIBLE
    }

    private fun onItemClicked(id: Long) {
        // если пройден - то сразу результаты
        if (formListAdapter.isPassed(id)) {
            fragmentManager.showFormResultFragment(id, 1)
        } else {
            // todo другой опльзователь - нужно показать его ответы
            fragmentManager.showFormFragment(id, addToBackStack = true)
        }
    }

    private fun onClearAll() {
        fragmentManager.getDatabaseInfo().getAnswerDAO().deleteAll()
        fragmentManager.getDatabaseInfo().getResultDAO().deleteAll()
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
//        viewModel = ViewModelProvider(this, viewModelFactory).get(FormsViewModel::class.java)
//        viewModel.init(requireActivity().applicationContext)
//        with(viewModel) {
//            FormsListLiveData.observe(viewLifecycleOwner, Observer { data -> showFormsList(data) })
//            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
        fetchForms()
//        }
    }


    private fun setToolbarSettings(toolbar: Toolbar) {
        with(toolbar) {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clearAll -> onClearAll()
                }
                true
            }

            val searchView = menu.findItem(R.id.search)?.actionView as SearchView
            searchView.apply {
                imeOptions = EditorInfo.IME_ACTION_DONE
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

    //todo fetch move
    private fun fetchForms() {
        val allForms: List<FormQuestionStatus> = fragmentManager.getDatabaseInfo().getFormDAO().getAllInfo()
        showFormsList(allForms)
    }

    private fun showFormsList(data: List<FormQuestionStatus>) {
        Log.i(LOG_TAG, "Model:$data")
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