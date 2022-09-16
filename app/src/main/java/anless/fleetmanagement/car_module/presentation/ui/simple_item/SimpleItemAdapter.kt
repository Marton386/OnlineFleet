package anless.fleetmanagement.car_module.presentation.ui.simple_item

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import anless.fleetmanagement.car_module.domain.model.SimpleItem

class SimpleItemAdapter(
    private val onClick: (SimpleItem) -> Unit
) : ListAdapter<SimpleItem, SimpleItemViewHolder>(TitleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemViewHolder {
        return SimpleItemViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: SimpleItemViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class TitleDiffCallback : DiffUtil.ItemCallback<SimpleItem>() {
        override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem.id == oldItem.id
        }
    }
}