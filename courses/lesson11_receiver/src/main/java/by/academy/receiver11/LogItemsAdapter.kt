package by.academy.receiver11


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class LogItemsAdapter : RecyclerView.Adapter<DataItemViewHolder>() {
    var sortOrder: Boolean = true

    var items: List<LogEntry> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataItemViewHolder =
            DataItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_info, parent, false))

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun changeSort(): Boolean {
        when {
            sortOrder -> sortAsc()
            else -> sortDesc()
        }
        sortOrder = !sortOrder
        return sortOrder
    }

    fun sortAsc() {
        items = items.sortedWith(compareBy({ it.name }, { it.date }, { it.time }))
        notifyDataSetChanged()
    }

    fun sortDesc() {
        items = items.sortedWith(compareBy({ it.name }, { it.date }, { it.time })).reversed()
        notifyDataSetChanged()
    }
}

class DataItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val date: TextView = itemView.findViewById(R.id.viewTextDate)
    private val time: TextView = itemView.findViewById(R.id.viewTextTime)
    private val entry: TextView = itemView.findViewById(R.id.textView)

    fun bind(logEntry: LogEntry) {
        date.text = logEntry.date
        time.text = logEntry.time
        entry.text = logEntry.name
        itemView.setOnClickListener { sendMail(logEntry) }
    }

    private fun sendMail(logEntry: LogEntry) {
        Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_SUBJECT, logEntry.name)
            putExtra(Intent.EXTRA_TEXT, logEntry.toString())
            startActivity(itemView.context, Intent.createChooser(this, "Mail send..."), Bundle.EMPTY)
        }
    }
}
