/*
 * Copyright (c) Pulkit Sharma
 * Android Developer
 * pulkit.sharma88548@gmail.com
 * 7014586715
 */
package common.neighbour.nearhud.base

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseViewModel<N> : ViewModel() {
    private var mNavigator: WeakReference<N>? = null



    var navigator: N
        get() = mNavigator!!.get()!!
        set(Navigator) {
            mNavigator = WeakReference<N>(Navigator)
        }


}