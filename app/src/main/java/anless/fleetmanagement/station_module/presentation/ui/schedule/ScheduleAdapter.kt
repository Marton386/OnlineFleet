package anless.fleetmanagement.station_module.presentation.ui.schedule

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import anless.fleetmanagement.station_module.domain.model.ScheduleItem

class ScheduleAdapter(
    private val onClick: (ScheduleItem) -> Unit
): ListAdapter<ScheduleItem, ScheduleViewHolder> (ScheduleItemDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class ScheduleItemDiffCallback: DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem.reservation.resNumber == newItem.reservation.resNumber
        }

        override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem.reservation.resNumber == newItem.reservation.resNumber
        }
    }
}