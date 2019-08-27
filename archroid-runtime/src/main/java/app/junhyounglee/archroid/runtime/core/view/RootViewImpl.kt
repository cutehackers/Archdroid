package app.junhyounglee.archroid.runtime.core.view

import android.view.ViewGroup

/**
 * View reference implementation
 */
class RootViewImpl : RootView {

    override lateinit var container: ViewGroup

    override val isViewAlive: Boolean
        get() = this::container.isInitialized
}