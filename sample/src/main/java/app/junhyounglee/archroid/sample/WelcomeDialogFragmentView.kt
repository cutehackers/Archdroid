package app.junhyounglee.archroid.sample

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpDialogFragmentView
import app.junhyounglee.archroid.annotations.MvpPresenter
import app.junhyounglee.archroid.runtime.core.presenter.AbsMvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlinx.android.synthetic.main.dialog_fragment_welcome.*

/**
 * Refer to DialogFragment documentation if you want some style
 * https://developer.android.com/reference/android/app/DialogFragment
 */
@MvpDialogFragmentView(WelcomeView::class, R.layout.dialog_fragment_welcome)
@BindMvpPresenter(WelcomePresenter::class)
class WelcomeDialogFragmentView : MvpWelcomeDialogFragmentView() {

    override fun show(message: String) {
        messageView.text = message
    }

}

interface WelcomeView : MvpView {
    fun show(message: String)
}

interface IWelcomePresenter {
    fun foo()
}

@MvpPresenter(WelcomeView::class, IWelcomePresenter::class)
class WelcomePresenter(view: WelcomeView) : MvpWelcomePresenter(view) {

    override fun onCreate() {
        super.onCreate()
        foo()
    }

    override fun foo() {
        view.show("Welcome to Archroid")
    }
}