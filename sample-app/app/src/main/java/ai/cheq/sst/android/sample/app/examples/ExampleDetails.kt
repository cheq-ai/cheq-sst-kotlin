package ai.cheq.sst.android.sample.app.examples

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
class ExampleDetails(
    @StringRes val nameResource: Int,
    val clazz: Class<out TabbedFragment>,
    @DrawableRes val iconResource: Int? = null
) : Parcelable