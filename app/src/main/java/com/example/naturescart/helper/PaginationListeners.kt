package com.example.naturescart.helper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListeners(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    companion object {
        const val pageSize = 20
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalVisibleItem: Int = layoutManager.childCount
        val totalItemCount: Int = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading() && !isLastPage()) {
            if ((totalVisibleItem + firstVisibleItemPosition) >= totalItemCount && totalItemCount >= pageSize) {
                loadMoreItems()
            }

        }

    }

    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean
    abstract fun loadMoreItems()
}