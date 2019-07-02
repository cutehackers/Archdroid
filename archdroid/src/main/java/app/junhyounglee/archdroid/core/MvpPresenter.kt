package app.junhyounglee.archdroid.core

abstract class MvpPresenter<VIEW : MvpView> constructor(open val view: VIEW)
