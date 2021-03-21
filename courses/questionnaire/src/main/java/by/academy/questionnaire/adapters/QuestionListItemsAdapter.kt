package by.academy.questionnaire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionInfoBinding

class QuestionListItemsAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (AnswerQuestion, Int) -> Unit,

        ) : RecyclerView.Adapter<QuestionListItemsAdapter.QuestionListItemViewHolder>() {

    var allItems: List<AnswerQuestion> = emptyList()
    var items: List<AnswerQuestion> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionListItemViewHolder(this,
            itemBinding = QuestionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: QuestionListItemViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
    private fun update() = notifyDataSetChanged().also { checkVisibilityListener.invoke(itemCount > 0) }

    fun filterUnanswered() {
        items = allItems.filter { t -> t.answerEntity == null }
        update()
    }

    class QuestionListItemViewHolder(
            private val adapter: QuestionListItemsAdapter,
            private val itemBinding: QuestionInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: AnswerQuestion) {
            with(itemBinding) {
                viewTextTitle.text = "${item.question.index}. ${item.question.title}"

                // options
                val optionCount = 7 // todo get form db
                radioWorkStatus.clearCheck()
                if (radioWorkStatus.children.count() == 0) {
                    for (i in 1..optionCount) {
                        val newRadioButton = RadioButton(itemView.context)
                        newRadioButton.text = "${i - 1}"
                        radioWorkStatus.addView(newRadioButton)
                    }
                }

                if (item.answerEntity != null) {
                    val option = item.answerEntity!!.option
                    (radioWorkStatus[option] as RadioButton).isChecked = true
                }

                radioWorkStatus.setOnCheckedChangeListener { _, checkedId ->
                    // get the radio group checked radio button
                    radioWorkStatus.findViewById<RadioButton>(checkedId)?.apply {
                        adapter.onItemClickEvent.invoke(item, text.toString().toInt())
                    }
                }

            }
        }
    }

}