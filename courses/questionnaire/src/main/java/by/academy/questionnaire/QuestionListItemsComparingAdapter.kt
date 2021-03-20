package by.academy.questionnaire

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.databinding.QuestionInfoBinding
import java.util.stream.Collectors.toList

class QuestionListItemsComparingAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (AnswerQuestion, Int) -> Unit,

        ) : RecyclerView.Adapter<QuestionListItemsComparingAdapter.QuestionListItemViewHolder>() {

    var allItems: List<Pair<AnswerQuestion, AnswerQuestion>> = emptyList()
    var items: List<Pair<AnswerQuestion, AnswerQuestion>> = emptyList()
        set(value) {
            field = value
            checkVisibility()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            QuestionListItemViewHolder(this@QuestionListItemsComparingAdapter,
                    itemBinding = QuestionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: QuestionListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    var filtered: Int = 0
    fun toggleFilterSimilar(): String {
        val size = items.size
        Log.i("filter_similar", "" + filtered)
        items = when (filtered) {
            0 -> {
                allItems.filter { t -> t.first.answerEntity!!.option != t.second.answerEntity!!.option }
            }
            1 -> {
                allItems.filter { t -> t.first.answerEntity!!.option == t.second.answerEntity!!.option }
            }
            else -> {
                allItems
            }
        }
        filtered = (filtered + 1) % 3
        if (items.size != size) {
            notifyDataSetChanged()
            checkVisibility()
        }
        return when (filtered) {
            0 -> "все"
            1 -> "разные"
            else -> "одинаковые"
        }
    }


    private fun checkVisibility() {
        checkVisibilityListener.invoke(itemCount > 0)
    }

    //todo rename and organize view, move strings
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
                for (i in 1..optionCount) {
                    val radioGroup = RadioGroup(itemView.context)
                    val newRadioButton = AppCompatRadioButton(itemView.context)
                    newRadioButton.text = "${i - 1}"
                    radioGroup.addView(newRadioButton)
                    radioWorkStatusLayout.addView(radioGroup)
                    if (option1 == i - 1 && option2 == i - 1) {
                        newRadioButton.isChecked = true
                        newRadioButton.setBackgroundColor(Color.parseColor("#ff00ff"))
                        continue
                    }
                    if (option1 == i - 1) {
                        newRadioButton.isChecked = true
                        newRadioButton.setBackgroundColor(Color.parseColor("#ee0000"))
                        continue
                    }
                    if (option2 == i - 1) {
                        newRadioButton.isChecked = true
                        newRadioButton.setBackgroundColor(Color.parseColor("#0000ee"))
                        continue
                    }
                    newRadioButton.isEnabled = false
                }
            }
        }
    }

}