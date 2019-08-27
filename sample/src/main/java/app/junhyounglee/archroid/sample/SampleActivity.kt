package app.junhyounglee.archroid.sample

import androidx.appcompat.app.AppCompatActivity
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpActivityView
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * Archroid (app.junhyounglee.archroid.sample)
 *  - sample
 *  - archroid-annotations
 *  - archroid-compiler
 *  - archroid-runtime
 *
 * Rule1. Basically, a view doesn't care about android lifecycle events (ex. onCreate, onResume ...). View only cares
 *  visual components.
 *
 * @MvpActivityView(SampleView::class)
 * @BindMvpPresenter(SamplePresenter::class)
 * class SampleActivity : MvpSampleActivityView {
 *
 * }
 *
 * SampleViewActions
 *
 * @MvpPresenter
 * @BindMvpView(SampleView::class)
 * class SamplePresenter : MvpSamplePresenter {
 *
 * }
 *
 *
 * @MvpActivityLifecycleController
 * @BindMvpView(SampleView::class)
 * class SampleActivity : MvpSampleActivityLifecycleController {
 *
 *  ...
 *
 *  @MvpDummyView
 *  @BindMvpPresenter(SamplePresenter::class)
 *  class SampleView : MvpSampleView {
 *
 *  }
 *
 * }
 *
 *
 * @RequireMvpView(SampleView::class)
 * class MvpActivityView : MvpActivityLifecycleController {
 *
 * }
 *
 */
@MvpActivityView(SampleView::class)
@BindMvpPresenter(SamplePresenter::class)
class SampleActivity : AppCompatActivity() {

}

interface SampleView : MvpView {
    fun say(message: String)
}

class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)
