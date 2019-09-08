package app.junhyounglee.archroid.runtime.core.view

import android.view.ViewGroup

/**
 * View reference
 */
interface RootView {

    var container: ViewGroup?

    val isViewAlive: Boolean
}