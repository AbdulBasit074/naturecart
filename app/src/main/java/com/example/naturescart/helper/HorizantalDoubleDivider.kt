package com.example.naturescart.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R

class HorizantalDoubleDivider() : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val padding = parent.context.resources.getDimension(R.dimen._12sdp).toInt()
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < 2) {
            if (itemPosition % 2 == 0) {
                outRect.left = padding
                outRect.right = padding / 2
                outRect.top = padding * 2
                outRect.bottom = padding
            } else {
                outRect.left = padding / 2
                outRect.right = padding
                outRect.top = padding * 2
                outRect.bottom = padding
            }
        } else {
            if (itemPosition % 2 == 0) {
                outRect.left = padding
                outRect.right = padding / 2
                outRect.top = padding
                outRect.bottom = padding
            } else {
                outRect.left = padding / 2
                outRect.right = padding
                outRect.top = padding
                outRect.bottom = padding
            }
        }
    }
}