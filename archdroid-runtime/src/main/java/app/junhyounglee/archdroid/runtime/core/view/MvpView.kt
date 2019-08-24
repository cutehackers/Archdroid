package app.junhyounglee.archdroid.runtime.core.view

import app.junhyounglee.archdroid.runtime.core.presenter.MvpPresenter

/**
 * ex)
 *
 * class SampleView : MvpView() {
 *  override val layoutResId: Int
 *      get() = R.layout.view_sample
 *
 *  override fun onCreatePresenter(): SamplePresenter {
 *      return SamplePresenter(this)
 *  }
 * }
 *
 * class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)
 */
interface MvpView : ArchView {

    val presenter: MvpPresenter<out MvpView>
}
