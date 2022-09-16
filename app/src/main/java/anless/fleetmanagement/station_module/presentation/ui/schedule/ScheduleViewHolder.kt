package anless.fleetmanagement.station_module.presentation.ui.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.utils.ActionManager
import anless.fleetmanagement.core.utils.DateFormatter
import anless.fleetmanagement.databinding.ItemScheduleBinding
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import com.google.android.material.color.MaterialColors

class ScheduleViewHolder private constructor(
    private val binding: ItemScheduleBinding,
    private val onClick: (ScheduleItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClick: (ScheduleItem) -> Unit): ScheduleViewHolder {
            val binding =
                ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ScheduleViewHolder(binding, onClick)
        }
    }

    private var item: ScheduleItem? = null

    init {
        itemView.setOnClickListener {
            item?.let { scheduleItem ->
                onClick.invoke(scheduleItem)
            }
        }
    }

    fun bind(scheduleItem: ScheduleItem) {
        item = scheduleItem

        val context = itemView.context

        val textColor = if (scheduleItem.isComplete) {
            MaterialColors.getColor(context, R.attr.text_extra_color, Color.GRAY)
        } else {
            MaterialColors.getColor(context, R.attr.text_primary_color, Color.LTGRAY)
        }

        val itemBackgroundStateColor =
            if (scheduleItem.isComplete) {
                MaterialColors.getColor(
                    context,
                    R.attr.background_extra_color,
                    Color.GRAY
                )
            } else {
                MaterialColors.getColor(
                    context,
                    R.attr.background_extra_light_color,
                    Color.LTGRAY
                )
            }

        binding.tvAction.text = context.getText(
            ActionManager.getTitleResString(scheduleItem.actionType)
        )

        if (scheduleItem.isComplete) {
            binding.tvAction.setTextColor(textColor)
        } else {
            val colorActionText = when (scheduleItem.actionType) {
                ActionManager.ActionType.PICKUP -> R.color.green
                ActionManager.ActionType.DROP_OFF -> R.color.blue
                else -> R.color.gray
            }
            binding.tvAction.setTextColor(ContextCompat.getColor(context, colorActionText))
        }

        binding.root.setBackgroundColor(itemBackgroundStateColor)

        binding.tvResNumber.text = scheduleItem.reservation.resNumber
        binding.tvResNumber.setTextColor(textColor)

        binding.tvClient.text = scheduleItem.reservation.client
        binding.tvClient.setTextColor(textColor)

        binding.tvSource.text = "${scheduleItem.reservation.source.company} - ${scheduleItem.reservation.source.source}"

        binding.tvStationTitle.text = scheduleItem.stationTitle
        binding.tvStationTitle.setTextColor(textColor)

        binding.tvDate.text = DateFormatter.formatFullTimeDate(scheduleItem.date)
        binding.tvDate.setTextColor(textColor)

        if (scheduleItem.car == null) {
            binding.tvCarNotAssigned.visibility = View.VISIBLE
            binding.tvCarNotAssigned.setTextColor(textColor)

            binding.tvCarModel.visibility = View.GONE
            binding.tvCarLicensePlate.visibility = View.GONE
        } else {
            binding.tvCarNotAssigned.visibility = View.GONE

            binding.tvCarModel.visibility = View.VISIBLE
            binding.tvCarModel.text = scheduleItem.car.model
            binding.tvCarModel.setTextColor(textColor)

            binding.tvCarLicensePlate.visibility = View.VISIBLE
            binding.tvCarLicensePlate.text = scheduleItem.car.licensePlate
            binding.tvCarLicensePlate.setTextColor(textColor)
        }


        if (scheduleItem.reservation.flightNumber == null) {
            binding.tvFlightNumber.visibility = View.GONE
            binding.tvFlightNumberTitle.visibility = View.GONE
        } else {
            binding.tvFlightNumber.text = scheduleItem.reservation.flightNumber
            binding.tvFlightNumber.visibility = View.VISIBLE
            binding.tvFlightNumberTitle.visibility = View.VISIBLE
            binding.tvFlightNumber.setTextColor(textColor)
        }
    }
}