package jwernikowski.pam_lab.ui.activity.song_details

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.u7416aupw
class DragCallback(
    val onFinishDrag: () -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = recyclerView.adapter
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition
        adapter?.notifyItemMoved(from, to)

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) onFinishDrag()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

}