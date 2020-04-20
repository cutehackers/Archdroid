package app.junhyounglee.archroid.runtime.core.presenter

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import app.junhyounglee.archroid.runtime.common.EmptyActivityLifecycleCallbacks

/**
 * Presenter holder class that stores instance to the parent fragment manager.
 */
internal class PresenterHolderFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val presenterStore = PresenterStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holderFragmentManager.holderCreated(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterStore.clear()
    }

    fun getPresenterStore() = presenterStore

    companion object {
        private const val LOG_TAG = "PresenterStores"
        const val HOLDER_TAG = "app.junhyounglee.archroid.runtime.core.presenter.PresenterHolderFragmentTag"

        private val holderFragmentManager = HolderFragmentManager()

        fun get(activity: FragmentActivity): PresenterHolderFragment {
            return holderFragmentManager.get(activity)
        }

        fun get(fragment: Fragment): PresenterHolderFragment {
            return holderFragmentManager.get(fragment)
        }
    }

    internal class HolderFragmentManager {
        private val notCommittedActivityHolders = HashMap<Activity, PresenterHolderFragment>()
        private val notCommittedFragmentHolders = HashMap<Fragment, PresenterHolderFragment>()

        private val activityCallbacks: Application.ActivityLifecycleCallbacks =
            object : EmptyActivityLifecycleCallbacks() {
                override fun onActivityDestroyed(activity: Activity) {
                    notCommittedActivityHolders.remove(activity)?.also {
                        Log.e(LOG_TAG, "Failed to save a presenter for $activity")
                    }
                }
            }

        private var isActivityCallbacksAdded = false

        private val parentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, parent: Fragment) {
                notCommittedFragmentHolders.remove(parent).also {
                    Log.e(LOG_TAG, "Failed to save a presenter for $parent")
                }
            }
        }

        internal fun holderCreated(holder: PresenterHolderFragment) {
            holder.parentFragment?.also {
                notCommittedFragmentHolders.remove(it)
                it.fragmentManager!!.unregisterFragmentLifecycleCallbacks(parentCallbacks)
            } ?: notCommittedActivityHolders.remove(holder.activity as Activity)
        }

        internal fun get(activity: FragmentActivity): PresenterHolderFragment {
            val fmtMgr = activity.supportFragmentManager

            var holder = find(fmtMgr)
            holder?.also {
                return@get it
            }

            holder = notCommittedActivityHolders[activity]
            holder?.also {
                return@get it
            }

            if (!isActivityCallbacksAdded) {
                isActivityCallbacksAdded = true
                activity.application.registerActivityLifecycleCallbacks(activityCallbacks)
            }

            holder = create(fmtMgr)
            notCommittedActivityHolders[activity] = holder
            return holder
        }

        internal fun get(parent: Fragment): PresenterHolderFragment {
            val fmtMgr = parent.childFragmentManager

            var holder = find(fmtMgr)
            holder?.also {
                return@get it
            }

            holder = notCommittedFragmentHolders[parent]
            holder?.also {
                return@get it
            }

            parent.fragmentManager!!.registerFragmentLifecycleCallbacks(parentCallbacks, false)
            holder = create(fmtMgr)
            notCommittedFragmentHolders[parent] = holder
            return holder
        }


        companion object {

            /**
             * find a holder fragment from FragmentManager
             * @return a holder fragment if FragmentManager found a fragment by HOLDER_TAG otherwise
             * null
             */
            private fun find(manager: FragmentManager): PresenterHolderFragment? {
                if (manager.isDestroyed) {
                    throw IllegalStateException("Can't access Presenters from onDestroy")
                }

                val fragmentByTag = manager.findFragmentByTag(HOLDER_TAG)
                if (fragmentByTag != null && fragmentByTag !is PresenterHolderFragment) {
                    throw IllegalStateException("Unexpected fragment instance was returned by HOLDER_TAG")
                }
                return fragmentByTag as PresenterHolderFragment?
            }

            private fun create(fm: FragmentManager): PresenterHolderFragment {
                return PresenterHolderFragment().apply {
                    fm.beginTransaction().add(this, HOLDER_TAG).commitAllowingStateLoss()
                }
            }
        }
    }

}
