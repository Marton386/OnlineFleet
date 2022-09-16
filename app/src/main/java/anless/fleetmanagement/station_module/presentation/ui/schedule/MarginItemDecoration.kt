package anless.fleetmanagement.station_module.presentation.ui.schedule

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.R

class MarginItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val margin = context.resources.getDimensionPixelSize(R.dimen.gapMedium)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position > 0) {
            outRect.top = 0
        } else {
            outRect.top = margin
        }

        outRect.left = margin
        outRect.right = margin
        outRect.bottom = margin
    }

}