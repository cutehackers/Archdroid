package app.junhyounglee.archdroid.sample

import androidx.appcompat.app.AppCompatActivity
import app.junhyounglee.archdroid.annotations.MvpActivityView
import app.junhyounglee.archdroid.runtime.core.view.MvpView

/**
 * Archdroid (app.junhyounglee.archdroid.sample)
 *  - sample
 *  - archdroid-annotations
 *  - archdroid-compiler
 *  - archdroid
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
class SampleActivity : AppCompatActivity() {

}

interface SampleView : MvpView {
    fun say(message: String)
}
