package com.example.naturescart.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R

class HorizantalDoubleDivider() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val padding = parent.context.resources.getDimension(R.dimen._8sdp).toInt()
        outRect.left = padding
        outRect.left = padding
        outRect.top = padding
        outRect.bottom = padding

    }
}