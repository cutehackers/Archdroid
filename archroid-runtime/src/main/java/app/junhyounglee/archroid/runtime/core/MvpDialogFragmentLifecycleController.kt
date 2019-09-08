package app.junhyounglee.archroid.runtime.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * Abstract base class that is used to generate fragment extended view using MvpDialogFragmentView
 * annotation. archroid-compiler will generate following codes for users.
 *
 * prerequisite:
 *  [MvpView interface]
 *  interface SampleView : MvpView
 *
 *  [MvpPresenter class]
 *  class SamplePresenter(view: SampleView) : MvpPresenter<SampleView>(view)
 *
 * auto-generated code by archroid-compiler will be:
 *  abstract class MvpSampleDialogFragmentView
 *      : MvpDialogFragmentLifecycleController<MvpView, MvpPresenter<MvpView>>()
 *      , MvpView {
 *
 *      private val impl = RootViewImpl()
 *
 *      override var rootView: ViewGroup
 *        get() = if (impl.isViewAlive) {
 *          impl.container!!
 *        } else {
 *          throw NullPointerException("Root content view is null!")
 *        }
 *        set(value) {
 *          impl.container = value
 *        }
 *
 *       override val isRootViewAlive: Boolean
 *         get() = impl.isViewAlive
 *
 *       override val layoutResId: Int
 *         get() = R.layout.dialog_fragment_sample
 *
 *       override fun getContext(): Context? = this
 *
 *       override fun onCreateMvpView(): SampleView = this
 *
 *       override fun onCreateMvpPresenter(): SamplePresenter = SamplePresenter(this)
 * }
 */
abstract class MvpDialogFragmentLifecycleController<VIEW : MvpView, PRESENTER : MvpPresenter<VIEW>>
    : DialogFragment()
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
        get() = activity

    override val hostFragmentManager: FragmentManager?
        get() = activity?.supportFragmentManager


    abstract fun onCreateMvpView(): VIEW

    abstract fun onCreateMvpPresenter(): PRESENTER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (view.layoutResId != 0) {
            inflater.inflate(view.layoutResId, container, false).also {
                onRootViewCreated(it as ViewGroup)
            }
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(presenter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycle.addObserver(presenter)
    }

    private fun onRootViewCreated(container: ViewGroup) {
        view.rootView = container
    }
}
