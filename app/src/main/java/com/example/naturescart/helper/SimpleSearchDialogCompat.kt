package com.example.naturescart.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Filter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.example.naturescart.R
import ir.mirrajabi.searchdialog.adapters.SearchDialogAdapter
import ir.mirrajabi.searchdialog.core.*


class SimpleSearchDialogCompat<T : Searchable?>(
    context: Context?, title: String, searchHint: String,
    @Nullable filter: Filter?, items: ArrayList<T>?,
    searchResultListener: SearchResultListener<T>
) : BaseSearchDialogCompat<T>(context, items, filter, null, null) {
    private var title: String? = null
    private var mSearchHint: String? = null
    private var mSearchResultListener: SearchResultListener<T>? = null
    private var titleTextView: TextView? = null
    private var searchBox: EditText? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var mCancelOnTouchOutside = true

    // In case you are doing process in another thread
    // and wanted to update the progress in that thread
    private var mHandler: Handler? = null
    private fun init(title: String, searchHint: String, searchResultListener: SearchResultListener<T>) {
        this.title = title
        mSearchHint = searchHint
        mSearchResultListener = searchResultListener
        setFilterResultListener { items ->
            (adapter as SearchDialogAdapter<*>)
                .setSearchTag(searchBox!!.text.toString())
                .setItems(items)
        }
        mHandler = Handler(Looper.getMainLooper())
    }

    override fun getView(view: View) {
        setContentView(view)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(true)
        titleTextView = view.findViewById(R.id.txt_title) as TextView
        searchBox = view.findViewById(searchBoxId)
        recyclerView = view.findViewById(recyclerViewId)
        progressBar = view.findViewById(R.id.progress)
        titleTextView!!.text = title.toString()
        titleTextView!!.text = title.toString()
        titleTextView!!.text = title.toString()
        titleTextView!!.text = title.toString()
        searchBox!!.hint = mSearchHint
        progressBar!!.isIndeterminate = true
        progressBar!!.visibility = View.GONE
        view.findViewById<View>(R.id.dummy_background)
            .setOnClickListener {
                if (mCancelOnTouchOutside) {
                    dismiss()
                }
            }
        val adapter: SearchDialogAdapter<*> = SearchDialogAdapter(context, R.layout.li_search_item, items)
        adapter.searchResultListener = mSearchResultListener
        adapter.setSearchDialog(this)
        filterResultListener = filterResultListener
        setAdapter(adapter)
        searchBox!!.requestFocus()

        (filter as BaseFilter<T>).onPerformFilterListener = object : OnPerformFilterListener {
            override fun doBeforeFiltering() {
                setLoading(true)
            }

            override fun doAfterFiltering() {
                setLoading(false)
            }
        }
    }

    fun setSearchHint(searchHint: String?): SimpleSearchDialogCompat<*> {
        mSearchHint = searchHint
        if (searchBox != null) {
            mHandler?.post(Runnable { searchBox!!.hint = mSearchHint })
        }
        return this
    }

    fun setLoading(isLoading: Boolean) {
        mHandler!!.post {
            if (progressBar != null) {
                recyclerView!!.visibility = if (!isLoading) View.VISIBLE else View.GONE
            }
            if (recyclerView != null) {
                progressBar!!.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    fun setSearchResultListener(
        searchResultListener: SearchResultListener<T>?
    ): SimpleSearchDialogCompat<*> {
        mSearchResultListener = searchResultListener
        return this
    }

    @LayoutRes
    override fun getLayoutResId(): Int {
        return R.layout.search_dialog
    }

    @IdRes
    override fun getSearchBoxId(): Int {
        return R.id.txt_search
    }

    @IdRes
    override fun getRecyclerViewId(): Int {
        return R.id.rv_items
    }

    fun setTitle(title: String?): SimpleSearchDialogCompat<*> {
        this.title = title
        if (titleTextView != null) {
            mHandler!!.post { titleTextView!!.text = title }
        }
        return this
    }

    fun willCancelOnTouchOutside(): Boolean {
        return mCancelOnTouchOutside
    }

    fun setCancelOnTouchOutside(cancelOnTouchOutside: Boolean) {
        mCancelOnTouchOutside = cancelOnTouchOutside
    }

    init {
        init(title, searchHint, searchResultListener)
    }
}