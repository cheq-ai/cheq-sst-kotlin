package ai.cheq.sst.android.sample.app.examples

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class TabFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    override fun onResume() {
        super.onResume()
        this.requireView().invalidate()
        this.requireView().forceLayout()
        this.requireView().requestLayout()
    }
}