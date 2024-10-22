package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import java.util.Objects;
import java.util.concurrent.Executor;

import kotlin.coroutines.ContinuationInterceptor;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.ExecutorsKt;

class Utils {
    private Utils() {
    }

    public static Executor getExecutor(CoroutineScope coroutineScope) {
        ContinuationInterceptor continuationInterceptor = coroutineScope.getCoroutineContext()
                                                                        .get(ContinuationInterceptor.Key);
        CoroutineDispatcher coroutineDispatcher = (CoroutineDispatcher) continuationInterceptor;
        return ExecutorsKt.asExecutor(Objects.requireNonNull(coroutineDispatcher));
    }
}
