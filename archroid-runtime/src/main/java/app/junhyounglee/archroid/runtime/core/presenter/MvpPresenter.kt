package app.junhyounglee.archroid.runtime.core.presenter

import androidx.lifecycle.LifecycleObserver
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlin.reflect.KClass

interface MvpPresenter<VIEW : MvpView> : LifecycleObserver {
    val view: VIEW
}