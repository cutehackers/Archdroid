package app.junhyounglee.archroid.sample

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpFragmentView
import app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter
import app.junhyounglee.archroid.runtime.core.view.MvpView
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 *
 */
@MvpFragmentView(ProfileView::class, R.layout.fragment_profile)
@BindMvpPresenter(ProfilePresenter::class)
class ProfileFragmentView : MvpProfileFragmentView() {

    override fun showName(name: String) {
        nameView.text = name
    }

}

interface ProfileView : MvpView {
    fun showName(name: String)
}

class ProfilePresenter(viewaa: ProfileView) : MvpPresenter<ProfileView>(viewaa) {
    override fun onCreate() {
        super.onCreate()
        view.showName("Archroid!!")
    }
}