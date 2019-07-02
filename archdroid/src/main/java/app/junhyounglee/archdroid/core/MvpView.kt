package app.junhyounglee.archdroid.core

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes

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
//abstract class MvpView : ArchView() {
//
//    val presenter: MvpPresenter<out MvpView> by lazy {
//        onCreatePresenter()
//    }
//
//    abstract fun onCreatePresenter(): MvpPresenter<out MvpView>
//}
//
//class SampleView : MvpView() {
//
//    override fun onCreatePresenter(): SamplePresenter = SamplePresenter(this)
//
//    override val layoutResId: Int
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//}
//
//class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)

interface MvpView : ArchView {

    val presenter: MvpPresenter<out MvpView>
}
