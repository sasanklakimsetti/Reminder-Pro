package com.sasank.reminderpro

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TimeZoneTouchHelperCallback(
    private val adapter: TimeZoneAdapter
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    0
) {
    override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.moveItem(vh.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
}
