package by.academy.questionnaire.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.R
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionInfoBinding

class QuestionListItemsComparingAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (AnswerQuestion, Int) -> Unit,

        ) : RecyclerView.Adapter<QuestionListItemsComparingAdapter.QuestionListItemViewHolder>() {

    var allItems: List<Pair<AnswerQuestion, AnswerQuestion>> = emptyList()
    var items: List<Pair<AnswerQuestion, AnswerQuestion>> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionListItemViewHolder(this,
            itemBinding = QuestionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: QuestionListItemViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
    private fun update() = notifyDataSetChanged().also { checkVisibilityListener.invoke(itemCount > 0) }

    private var filtered: Int = 0

    fun toggleFilterSimilar(): Int {
        val size = items.size
        items = when (filtered) {
            0 -> allItems.filter { t -> t.first.answerEntity!!.option != t.second.answerEntity!!.option }
            1 -> allItems.filter { t -> t.first.answerEntity!!.option == t.second.answerEntity!!.option }
            else -> allItems
        }
        filtered = (filtered + 1) % 3
        if (items.size != size) {
            update()
        }
        return when (filtered) {
            0 -> R.string.comparing_filter_all
            1 -> R.string.comparing_filter_diff
            else -> R.string.comparing_filter_sim
        }
    }

    class QuestionListItemViewHolder(
            private val adapter: QuestionListItemsComparingAdapter,
            private val itemBinding: QuestionInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Pair<AnswerQuestion, AnswerQuestion>) {
            with(itemBinding) {
                viewTextTitle.text = "${item.first.question.index}. ${item.first.question.title}"
                radioWorkStatusLayout.removeAllViews()
                // options
                val option1 = item.first.answerEntity!!.option
                val option2 = item.second.answerEntity!!.option
                val optionCount = 7 // todo get form db
                for (i in 0 until optionCount) {
                    AppCompatRadioButton(itemView.context)
                            .also {
                                it.text = "$i"
                                RadioGroup(itemView.context).apply {
                                    this.addView(it)
                                    radioWorkStatusLayout.addView(this)
                                }

                                if (option1 == i && option2 == i) {
                                    it.isChecked = true
                                    it.setBackgroundColor(ContextCompat.getColor(root.context, R.color.violet))
                                    return@also
                                }
                                if (option1 == i) {
                                    it.isChecked = true
                                    it.setBackgroundColor(ContextCompat.getColor(root.context, R.color.red))
                                    return@also
                                }
                                if (option2 == i) {
                                    it.isChecked = true
                                    it.setBackgroundColor(ContextCompat.getColor(root.context, R.color.blue))
                                    return@also
                                }
                                it.isEnabled = false
                            }
                }
            }
        }
    }

}