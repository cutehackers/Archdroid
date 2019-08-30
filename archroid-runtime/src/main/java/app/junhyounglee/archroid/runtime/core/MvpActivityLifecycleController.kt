package app.junhyounglee.archroid.runtime.core

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
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
 * auto-generated code will be:
 *  abstract class MvpSampleActivityView
 *    : MvpActivityLifecycleController<SampleView, SamplePresenter>()
 *    , SampleView {
 *
 *    private val impl = RootViewImpl()
 *
 *    override val context: Context?
 *      override val context: Context?
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
 *    override fun createMvpView(): SampleView = this
 *
 *    override fun onCreatePresenter() = SamplePresenter(this)
 *  }
 */
abstract class MvpActivityLifecycleController<VIEW : MvpView, PRESENTER : MvpPresenter<VIEW>>
    : AppCompatActivity()
    , MvpLifecycleController<VIEW, PRESENTER> {

    override val view: VIEW by lazy {
        createMvpView().also {
            // to create presenter instance right after view created
            it.presenter
        }
    }

    override val presenter: PRESENTER by lazy {
        onCreatePresenter()
    }

    override val hostActivity: FragmentActivity
        get() = this

    override val fragmentManager: FragmentManager
        get() = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.layoutResId)

        onRootViewCreated(window.decorView.findViewById<FrameLayout>(android.R.id.content))

        lifecycle.addObserver(presenter)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(presenter)
    }

    abstract fun createMvpView(): VIEW

    abstract fun onCreatePresenter(): PRESENTER

    @CallSuper
    open fun onRootViewCreated(container: ViewGroup) {
        view.rootView = container
    }
}
