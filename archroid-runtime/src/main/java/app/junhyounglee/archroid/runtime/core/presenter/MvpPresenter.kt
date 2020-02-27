package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView

interface MvpPresenter<VIEW : MvpView> {
    val view: VIEW
}