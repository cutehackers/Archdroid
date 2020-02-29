package app.junhyounglee.archroid.runtime.core.presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import app.junhyounglee.archroid.runtime.core.view.MvpView

abstract class AbsMvpPresenter<VIEW : MvpView>(private val _view: VIEW) : MvpPresenter<VIEW> {

    override val view: VIEW
        get() = _view

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {

    }
}