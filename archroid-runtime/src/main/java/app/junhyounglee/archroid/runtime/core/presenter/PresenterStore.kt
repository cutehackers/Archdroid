package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView

class PresenterStore {

    private val map = HashMap<String, MvpPresenter<out MvpView>>()

    fun put(key: String, presenter: MvpPresenter<out MvpView>) {
        map[key]?.onClear()
        map[key] = presenter
    }

    fun get(key: String): MvpPresenter<out MvpView>? = map[key]

    fun clear() {
        for (presenter in map.values) {
            presenter.onClear()
        }
        map.clear()
    }
}