package by.academy.questionnaire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.R
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.databinding.ResultInfoBinding
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.formatDate

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
            update()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResultListItemViewHolder(this,
            itemBinding = ResultInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ResultListItemViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
    private fun update() = notifyDataSetChanged().also { checkVisibilityListener.invoke(itemCount > 0) }

    class ResultListItemViewHolder(
            private val adapter: ResultListItemsAdapter,
            private val itemBinding: ResultInfoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: ResultUser) {
            val furContext = item.resultEntity.toFurContext()
            with(itemBinding) {
                root.setOnClickListener { adapter.onItemClickEvent(furContext) }
                viewTextTitle.text = item.userName
                viewTextDescription.text = item.resultEntity.dateEnd?.let { formatDate(it) } ?: ""
                viewTextRight.apply {
                    setOnClickListener { adapter.onItemCompareClicked(furContext) }
                }.also {
                    when (item.resultEntity.getId()) {
                        adapter.currentResultId -> it.text = root.context.getString(R.string.result_compare_status_current)
                        adapter.resultIdInCompare -> it.text = root.context.getString(R.string.result_compare_status_in_compare)
                        else -> it.text = root.context.getString(R.string.result_compare_status_compare)
                    }
                }
                delButton.setOnClickListener { adapter.onDeleteClickEvent(furContext) }
            }
        }
    }
}

fun ResultEntity.toFurContext() = FURContext(formId, userId, getId())

