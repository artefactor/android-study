package by.academy.questionnaire

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.databinding.ResultInfoBinding


class ResultListItemsAdapter(
        private val checkVisibilityListener: (Boolean) -> Unit,
        private val onItemClickEvent: (Long) -> Unit,
        private val onItemCompareClicked: (Long) -> Unit,

        ) : RecyclerView.Adapter<ResultListItemsAdapter.ResultListItemViewHolder>() {

    var currentUserId: Long = 1
    var userIdInCompere: Long = -1

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

                viewTextRight.setOnClickListener {
                    adapter.onItemCompareClicked(item.resultEntity.userId)
                }
                when (item.resultEntity.userId) {
                    adapter.currentUserId -> viewTextRight.text = "Текущий"
                    adapter.userIdInCompere -> viewTextRight.text = "В сравнении"
                    else -> viewTextRight.text = "Сравнить"
                }

                root.setOnClickListener {
                    adapter.onItemClickEvent(item.resultEntity.userId)
                }
            }
        }
    }

}