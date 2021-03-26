package by.academy.questionnaire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.R
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.databinding.FormInfoBinding
import by.academy.questionnaire.domain.FormStatus.FINISHED
import by.academy.questionnaire.domain.FormStatus.IN_PROCESS
import by.academy.questionnaire.domain.FormStatus.NOT_STARTED
import by.academy.questionnaire.domain.convertToFormStatus
import java.util.*

class FormListItemsAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (FormQuestionStatus) -> Unit,

        ) : RecyclerView.Adapter<FormListItemsAdapter.FormListItemViewHolder>() {

    var allItems: List<FormQuestionStatus> = emptyList()
    var items: List<FormQuestionStatus> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FormListItemViewHolder(this,
            itemBinding = FormInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FormListItemViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
    private fun update() = notifyDataSetChanged().also { checkVisibilityListener.invoke(itemCount > 0) }

    fun filter(text: String?) {
        items = allItems.filter { t -> t.title.toLowerCase(Locale.ROOT).contains(text?.toLowerCase(Locale.ROOT).toString()) }
        update()
    }

    fun clearAnswers() {
        allItems.forEach(FormQuestionStatus::clear)
        items.forEach(FormQuestionStatus::clear)
        notifyDataSetChanged()
    }

    class FormListItemViewHolder(
            private val adapter: FormListItemsAdapter,
            private val itemBinding: FormInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: FormQuestionStatus) {
            with(itemBinding) {
                root.setOnClickListener { adapter.onItemClickEvent(item) }

                when (val resID = root.resources.getIdentifier(item.icon, "drawable", "by.academy.questionnaire")) {
                    0 -> imageStatus.setImageResource(R.drawable.ic_noun_questionnaire)
                    else -> imageStatus.setImageResource(resID)
                }

                viewTextTitle.text = item.title
                convertToFormStatus(item)
                        .also {
                            viewTextDescription.apply {
                                text = when (it) {
                                    FINISHED -> root.context.getString(R.string.form_list_results)
                                    NOT_STARTED -> ""
                                    IN_PROCESS -> root.context.getString(R.string.form_list_continue,
                                            item.questionCount - item.passedQuestionCount)
                                }
                            }
                            viewTextRight.apply {
                                text = when (it) {
                                    FINISHED -> ""
                                    NOT_STARTED -> item.questionCount.toString()
                                    IN_PROCESS -> ""
                                }
                            }
                        }
            }
        }
    }

}