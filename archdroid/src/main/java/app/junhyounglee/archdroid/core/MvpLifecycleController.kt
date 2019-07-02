package app.junhyounglee.archdroid.core

interface MvpLifecycleController<VIEW: MvpView, PRESENTER : MvpPresenter<VIEW>> : LifecycleController {

    val view: VIEW

    val presenter: PRESENTER
}
