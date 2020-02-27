package app.junhyounglee.archroid.runtime.core

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleObserver
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * Abstract base class that generates boilerplate code regarding MVP architecture for
 * MvpActivityView annotation. archroid-compiler will generate following codes for users.
 *
 * prerequisite:
 *  [MvpView interface]
 *  interface SampleView : MvpView
 *
 *  [MvpPresenter class]
 *  class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)
 *
 * auto-generated code by archroid-compiler will be:
 *  abstract class MvpSampleActivityView
 *    : MvpActivityLifecycleController<SampleView, SamplePresenter>()
 *    , SampleView {
 *
 *    private val impl = RootViewImpl()
 *
 *    override val context: Context? = this
 *
 *    override val layoutResId: Int
 *      get() = R.layout.users_layout_name
 *
 *    override var rootView: ViewGroup
 *      get() = impl.container
 *      set(value) {
 *        impl.container = value
 *      }
 *
 *    override val isRootViewAlive: Boolean
 *      get() = impl.isViewAlive
 *
 *    override fun onCreateMvpView(): SampleView = this
 *
 *    override fun onCreateMvpPresenter() = SamplePresenter(this)
 *  }
 *
 *  Finally, actual MVP activity view  will be like this:
 *   @MvpActivityView(SampleView::class, layoutResId = R.layout.activity_sample)
 *   @BindMvpPresenter(SamplePresenter::class)
 *   class SampleActivityView : MvpSampleActivityView()
 */
abstract class MvpActivityLifecycleController<VIEW : MvpView, PRESENTER : MvpPresenter<VIEW>>
    : AppCompatActivity()
    , MvpLifecycleController<VIEW, PRESENTER> {

    override val view: VIEW by lazy {
        onCreateMvpView().also {
            // to create presenter instance right after view created
            it.presenter
        }
    }

    override val presenter: PRESENTER by lazy {
        onCreateMvpPresenter()
    }

    override val hostActivity: FragmentActivity?
        get() = this

    override val hostFragmentManager: FragmentManager?
        get() = supportFragmentManager


    abstract fun onCreateMvpView(): VIEW

    abstract fun onCreateMvpPresenter(): PRESENTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.layoutResId)
        onRootViewCreated(window.decorView.findViewById<FrameLayout>(android.R.id.content))

        if (presenter is LifecycleObserver) {
            lifecycle.addObserver(presenter as LifecycleObserver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (presenter is LifecycleObserver) {
            lifecycle.removeObserver(presenter as LifecycleObserver)
        }
    }

    private fun onRootViewCreated(container: ViewGroup) {
        view.rootView = container
    }
}
