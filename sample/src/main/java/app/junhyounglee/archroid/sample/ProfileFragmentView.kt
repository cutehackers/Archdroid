package app.junhyounglee.archroid.sample

import android.os.Bundle
import android.view.View
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpFragmentView
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlinx.android.synthetic.main.fragment_profile.*

@MvpFragmentView(ProfileView::class, R.layout.fragment_profile)
@BindMvpPresenter(ProfilePresenter::class)
class ProfileFragmentView : MvpProfileFragmentView() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runDialogView.setOnClickListener { onRunDialogClick() }
    }

    override fun showName(name: String) {
        nameView.text = name
    }

    private fun onRunDialogClick() {
        fragmentManager?.beginTransaction()?.apply {
            addToBackStack(null)
            WelcomeDialogFragmentView().show(this, "dialog")
        }

        fragmentManager?.let { fmtMgr ->
            fmtMgr.beginTransaction().apply {
                fmtMgr.findFragmentByTag("dialog")?.also {
                    remove(it)
                }

                addToBackStack(null)
                WelcomeDialogFragmentView().show(this, "dialog")
            }
        }
    }

}

interface ProfileView : MvpView {
    fun showName(name: String)
}

class ProfilePresenter(view: ProfileView) : MvpPresenter<ProfileView>(view) {
    override fun onCreate() {
        super.onCreate()
        view.showName("Archroid!!")
    }
}