package app.junhyounglee.archroid.runtime.core

import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView

interface MvpLifecycleController<VIEW: MvpView, PRESENTER : MvpPresenter<VIEW>> :
    LifecycleController {

    val view: VIEW

    val presenter: PRESENTER
}
