package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * This is to create a presenter that has arguments.
 * ex)
 *   class SamplePresenterFactory(private val repository: DataRepository) : MvpPresenterFactory<SampleView, SamplePresenter> {
 *      override fun create(view: SampleView): SamplePresenter {
 *          return SamplePresenter(view, repository)
 *      }
 *   }
 *
 *   class SampleView : MvpView
 *
 *   class SamplePresenter(view: SampleView) : AbsMvpPresenter<SampleView>(view)
 */
interface MvpPresenterFactory<VIEW : MvpView, T : MvpPresenter<VIEW>> {
    fun create(view: VIEW): T
}
