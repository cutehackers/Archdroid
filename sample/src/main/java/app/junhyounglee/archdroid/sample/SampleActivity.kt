package app.junhyounglee.archdroid.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.IllegalArgumentException

/**
 * Archdroid (app.junhyounglee.archdroid.sample)
 *  - sample
 *  - archdroid-annotations
 *  - archdroid-compiler
 *  - archdroid
 *
 * Rule1. Basically, view doesn't care about android lifecycle events (ex. onCreate, onResume ...). View only cares
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
 * @RequireMvpView(SampleView::class)
 * class SampleActivity : MvpSampleActivityLifecycleController {
 *
 *  ...
 *
 *  @MvpCommonView
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
class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}
