package by.academy.questionnaire.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
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
                val layoutParamsGroup = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { gravity = Gravity.CENTER }
                val layoutParamsButton = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { gravity = Gravity.CENTER }

                val option1 = item.first.answerEntity!!.option
                val option2 = item.second.answerEntity!!.option
                val optionCount = 7 // todo get form db
                for (i in 0 until optionCount) {
                    AppCompatRadioButton(itemView.context)
                            .also {
                                it.layoutParams = layoutParamsButton
                                it.text = "$i"
                                defineColor(it, i, option1, option2)
                                radioWorkStatusLayout.addView(wrapInRadioGroup(it, layoutParamsGroup))
                            }
                }
            }
        }

        private fun defineColor(radioButton: AppCompatRadioButton, i: Int, option1: Int, option2: Int) {
            if (option1 == i && option2 == i) {
                radioButton.isChecked = true
                radioButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.violet))
                return
            }
            if (option1 == i) {
                radioButton.isChecked = true
                radioButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
                return
            }
            if (option2 == i) {
                radioButton.isChecked = true
                radioButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue))
                return
            }
            radioButton.isEnabled = false
        }

        private fun wrapInRadioGroup(radioButton: AppCompatRadioButton, params: LinearLayout.LayoutParams) =
                RadioGroup(itemView.context).apply {
                    layoutParams = params
                    gravity = Gravity.CENTER
                    setPadding(0, 1, 0, 1)
                    addView(radioButton)
                }
    }

}