package ai.cheq.sst.android.sample.app.examples

import android.graphics.Point
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.button.MaterialButton

var Button.enableButton: Boolean
    get() {
        return isEnabled
    }
    set(value) {
        isEnabled = value
        isClickable = value
    }

var MaterialButton.enableButton: Boolean
    get() {
        return isEnabled
    }
    set(value) {
        isEnabled = value
        isClickable = value
    }

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

val WindowManager.width: Int
    get() = when (SDK_INT) {
        in Build.VERSION_CODES.R..Int.MAX_VALUE ->
            currentWindowMetrics.bounds.width()

        else -> {
            val size = Point()
            @Suppress("DEPRECATION") defaultDisplay.getSize(size)
            size.x
        }
    }