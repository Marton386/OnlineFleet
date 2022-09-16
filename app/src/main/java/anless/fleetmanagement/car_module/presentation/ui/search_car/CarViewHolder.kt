package anless.fleetmanagement.car_module.presentation.ui.search_car

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.databinding.ItemCarBinding

class CarViewHolder private constructor(
    private val binding: ItemCarBinding,
    private val onClick: (CarItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClick: (CarItem) -> Unit): CarViewHolder {
            val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CarViewHolder(binding, onClick)
        }
    }

    private var item: CarItem? = null

    init {
        itemView.setOnClickListener {
            item?.let { car ->
                onClick.invoke(car)
            }
        }
    }

    fun bind(car: CarItem) {
        item = car

        binding.tvCarLicensePlate.text = car.carInfo.licensePlate
        binding.tvCarName.text = car.carInfo.model
        binding.tvStationCode.text = car.stationInfo.codeStation
    }
}