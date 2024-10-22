package ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin

import ai.cheq.sst.android.core.Sst
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventMobileDataFragment
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.MobileDataWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.function.Consumer

class KotlinBasicSendEventMobileDataFragment :
    BasicSendEventMobileDataFragment(KotlinMobileDataWrapper()) {
    class KotlinMobileDataWrapper : MobileDataWrapper {
        override fun getMobileData(
            coroutineScope: CoroutineScope, onFinished: Consumer<Map<String, *>?>
        ) {
            coroutineScope.launch {
                onFinished.accept(Sst.getMobileData())
            }
        }
    }
}