package by.academy.questionnaire

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.database.FORM_BURNOUT_MBI
import by.academy.questionnaire.database.FORM_WORK_ENGAGEMENT_UWES
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.databinding.FormInfoBinding
import by.academy.questionnaire.domain.FormStatus
import by.academy.questionnaire.domain.convertToFormStatus


class FormListItemsAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (FormQuestionStatus) -> Unit,

        ) : RecyclerView.Adapter<FormListItemsAdapter.FormListItemViewHolder>() {

    var allItems: List<FormQuestionStatus> = emptyList()
    var items: List<FormQuestionStatus> = emptyList()
        set(value) {
            field = value
            checkVisibility()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            FormListItemViewHolder(this@FormListItemsAdapter,
                    itemBinding = FormInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FormListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun filter(text: String?) {
        items = allItems.filter { t -> t.title.toLowerCase().contains(text?.toLowerCase().toString()) }
        notifyDataSetChanged()
        checkVisibility()
    }

    private fun checkVisibility() {
        checkVisibilityListener.invoke(itemCount > 0)
    }

    fun clearAnswers() {
        allItems.forEach { item ->
            with(item) {
                passedQuestionCount = 0
                countPasses = 0
                mainResultId = 0
                userId = 0
            }
        }
        items.forEach { item ->
            with(item) {
                passedQuestionCount = 0
                countPasses = 0
                mainResultId = 0
                userId = 0
            }
        }
        notifyDataSetChanged()
    }

    //todo rename and organize view, move strings
    class FormListItemViewHolder(
            private val adapter: FormListItemsAdapter,
            private val itemBinding: FormInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: FormQuestionStatus) {
            with(itemBinding) {
                //TODO move to database
                when (item.formId) {
                    FORM_WORK_ENGAGEMENT_UWES -> imageStatus.setImageResource(R.drawable.ic_noun_user_engagement_995657)
                    FORM_BURNOUT_MBI -> imageStatus.setImageResource(R.drawable.ic_noun_occupational_burnout_2717886)

                    else -> imageStatus.setImageResource(R.drawable.ic_noun_questionnaire_3683588)
                }

                viewTextTitle.text = item.title
                when (convertToFormStatus(item)) {
                    FormStatus.FINISHED -> {
                        // all passed
                        viewTextDescription.text = "Результаты"
                        viewTextRight.text = ""
                    }
                    FormStatus.NOT_STARTED -> {
                        // not started
                        viewTextDescription.text = ""
                        viewTextRight.text = item.questionCount.toString()
                    }
                    FormStatus.IN_PROCESS -> {
                        val remainingQuestions = item.questionCount - item.passedQuestionCount
                        // in the middle
                        viewTextDescription.text = "Продолжить. Осталось вопросов: "
                        viewTextRight.text = remainingQuestions.toString()
                    }
                }


                root.setOnClickListener {
                    adapter.onItemClickEvent(item)
                }
            }
        }
    }

}