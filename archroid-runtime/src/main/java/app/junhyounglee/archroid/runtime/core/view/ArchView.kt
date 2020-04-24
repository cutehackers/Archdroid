package app.junhyounglee.archroid.runtime.core.view

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * architecture base view that has android context and view.
 */
interface ArchView {

    @get:LayoutRes
    val layoutResId: Int

    var container: ViewGroup

    val isContainerAlive: Boolean

    fun getContext(): Context?
}
