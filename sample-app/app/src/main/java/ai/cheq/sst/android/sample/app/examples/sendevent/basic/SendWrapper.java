package ai.cheq.sst.android.sample.app.examples.sendevent.basic;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.monitoring.Error;
import ai.cheq.sst.android.core.monitoring.EventHandler;
import ai.cheq.sst.android.core.monitoring.HttpRequest;
import ai.cheq.sst.android.core.monitoring.HttpResponse;
import kotlinx.coroutines.CoroutineScope;

public interface SendWrapper {
    void startRequestHandler(LifecycleOwner lifecycleOwner, EventHandler<HttpRequest> handler);

    void startResponseHandler(LifecycleOwner lifecycleOwner, EventHandler<HttpResponse> handler);

    void startErrorHandler(LifecycleOwner lifecycleOwner, EventHandler<Error> handler);

    String getCheqUuid(CoroutineScope coroutineScope) throws InterruptedException,
            ExecutionException, NotConfiguredException;

    void clearCheqUuid(CoroutineScope coroutineScope) throws NotConfiguredException;

    String getClientName();

    String getDomain();

    String getPublishPath();

    String getNexusHost();

    List<String> getEventTypes();

    void trackEvent(
            CoroutineScope coroutineScope,
            String eventType,
            Map<String, String> additionalParameters,
            Consumer<String> onSuccess,
            Runnable onFinish
    ) throws NotConfiguredException;

}
