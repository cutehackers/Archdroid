package app.junhyounglee.archroid.runtime.core

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

interface LifecycleController : LifecycleOwner {

    val hostActivity: FragmentActivity

    val fragmentManager: FragmentManager
}
