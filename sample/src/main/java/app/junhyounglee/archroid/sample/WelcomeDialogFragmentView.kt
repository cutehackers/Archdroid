package app.junhyounglee.archroid.sample

import android.os.Bundle
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpDialogFragmentView
import app.junhyounglee.archroid.annotations.MvpPresenter
import app.junhyounglee.archroid.runtime.core.presenter.AbsMvpPresenter
import app.junhyounglee.archroid.runtime.core.presenter.PresenterProvider
import app.junhyounglee.archroid.runtime.core.presenter.PresenterProviders
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlinx.android.synthetic.main.dialog_fragment_welcome.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * Refer to DialogFragment documentation if you want some style
 * https://developer.android.com/reference/android/app/DialogFragment
 */
@MvpDialogFragmentView(WelcomeView::class, R.layout.dialog_fragment_welcome)
@BindMvpPresenter(WelcomePresenter::class, bindingNeeded = false)
class WelcomeDialogFragmentView : MvpWelcomeDialogFragmentView() {

    override fun onCreateMvpPresenter(view: WelcomeView): WelcomePresenter {
        val message = arguments?.getString(ARGS_MESSAGE) ?: ""

        val factory = WelcomePresenterFactory(this, message)
        return PresenterProviders.of(this, factory).get(WelcomePresenter::class.java, WelcomeView::class.java, view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun show(message: String?) {
        messageView.text = message
    }

    companion object {
        private const val ARGS_MESSAGE = "args_message"

        fun newInstance(message: String): WelcomeDialogFragmentView {
            val fragment = WelcomeDialogFragmentView()
            Bundle().run {
                putString(ARGS_MESSAGE, message)
                fragment.arguments = this
            }
            return fragment
        }
    }
}

interface WelcomeView : MvpView {
    fun show(message: String? = null)
}

interface IWelcomePresenter {
    fun foo()
}

class WelcomePresenterFactory(
    private val view: WelcomeView,
    private val message: String
) : PresenterProvider.Factory {

    override fun <V : MvpView, P : app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter<V>> create(
        presenterType: Class<P>,
        viewType: Class<V>,
        view: V
    ): P {
        if (presenterType.isAssignableFrom(WelcomePresenter::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WelcomePresenter(this.view, message) as P
        }
        throw IllegalArgumentException("Unknown presenter class")
    }
}

@MvpPresenter(WelcomeView::class, IWelcomePresenter::class)
class WelcomePresenter(view: WelcomeView, private val message: String) : MvpWelcomePresenter(view) {

    override fun onCreate() {
        super.onCreate()
        foo()
    }

    override fun foo() {
        view.show(message)
    }
}