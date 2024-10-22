package ai.cheq.sst.android.sample.app.examples

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
