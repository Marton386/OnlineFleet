package anless.fleetmanagement.station_module.presentation.ui.stations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.databinding.ItemStationBinding
import anless.fleetmanagement.station_module.domain.model.Station
import java.util.*

class StationViewHolder private constructor(
    private val binding: ItemStationBinding,
    private val onClick: (Station) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClick: (Station) -> Unit): StationViewHolder {
            val binding = ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StationViewHolder(binding, onClick)
        }
    }

    private var item: Station? = null

    init {
        itemView.setOnClickListener {
            item?.let { station ->
                onClick.invoke(station)
            }
        }
    }

    fun bind(station: Station) {
        item = station
        binding.tvStationName.text = if (Locale.getDefault().language == "ru") {
             station.nameRu
        } else {
            station.nameEn
        }
    }
}