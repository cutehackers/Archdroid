package app.junhyounglee.archroid.runtime.core.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object PresenterStores {

    fun of(activity: FragmentActivity): PresenterStore {
        return PresenterHolderFragment.get(activity).getPresenterStore()
    }

    fun of(fragment: Fragment): PresenterStore {
        return PresenterHolderFragment.get(fragment).getPresenterStore()
    }
}