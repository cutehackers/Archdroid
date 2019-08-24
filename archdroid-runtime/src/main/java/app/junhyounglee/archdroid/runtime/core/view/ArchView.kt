package app.junhyounglee.archdroid.runtime.core.view

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner

/**
 * architecture base view that has android context and view.
 */
interface ArchView : LifecycleOwner {

    val context: Context?

    @get:LayoutRes
    val layoutResId: Int

    var rootView: ViewGroup

    val isRootViewAlive: Boolean
}
