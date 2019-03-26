package com.etech.kidsstuffdemo.helpers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


class InfiniteScroll(private var layoutManager: LinearLayoutManager, private var loadMore: (page: Int) -> Unit) :
    RecyclerView.OnScrollListener() {

    private var loading = true
    private var previousTotal = 0
    private var visibleThreshold = 5
    private var current_page = 1
    private var firstVisibleItem by Delegates.notNull<Int>()
    private var visibleItemCount by Delegates.notNull<Int>()
    private var totalItemCount by Delegates.notNull<Int>()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = layoutManager.itemCount
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loadMore(++current_page)
            loading = true
        }

    }

}