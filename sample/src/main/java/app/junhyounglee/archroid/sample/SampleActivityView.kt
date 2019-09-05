package app.junhyounglee.archroid.sample

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpActivityView
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlinx.android.synthetic.main.activity_sample.view.*

/**
 * Archroid (app.junhyounglee.archroid.sample)
 *  - sample
 *  - archroid-annotations
 *  - archroid-compiler
 *  - archroid-runtime
 *
 * Case 1. create an Activity that will serve as a MVP view
 *
 * MvpActivityView 1. with layoutResId (automatically bound with an Activity>
 *
 * interface SampleView : MvpView {
 *  ...
 * }
 *
 * class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view) {
 *  ...
 * }
 *
 * @MvpActivityView(SampleView::class, layoutResId = R.layout.activity_sample)
 * @BindMvpPresenter(SamplePresenter::class)
 * class SampleActivityView : MvpSampleActivityView {
 *
 * }
 *
 * Case 2. Just extends MvpView interface if you don't have contract view-presenter methods yet.
 *  It is a MVP view doesn't have any methods.
 *
 * class SamplePresenter(view: MvpView) : MvpPresenter<MvpView>(view)
 *
 * @MvpActivityView(MvpView::class, layoutResId = R.layout.activity_sample)
 * @BindMvpPresenter(SamplePresenter::class)
 * class SampleActivityView : MvpSampleActivityView {
 *
 * }
 *
 * SampleViewEventListeners
 *
 * Case 3. If you want to separate MVP view from Activity, you will divide @MvpActivityView into two
 *  classes, @MvpActivityLifecycleController and @MvpView. Basically, a view doesn't care android
 *  lifecycle events (ex. onCreate, onResume ...) in this case. A view only cares visual components.
 *
 * @MvpView
 *  layoutResId: compulsory parameter of which annotation delegates MVP view to the concrete class
 *  view: optional
 *
 * @MvpView(SampleView::class, layoutResId = R.layout.activity_sample)
 * @BindMvpPresenter(SamplePresenter::class)
 * class SampleViewImpl : MvpSampleView {
 *
 * }
 *
 * @MvpActivityLifecycleController
 * @BindMvpView(SampleViewImpl::class)
 * class SampleActivityLifecycleController : MvpSampleActivityLifecycleController {
 *
 * }
 *
 * Case 4.
 *
 * @MvpView(layoutResId = R.layout.activity_sample)
 * @BindMvpPresenter(SamplePresenter::class)
 * class SampleView : MvpSampleView {
 *
 * }
 *
 * @MvpActivityLifecycleController
 * @BindMvpView(SampleView::class)
 * class SampleActivityLifecycleController : MvpSampleActivityLifecycleController {
 *
 * }
 */
@MvpActivityView(SampleView::class, R.layout.activity_sample)
@BindMvpPresenter(SamplePresenter::class)
class SampleActivityView : MvpSampleActivityView() {

    override fun say(message: String) {
        rootView.titleView.text = message
    }

}

interface SampleView : MvpView {
    fun say(message: String)
}

class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view) {

    override fun onResume() {
        super.onResume()
        view.say("Hello Archroid!")
    }
}
