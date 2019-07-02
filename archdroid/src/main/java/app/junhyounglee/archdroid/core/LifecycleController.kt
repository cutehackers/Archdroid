package app.junhyounglee.archdroid.core

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

interface LifecycleController {

    val hostActivity: FragmentActivity

    val fragmentManager: FragmentManager
}
