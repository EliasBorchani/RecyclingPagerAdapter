package com.mxco.recyclingviewpager

import android.view.View
import android.view.ViewGroup
import android.util.SparseArray
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.PagerAdapter

class RecyclingPagerAdapter(private val contentProvider: ContentProvider) : PagerAdapter() {

    interface ContentProvider {
        fun getViewModel(position: Int): ViewModel
        fun getItemViewType(position: Int): Int = 0
        fun getViewHolderForType(viewType: Int): ViewHolder<out ViewModel>
        fun getCount(): Int
        fun getTitle(position: Int): String
    }

    private val recycledViewHolders = SparseArray<MutableList<ViewHolder<out ViewModel>>>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val viewModel = contentProvider.getViewModel(position)
        val viewType = contentProvider.getItemViewType(position)
        val viewHolder = recycledViewHolders.get(viewType)?.firstOrNull()?.apply {
            recycledViewHolders.get(viewType).remove(this)
            isRecycled = false
        } ?: contentProvider.getViewHolderForType(viewType).apply { createView(container) }
        viewHolder.bindView(viewModel)
        container.addView(viewHolder.view)
        return viewHolder
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val viewHolder = any as ViewHolder<out ViewModel>
        val viewType = contentProvider.getItemViewType(position)
        recycledViewHolders.get(viewType)?.add(viewHolder) ?: recycledViewHolders.put(viewType, mutableListOf(viewHolder))
        viewHolder.isRecycled = true
        container.removeView(viewHolder.view)
    }

    override fun isViewFromObject(view: View, any: Any) = view === (any as? ViewHolder<out ViewModel>)?.view

    override fun getCount(): Int = contentProvider.getCount()

    override fun getPageTitle(position: Int) = contentProvider.getTitle(position)

    abstract class ViewHolder<T : ViewModel>(private val activityLifecycle: Lifecycle) : LifecycleOwner {

        lateinit var view: View
        internal var isRecycled = false
            set(value) {
                if (field != value) {
                    field = value
                    onRecycledStateChanged(value)
                }
            }
        @Suppress("LeakingThis")
        private val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
        private val activityLifeCycleObserver = GenericLifecycleObserver { _, event -> lifecycle.handleLifecycleEvent(event) }

        init {
            activityLifecycle.addObserver(activityLifeCycleObserver)
        }

        abstract fun onCreateView(container: ViewGroup): View
        abstract fun onBindView(viewModel: T)
        internal fun createView(container: ViewGroup) {
            view = onCreateView(container)
        }

        internal fun bindView(viewModel: ViewModel) {
            (viewModel as? T)?.let { onBindView(it) }
        }

        override fun getLifecycle(): Lifecycle = lifecycle

        private fun onRecycledStateChanged(state: Boolean) {
            if (state) {
                activityLifecycle.removeObserver(activityLifeCycleObserver)
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            } else {
                lifecycle.markState(activityLifecycle.currentState)
                activityLifecycle.addObserver(activityLifeCycleObserver)
            }
        }
    }
}
