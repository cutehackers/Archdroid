package app.junhyounglee.archroid.runtime.core.view

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * architecture base view that has android context and view.
 */
interface ArchView {

    val context: Context?

    @get:LayoutRes
    val layoutResId: Int

    var rootView: ViewGroup

    val isRootViewAlive: Boolean
}
