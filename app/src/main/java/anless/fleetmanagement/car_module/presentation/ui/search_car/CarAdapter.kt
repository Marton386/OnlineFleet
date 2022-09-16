package anless.fleetmanagement.car_module.presentation.ui.search_car

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import anless.fleetmanagement.car_module.domain.model.CarItem

class CarAdapter(
    private val onClick: (CarItem) -> Unit
) : ListAdapter<CarItem, CarViewHolder>(CarDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        return CarViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class CarDiffCallback : DiffUtil.ItemCallback<CarItem>() {
        override fun areItemsTheSame(oldItem: CarItem, newItem: CarItem): Boolean {
            return oldItem.carInfo.id == oldItem.carInfo.id
        }

        override fun areContentsTheSame(oldItem: CarItem, newItem: CarItem): Boolean {
            return oldItem.carInfo.model == newItem.carInfo.model
        }
    }
}