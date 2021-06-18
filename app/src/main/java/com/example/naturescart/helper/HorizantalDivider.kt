package com.example.naturescart.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R

class HorizantalDivider() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val padding = parent.context.resources.getDimension(R.dimen._8sdp).toInt()
        when (itemPosition) {
            0 -> {
                outRect.right = padding / 3
                outRect.left = padding
            }
            state.itemCount - 1 -> {
                outRect.left = padding / 4
                outRect.right = padding
            }
            else -> {
                outRect.left = padding / 4
                outRect.right = padding / 4
            }
        }
        outRect.top = padding / 3
        outRect.bottom = padding

    }
}