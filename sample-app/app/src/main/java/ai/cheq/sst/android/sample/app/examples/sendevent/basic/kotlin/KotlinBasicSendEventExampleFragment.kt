package ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.sample.app.examples.ExampleDetails
import ai.cheq.sst.android.sample.app.examples.Titled
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventExampleFragment

class KotlinBasicSendEventExampleFragment : BasicSendEventExampleFragment(
    KotlinBasicSendEventConfigureFragment(),
    KotlinBasicSendEventMobileDataFragment(),
    KotlinBasicSendEventSendFragment()
) {
    override fun onResume() {
        super.onResume()
        (activity as Titled?)?.setTitle(getExampleDetails().nameResource)
    }

    companion object {
        fun getExampleDetails() = ExampleDetails(
            R.string.kotlin_basic_send_event, KotlinBasicSendEventExampleFragment::class.java
        )
    }
}