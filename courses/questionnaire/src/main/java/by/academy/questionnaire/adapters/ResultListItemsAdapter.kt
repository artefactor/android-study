package by.academy.questionnaire.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
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
        context: Context,

        ) : RecyclerView.Adapter<ResultListItemsAdapter.ResultListItemViewHolder>() {

    var currentResultId: Long = 1
    var resultIdInCompare: Long = -1
    var usersColorMap: MutableMap<Long, Int> = mutableMapOf()

    private val colorSelectedUser = ContextCompat.getColor(context, R.color.selected)

    private val colors = arrayOf(
            ContextCompat.getColor(context, R.color.line1),
            ContextCompat.getColor(context, R.color.line2),
            ContextCompat.getColor(context, R.color.line3),
            ContextCompat.getColor(context, R.color.line4),
    )

    var allItems: List<ResultUser> = emptyList()
    var items: List<ResultUser> = emptyList()
        set(value) {
            field = value
            var i = 0
            value.map { r -> r.resultEntity.userId }.toSet().forEach { usersColorMap[it] = colors[(i++) % colors.size] }
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

                val color: Int = adapter.usersColorMap[furContext.userId]!!
                ColorStateList.valueOf(color).also { userColor ->
                    root.backgroundTintList = userColor
                    delButton.backgroundTintList = userColor
                }
                viewTextTitle.text = item.userName
                viewTextDescription.text = item.resultEntity.dateEnd?.let { formatDate(it) } ?: ""

                viewTextRight.apply {
                    paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    setOnClickListener { adapter.onItemCompareClicked(furContext) }
                    when (item.resultEntity.getId()) {
                        adapter.currentResultId -> {
                            this.text = root.context.getString(R.string.result_compare_status_current)
                            visualizeCurrentItem()
                        }
                        adapter.resultIdInCompare -> this.text = root.context.getString(R.string.result_compare_status_in_compare)
                        else -> this.text = root.context.getString(R.string.result_compare_status_compare)
                    }
                }
                delButton.setOnClickListener { adapter.onDeleteClickEvent(furContext) }
            }
        }

        private fun visualizeCurrentItem() {
            with(itemBinding) {
                root.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.placeholder))
                ColorStateList.valueOf(adapter.colorSelectedUser).also { color ->
                    root.backgroundTintList = color
                    delButton.backgroundTintList = color
                }
            }

        }
    }
}

fun ResultEntity.toFurContext() = FURContext(formId, userId, getId())

