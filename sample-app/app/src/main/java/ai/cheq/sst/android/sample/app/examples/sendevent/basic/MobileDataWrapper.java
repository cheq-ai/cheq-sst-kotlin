package ai.cheq.sst.android.sample.app.examples.sendevent.basic;

import java.util.Map;
import java.util.function.Consumer;

import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import kotlinx.coroutines.CoroutineScope;

public interface MobileDataWrapper {
    void getMobileData(CoroutineScope coroutineScope, Consumer<Map<String, ?>> onFinished) throws
            NotConfiguredException;
}
