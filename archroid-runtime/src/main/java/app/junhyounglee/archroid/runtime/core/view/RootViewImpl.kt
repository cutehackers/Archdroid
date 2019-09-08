package app.junhyounglee.archroid.runtime.core.view

import android.view.ViewGroup

/**
 * View reference implementation
 */
class RootViewImpl : RootView {

    override var container: ViewGroup? = null

    override val isViewAlive: Boolean
        get() = container != null
}