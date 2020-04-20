package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 *
 * Activity, Fragment
 * -------------------------
 * | FragmentManager       |
 * |                       |
 * |  ------------------   |
 * |  | HolderFragment | --|--->  PresenterStore <Key, Presenter>
 * |  ------------------   |
 * |------------------------
 *
 * Child Fragment
 * -------------------------
 * | ChildFragmentManager  |
 * |                       |
 * |  ------------------   |
 * |  | HolderFragment | --|--->  PresenterStore <Key, Presenter>
 * |  ------------------   |
 * |------------------------
 */
interface MvpPresenter<VIEW : MvpView> {

    val view: VIEW

    fun onClear()
}