package ai.cheq.sst.android.sample.app.examples

import androidx.annotation.StringRes

interface Titled {
    fun setTitle(@StringRes titleResource: Int)
}