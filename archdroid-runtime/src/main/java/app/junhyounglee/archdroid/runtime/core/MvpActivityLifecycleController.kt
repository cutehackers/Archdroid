package app.junhyounglee.archdroid.runtime.core

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import app.junhyounglee.archdroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archdroid.runtime.core.view.MvpView
import app.junhyounglee.archdroid.runtime.core.view.RootViewImpl

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

// step 1
//class MvpActivityView : MvpActivityLifecycleController<SampleView, SamplePresenter>() {
//
//    override fun createMvpView(): SampleView = SampleView()
//
//    override val presenter: SamplePresenter
//        get() = view.presenter
//}

// step 2. example
interface SampleView : MvpView

class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)

abstract class MvpSampleActivityView
    : MvpActivityLifecycleController<SampleView, SamplePresenter>()
    , SampleView {

    private val impl = RootViewImpl()

    override val context: Context?
        get() = this

    override val layoutResId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override var rootView: ViewGroup
        get() = impl.container
        set(value) {
            impl.container = value
        }

    override val isRootViewAlive: Boolean
        get() = impl.isViewAlive

    override fun createMvpView(): SampleView = this

    override fun onCreatePresenter() = SamplePresenter(this)
}
