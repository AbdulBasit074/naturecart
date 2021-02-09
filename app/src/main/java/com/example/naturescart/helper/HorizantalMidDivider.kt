package com.example.naturescart.helper

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizantalMidDivider(private val drawable: Drawable?) : RecyclerView.ItemDecoration() {


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val dividerLeft = parent.paddingLeft+20
         val rightPadding = parent.paddingRight+40
        val dividerRight = parent.width - rightPadding
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom: Int = dividerTop + (drawable?.intrinsicHeight ?: 0)
            drawable?.setBounds(dividerLeft , dividerTop, dividerRight , dividerBottom)
            drawable?.draw(c)
        }
    }
}