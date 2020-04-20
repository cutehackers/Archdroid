package app.junhyounglee.archroid.runtime.core.presenter

import android.app.Activity
import android.app.Application
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import app.junhyounglee.archroid.runtime.core.view.MvpView
import java.lang.IllegalStateException

object PresenterProviders {

    private var factory: DefaultFactory? = null

    private fun ensureFactory(application: Application) {
        if (factory == null) {
            factory = DefaultFactory(application)
        }
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application ?: throw IllegalStateException("Your activity/fragment is not " +
                "yet attached to Application. You can't request Presenter before onCreate call.")
    }

    private fun checkActivity(fragment: Fragment): Activity {
        return fragment.activity ?: throw IllegalStateException("Can't create ViewModelProvider for " +
                "detached fragment")
    }

    @MainThread
    fun of(fragment: Fragment): PresenterProvider {
        ensureFactory(checkApplication(checkActivity(fragment)))
        return PresenterProvider(PresenterStores.of(fragment), factory!!)
    }

    @MainThread
    fun of(fragment: Fragment, factory: PresenterProvider.Factory): PresenterProvider {
        ensureFactory(checkApplication(checkActivity(fragment)))
        return PresenterProvider(PresenterStores.of(fragment), factory)
    }

    @MainThread
    fun of(activity: FragmentActivity): PresenterProvider {
        ensureFactory(checkApplication(activity))
        return PresenterProvider(PresenterStores.of(activity), factory!!)
    }

    @MainThread
    fun of(activity: FragmentActivity, factory: PresenterProvider.Factory): PresenterProvider {
        ensureFactory(checkApplication(activity))
        return PresenterProvider(PresenterStores.of(activity), factory)
    }

    class DefaultFactory(private val application: Application)
        : PresenterProvider.NewInstanceFactory() {
        override fun <T : MvpPresenter<out MvpView>> create(model: Class<T>): T {
            return super.create(model)
        }
    }
}