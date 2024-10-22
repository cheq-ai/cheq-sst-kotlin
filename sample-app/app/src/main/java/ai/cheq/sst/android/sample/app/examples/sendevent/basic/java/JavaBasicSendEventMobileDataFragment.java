package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import java.util.Map;
import java.util.function.Consumer;

import ai.cheq.sst.android.core.Sst;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventMobileDataFragment;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.MobileDataWrapper;
import kotlinx.coroutines.CoroutineScope;

public class JavaBasicSendEventMobileDataFragment extends BasicSendEventMobileDataFragment {
    public JavaBasicSendEventMobileDataFragment() {
        super(new JavaMobileDataWrapper());
    }

    private static class JavaMobileDataWrapper implements MobileDataWrapper {
        @Override
        public void getMobileData(
                CoroutineScope coroutineScope, Consumer<Map<String, ?>> onFinished
        ) throws NotConfiguredException {
            Sst.getMobileData(coroutineScope).whenCompleteAsync(
                    (mobileData, throwable) -> onFinished.accept(mobileData),
                    Utils.getExecutor(coroutineScope)
            );
        }
    }
}
