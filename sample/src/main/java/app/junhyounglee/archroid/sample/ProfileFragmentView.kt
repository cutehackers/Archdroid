package app.junhyounglee.archroid.sample

import android.os.Bundle
import android.view.View
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpFragmentView
import app.junhyounglee.archroid.annotations.MvpPresenter
import app.junhyounglee.archroid.runtime.core.presenter.AbsMvpPresenter
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
            WelcomeDialogFragmentView.newInstance("Welcome to Android").show(this, "dialog")
        }

        fragmentManager?.let { fmtMgr ->
            fmtMgr.beginTransaction().apply {
                fmtMgr.findFragmentByTag("dialog")?.also {
                    remove(it)
                }

                addToBackStack(null)
                WelcomeDialogFragmentView.newInstance("Welcome to Android").show(this, "dialog")
            }
        }
    }
}

interface ProfileView : MvpView {
    fun showName(name: String)
}

interface IProfilePresenter {
    fun foo()
}

@MvpPresenter(ProfileView::class, IProfilePresenter::class)
class ProfilePresenter(view: ProfileView) : MvpProfilePresenter(view) {

    override fun onCreate() {
        super.onCreate()
        foo()
    }

    override fun foo() {
        view.showName("Archroid!!")
    }
}