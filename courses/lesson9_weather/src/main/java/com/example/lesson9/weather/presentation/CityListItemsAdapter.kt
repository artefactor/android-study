package com.example.lesson9.weather.presentation

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson9.weather.databinding.ItemCityInfoBinding

class CityListItemsAdapter(
        private val onCityClickEvent: (Long) -> Unit,
) : RecyclerView.Adapter<CityListItemsAdapter.CityListItemViewHolder>() {

    var selectedId: Long = -1
    var lastPosition: Int = -1
    var items: List<CityItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CityListItemViewHolder(
                    this@CityListItemsAdapter,
                    itemCityBinding = ItemCityInfoBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    ),
                    onCityClickEvent = onCityClickEvent
            )

    override fun onBindViewHolder(holder: CityListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class CityListItemViewHolder(
            private val adapter: CityListItemsAdapter,
            private val itemCityBinding: ItemCityInfoBinding,
            private val onCityClickEvent: (Long) -> Unit,
    ) : RecyclerView.ViewHolder(itemCityBinding.root) {

        fun bind(item: CityItem) {
            with(itemCityBinding) {
                textViewCityName.text = "${item.name} ${item.country}"
                if (item.id == adapter.selectedId) {
                    imgChecked.visibility = VISIBLE
                    adapter.lastPosition = adapterPosition
                } else {
                    imgChecked.visibility = INVISIBLE
                }

                root.setOnClickListener {
                    imgChecked.visibility = VISIBLE
                    if (adapter.lastPosition != adapterPosition) {
                        adapter.notifyItemChanged(adapter.lastPosition)
                        adapter.lastPosition = adapterPosition
                        adapter.selectedId = item.id
                        onCityClickEvent(item.id)
                    }
                }
            }
        }
    }

}