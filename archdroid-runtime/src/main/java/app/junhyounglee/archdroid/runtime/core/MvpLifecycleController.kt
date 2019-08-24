package app.junhyounglee.archdroid.runtime.core

import app.junhyounglee.archdroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archdroid.runtime.core.view.MvpView

interface MvpLifecycleController<VIEW: MvpView, PRESENTER : MvpPresenter<VIEW>> :
    LifecycleController {

    val view: VIEW

    val presenter: PRESENTER
}
