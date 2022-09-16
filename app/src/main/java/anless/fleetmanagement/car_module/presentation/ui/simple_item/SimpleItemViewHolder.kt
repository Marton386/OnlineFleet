package anless.fleetmanagement.car_module.presentation.ui.simple_item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.car_module.domain.model.SimpleItem
import anless.fleetmanagement.databinding.ItemTitleBinding

class SimpleItemViewHolder private constructor(
    private val binding: ItemTitleBinding,
    private val onClick: (SimpleItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClick: (SimpleItem) -> Unit): SimpleItemViewHolder {
            val binding =
                ItemTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SimpleItemViewHolder(binding, onClick)
        }
    }

    private var item: SimpleItem? = null

    init {
        itemView.setOnClickListener {
            item?.let { action ->
                onClick.invoke(action)
            }
        }
    }

    fun bind(action: SimpleItem) {
        item = action

        binding.tvTitle.text = action.name
    }
}