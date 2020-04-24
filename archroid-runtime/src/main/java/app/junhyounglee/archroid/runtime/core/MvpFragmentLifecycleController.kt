package app.junhyounglee.archroid.runtime.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleObserver
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * Abstract base class that is used to generate fragment extended view using MvpFragmentView
 * annotation. archroid-compiler will generate following codes for users.
 */
abstract class MvpFragmentLifecycleController<VIEW : MvpView, PRESENTER : MvpPresenter<VIEW>>
    : Fragment()
    , MvpLifecycleController<VIEW, PRESENTER> {

    private lateinit var _presenter: PRESENTER

    override val view: VIEW by lazy {
        onCreateMvpView().also {
            _presenter = onCreateMvpPresenter(it)
        }
    }

    override val presenter: PRESENTER
        get() = if (this::_presenter.isInitialized) {
            _presenter
        } else {
            throw IllegalStateException("Presenter is not initialized yet!")
        }

    override val hostActivity: FragmentActivity?
        get() = activity

    override val hostFragmentManager: FragmentManager?
        get() = activity?.supportFragmentManager


    abstract fun onCreateMvpView(): VIEW

    abstract fun onCreateMvpPresenter(view: VIEW): PRESENTER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(view.layoutResId, container, false).also {
            onRootViewCreated(it as ViewGroup)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (presenter is LifecycleObserver) {
            lifecycle.removeObserver(presenter as LifecycleObserver)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (presenter is LifecycleObserver) {
            lifecycle.addObserver(presenter as LifecycleObserver)
        }
    }

    private fun onRootViewCreated(container: ViewGroup) {
        view.container = container
    }
}