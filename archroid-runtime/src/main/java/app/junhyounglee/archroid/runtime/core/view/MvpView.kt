package app.junhyounglee.archroid.runtime.core.view

import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter

/**
 * MVP view base class that has presenter
 */
interface MvpView : ArchView {

    val presenter: MvpPresenter<out MvpView>
}
