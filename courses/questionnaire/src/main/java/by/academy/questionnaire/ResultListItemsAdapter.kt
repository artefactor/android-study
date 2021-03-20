package by.academy.questionnaire

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.databinding.ResultInfoBinding
import by.academy.questionnaire.domain.FURContext
import java.text.SimpleDateFormat
import java.util.*


class ResultListItemsAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (FURContext) -> Unit,
        private val onItemCompareClicked: (FURContext) -> Unit,
        private val onDeleteClickEvent: (FURContext) -> Unit,

        ) : RecyclerView.Adapter<ResultListItemsAdapter.ResultListItemViewHolder>() {

    var currentResultId: Long = 1
    var resultIdInCompare: Long = -1

    var allItems: List<ResultUser> = emptyList()
    var items: List<ResultUser> = emptyList()
        set(value) {
            field = value
            checkVisibility()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ResultListItemViewHolder(this@ResultListItemsAdapter,
                    itemBinding = ResultInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

    override fun onBindViewHolder(holder: ResultListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

//    fun filter(text: String?) {
//        items = allItems.filter { t -> t.title.toLowerCase().contains(text?.toLowerCase().toString()) }
//        notifyDataSetChanged()
//        checkVisibility()
//    }

    private fun checkVisibility() {
        checkVisibilityListener.invoke(itemCount > 0)
    }

    //todo rename and organize view, move strings
    class ResultListItemViewHolder(
            private val adapter: ResultListItemsAdapter,
            private val itemBinding: ResultInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: ResultUser) {
            with(itemBinding) {
                viewTextTitle.text = item.userName

                val furContext = FURContext(
                        item.resultEntity.formId,
                        item.resultEntity.userId,
                        item.resultEntity.getId(),
                )
                viewTextRight.setOnClickListener {
                    adapter.onItemCompareClicked(furContext)
                }
                when (item.resultEntity.getId()) {
                    adapter.currentResultId -> viewTextRight.text = "Текущий"
                    adapter.resultIdInCompare -> viewTextRight.text = "В сравнении"
                    else -> viewTextRight.text = "Сравнить"
                }
                val timeStamp = SimpleDateFormat("yyyy-MM-dd hh:mm").format(item.resultEntity.dateEnd)
                viewTextDescription.text = timeStamp
                delButton.setOnClickListener { adapter.onDeleteClickEvent(furContext)}
                root.setOnClickListener { adapter.onItemClickEvent(furContext) }
            }
        }
    }

}