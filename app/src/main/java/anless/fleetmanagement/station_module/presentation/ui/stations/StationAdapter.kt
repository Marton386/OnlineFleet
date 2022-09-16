package anless.fleetmanagement.station_module.presentation.ui.stations

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import anless.fleetmanagement.station_module.domain.model.Station

class StationAdapter(
    private val onClick: (Station) -> Unit
) : ListAdapter<Station, StationViewHolder>(StationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.nameRu == newItem.nameRu
        }
    }
}