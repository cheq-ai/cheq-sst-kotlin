package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.lifecycle.RepeatOnLifecycleKt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ai.cheq.sst.android.core.Event;
import ai.cheq.sst.android.core.Sst;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.monitoring.Error;
import ai.cheq.sst.android.core.monitoring.EventHandler;
import ai.cheq.sst.android.core.monitoring.HttpRequest;
import ai.cheq.sst.android.core.monitoring.HttpResponse;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventSendFragment;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.SendWrapper;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;

public class JavaBasicSendEventSendFragment extends BasicSendEventSendFragment {
    public JavaBasicSendEventSendFragment() {
        super(new JavaSendEventWrapper());
    }

    private static class JavaSendEventWrapper implements SendWrapper {
        private final Map<String, Event> eventTypes = createEventTypes();

        @Override
        public void startRequestHandler(
                LifecycleOwner lifecycleOwner, EventHandler<HttpRequest> handler
        ) {
            startEventHandler(lifecycleOwner,
                              (coroutineScope, continuation) -> Sst.onRequest(handler, continuation)
            );
        }

        @Override
        public void startResponseHandler(
                LifecycleOwner lifecycleOwner, EventHandler<HttpResponse> handler
        ) {
            startEventHandler(lifecycleOwner,
                              (coroutineScope, continuation) -> Sst.onResponse(handler,
                                                                               continuation
                              )
            );
        }

        @Override
        public void startErrorHandler(LifecycleOwner lifecycleOwner, EventHandler<Error> handler) {
            startEventHandler(lifecycleOwner,
                              (coroutineScope, continuation) -> Sst.onError(handler, continuation)
            );
        }

        @Override
        public String getCheqUuid(CoroutineScope coroutineScope) throws InterruptedException,
                ExecutionException, NotConfiguredException {
            return Sst.getCheqUuid(coroutineScope, Dispatchers.getDefault()).get();
        }

        @Override
        public void clearCheqUuid(CoroutineScope coroutineScope) throws NotConfiguredException {
            Sst.clearCheqUuid(coroutineScope);
        }

        @Override
        public String getClientName() {
            return Sst.config().getClientName();
        }

        @Override
        public String getDomain() {
            return Sst.config().getDomain();
        }

        @Override
        public String getPublishPath() {
            return Sst.config().getPublishPath();
        }

        @Override
        public String getNexusHost() {
            return Sst.config().getNexusHost();
        }

        @Override
        public List<String> getEventTypes() {
            return List.copyOf(eventTypes.keySet());
        }

        @Override
        public void trackEvent(
                CoroutineScope coroutineScope,
                String eventType,
                Map<String, String> additionalParameters,
                Consumer<String> onSuccess,
                Runnable onFinish
        ) throws NotConfiguredException {
            Event eventTemplate = Objects.requireNonNull(eventTypes.get(eventType));
            String name = eventTemplate.getName();
            Map<String, Object> data = eventTemplate.getData();
            Map<String, String> parameters = Stream.of(eventTemplate.getParameters(),
                                                       additionalParameters
            ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,
                                                                           Map.Entry::getValue
            ));
            ai.cheq.sst.android.core.Event event = new ai.cheq.sst.android.core.Event(name,
                                                                                      data,
                                                                                      parameters
            );
            Sst.trackEvent(coroutineScope, event).thenComposeAsync((Void v) -> {
                try {
                    return Sst.getCheqUuid(coroutineScope);
                } catch (NotConfiguredException e) {
                    throw new CompletionException(e);
                }
            }).whenCompleteAsync((String cheqUuid, Throwable throwable) -> {
                if (throwable == null) {
                    onSuccess.accept(cheqUuid);
                }
                onFinish.run();
            }, Utils.getExecutor(coroutineScope));
        }

        private void startEventHandler(
                LifecycleOwner lifecycleOwner,
                Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ?> consumer
        ) {
            LifecycleCoroutineScope lifecycleScope = LifecycleOwnerKt.getLifecycleScope(
                    lifecycleOwner);
            BuildersKt.launch(lifecycleScope,
                              lifecycleScope.getCoroutineContext(),
                              CoroutineStart.DEFAULT,
                              (coroutineScope, continuation) -> RepeatOnLifecycleKt.repeatOnLifecycle(
                                      lifecycleOwner,
                                      Lifecycle.State.STARTED,
                                      consumer,
                                      continuation
                              )
            );
        }

        private static Map<String, Event> createEventTypes() {
            Map<String, Object> customData = new HashMap<>();
            customData.put("string", "foobar");
            customData.put("int", 123);
            customData.put("float", 456.789);
            customData.put("boolean", true);
            customData.put("date", new Date());
            customData.put("zonedDateTime", ZonedDateTime.now());
            customData.put("zonedDateTimeUTC",
                           ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
            );
            customData.put("localDate", LocalDate.now());
            customData.put("localDateTime", LocalDateTime.now());
            customData.put("list", List.of("abc", "123"));
            customData.put("map", Map.of("a", "1", "b", Map.of("c", "2", "d", 3)));
            customData.put("customData", new CustomData());

            Map<String, Event> eventTypes = new LinkedHashMap<>();
            eventTypes.put("Screen view", new Event("screen_view", Map.of("screen_name", "Home")));
            eventTypes.put("Custom", new Event("custom_example", customData));
            eventTypes.put("Invalid data",
                           new Event("invalid_data",
                                     Map.of("invalid_data", new UnserializableClass())
                           )
            );
            eventTypes.put("Disable user tracking", new Event("screen_view",
                                                              Map.of("screen_name", "Home"),
                                                              Map.of("ensDisableTracking", "user")
            ));

            return eventTypes;
        }

        private static class UnserializableClass {
            /**
             * @noinspection unused
             */
            public String getFoo() {
                throw new IllegalStateException("This class is not serializable");
            }
        }
    }
}
