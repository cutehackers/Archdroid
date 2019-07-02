package app.junhyounglee.archdroid.core

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * architecture base view that has android context and view.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
//abstract class ArchView : LifecycleObserver {
//
//    val context: Context?
//        get() = if (isRootViewAlive) {
//            rootView.context
//        } else {
//            null
//        }
//
//    @get:LayoutRes
//    abstract val layoutResId: Int
//
//    open lateinit var rootView: ViewGroup
//
//    val isRootViewAlive: Boolean
//        get() = this::rootView.isInitialized
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    open fun onCreate() {
//
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    open fun onStart() {
//
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    open fun onResume() {
//
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    open fun onPause() {
//
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    open fun onStop() {
//
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    open fun onDestroy() {
//
//    }
//}

interface ArchView : LifecycleObserver {

    val context: Context?

    @get:LayoutRes
    val layoutResId: Int

    var rootView: ViewGroup
}

interface RootView {

    var container: ViewGroup

    val isViewAlive: Boolean
}

class RootViewImpl : RootView {

    override lateinit var container: ViewGroup

    override val isViewAlive: Boolean
        get() = this::container.isInitialized
}
