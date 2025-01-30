package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ai.cheq.sst.android.core.Config;
import ai.cheq.sst.android.core.Sst;
import ai.cheq.sst.android.core.config.VirtualBrowser;
import ai.cheq.sst.android.core.exceptions.ConflictingModelException;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.models.DeviceModel;
import ai.cheq.sst.android.core.models.Model;
import ai.cheq.sst.android.core.models.ModelContext;
import ai.cheq.sst.android.core.models.Models;
import ai.cheq.sst.android.core.storage.Cookie;
import ai.cheq.sst.android.core.storage.Identifiable;
import ai.cheq.sst.android.core.storage.Item;
import ai.cheq.sst.android.core.storage.StorageCollection;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventConfigureFragment;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.ConfigWrapper;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.CoroutineScope;

public class JavaBasicSendEventConfigureFragment extends BasicSendEventConfigureFragment {
    public JavaBasicSendEventConfigureFragment() {
        super(new JavaConfigWrapper());
    }

    private static class JavaConfigWrapper implements ConfigWrapper {
        private final String uuid = "9c0877d9-253d-4a01-9a0c-dcac62a10dc1";

        @Override
        public String getClientName() {
            String clientName = Sst.config().getClientName();
            if (clientName.trim().isEmpty()) {
                return "mobile_demo";
            }
            return clientName;
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
        public String getDataLayerName() {
            return Sst.config().getDataLayerName();
        }

        @Override
        public String getVirtualBrowserPage() {
            return Sst.config().getVirtualBrowser().getPage();
        }

        @Override
        public String getUserAgent() {
            return Sst.config().getVirtualBrowser().getUserAgent();
        }

        @Override
        public boolean isDebug() {
            return Sst.config().getDebug();
        }

        @Override
        public Storage dataLayer() {
            return new Storage() {
                @Override
                public void clear(CoroutineScope coroutineScope, Runnable onFinished) throws
                        NotConfiguredException {
                    Sst.getDataLayer().clear(coroutineScope)
                       .whenCompleteAsync((result, throwable) -> onFinished.run(),
                                          Utils.getExecutor(coroutineScope)
                       );
                }

                @Override
                public void populate(CoroutineScope coroutineScope, Runnable onFinished) throws
                        NotConfiguredException {
                    Sst.getDataLayer().add(coroutineScope, "uuid", uuid)
                       .thenCompose((Void v) -> add(coroutineScope, "string", "baz"))
                       .thenCompose((Void v) -> add(coroutineScope, "int", 456))
                       .thenCompose((Void v) -> add(coroutineScope, "float", 789.012))
                       .thenCompose((Void v) -> add(coroutineScope, "boolean", true))
                       .thenCompose((Void v) -> add(coroutineScope, "date", new Date()))
                       .thenCompose((Void v) -> add(coroutineScope,
                                                    "zonedDateTime",
                                                    ZonedDateTime.now()
                       )).thenCompose((Void v) -> add(coroutineScope,
                                                      "zonedDateTimeUTC",
                                                      ZonedDateTime.now()
                                                                   .withZoneSameInstant(ZoneOffset.UTC)
                       )).thenCompose((Void v) -> add(coroutineScope, "localDate", LocalDate.now()))
                       .thenCompose((Void v) -> add(coroutineScope,
                                                    "localDateTime",
                                                    LocalDateTime.now()
                       ))
                       .thenCompose((Void v) -> add(coroutineScope, "list", List.of("def", "987")))
                       .thenCompose((Void v) -> add(coroutineScope,
                                                    "map",
                                                    Map.of("z", "9", "y", Map.of("x", "8", "w", 7))
                       ))
                       .thenCompose((Void v) -> add(coroutineScope, "customData", new CustomData()))
                       .whenCompleteAsync((result, throwable) -> onFinished.run(),
                                          Utils.getExecutor(coroutineScope)
                       );
                }

                @Override
                public void isPopulated(
                        CoroutineScope coroutineScope, Consumer<Boolean> onFinished
                ) throws NotConfiguredException {
                    Sst.getDataLayer().get(coroutineScope, "uuid", String.class)
                       .whenCompleteAsync((s, throwable) -> onFinished.accept(uuid.equals(s)),
                                          Utils.getExecutor(coroutineScope)
                       );
                }

                @NonNull
                private <T> CompletableFuture<Void> add(
                        CoroutineScope coroutineScope, String key, T value
                ) {
                    try {
                        return Sst.getDataLayer().add(coroutineScope, key, value);
                    } catch (NotConfiguredException e) {
                        throw new CompletionException(e);
                    }
                }
            };
        }

        @Override
        public Storage cookies() {
            return new StorageImpl<>(() -> Sst.getStorage().getCookies(), Cookie::new,
                                     Map.of("MOBILE_DEMO_ENSIGHTEN_PRIVACY_BANNER_VIEWED", "1",
                                            "MOBILE_DEMO_ENSIGHTEN_PRIVACY_BANNER_LOADED", "1",
                                            "MOBILE_DEMO_ENSIGHTEN_PRIVACY_Analytics", "0"));
        }

        @Override
        public Storage localStorage() {
            return new StorageImpl<>("local", () -> Sst.getStorage().getLocal(), Item::new);
        }

        @Override
        public Storage sessionStorage() {
            return new StorageImpl<>("session", () -> Sst.getStorage().getSession(), Item::new);
        }

        @Override
        public Model<?> getStaticModel() {
            return new StaticModel();
        }

        @Override
        public void configure(
                String clientName,
                String domain,
                String publishPath,
                String nexusHost,
                String dataLayerName,
                String virtualBrowserPage,
                String userAgent,
                boolean isDebug,
                boolean includeDefaultModels,
                boolean includeDeviceModelId,
                boolean includeDeviceModelOs,
                boolean includeDeviceModelScreen,
                Model<?>[] customModels,
                Context context
        ) throws ConflictingModelException {
            Models models;
            if (includeDefaultModels) {
                models = Models.defaultModels();
                if (!includeDeviceModelId || !includeDeviceModelOs || !includeDeviceModelScreen) {
                    models = models.add(configureDeviceModel(includeDeviceModelId,
                                                             includeDeviceModelOs,
                                                             includeDeviceModelScreen
                    ));
                }
            } else {
                models = Models.requiredModels();
                if (includeDeviceModelId || includeDeviceModelOs || includeDeviceModelScreen) {
                    models = models.add(configureDeviceModel(includeDeviceModelId,
                                                             includeDeviceModelOs,
                                                             includeDeviceModelScreen
                    ));
                }
            }

            models.add(customModels);
            Config config = new Config(clientName,
                                       domain,
                                       publishPath,
                                       nexusHost,
                                       dataLayerName,
                                       new VirtualBrowser(virtualBrowserPage, userAgent),
                                       models,
                                       isDebug
            );
            Sst.configure(config, () -> context);
        }

        private static DeviceModel configureDeviceModel(
                boolean includeDeviceModelId,
                boolean includeDeviceModelOs,
                boolean includeDeviceModelScreen
        ) {
            if (includeDeviceModelId && includeDeviceModelOs && includeDeviceModelScreen) {
                return DeviceModel.defaultModel();
            }
            DeviceModel.Config config = DeviceModel.customModel();
            if (!includeDeviceModelId) {
                config.disableId();
            }
            if (!includeDeviceModelOs) {
                config.disableOs();
            }
            if (!includeDeviceModelScreen) {
                config.disableScreen();
            }
            return config.create();
        }

        static class StaticModel extends Model<StaticModel.Data> {
            public StaticModel() {
                super(Data.class, "custom_static_model", "1.0.0");
            }

            @Nullable
            @Override
            public Data get(
                    @NonNull ModelContext modelContext,
                    @NonNull Continuation<? super Data> completion
            ) {
                return new Data();
            }

            static class Data extends Model.Data {
                /**
                 * @noinspection unused
                 */
                @JsonProperty
                public String getFoo() {
                    return "bar";
                }
            }
        }

        private static class StorageImpl<T extends Identifiable> implements Storage {
            private final Supplier<StorageCollection<T>> storageLayerSupplier;
            private final BiFunction<String, String, T> valueCreator;
            private final Map<String, String> items;

            private StorageImpl(
                    String type,
                    Supplier<StorageCollection<T>> storageLayerSupplier,
                    BiFunction<String, String, T> valueCreator
            ) {
                this(storageLayerSupplier, valueCreator, Map.of(prefixed(type, "StorageKey1"),
                                                                prefixed(type, "StorageValue1"),
                                                                prefixed(type, "StorageKey2"),
                                                                prefixed(type, "StorageValue2")
                ));
            }

            private StorageImpl(
                    Supplier<StorageCollection<T>> storageLayerSupplier,
                    BiFunction<String, String, T> valueCreator,
                    Map<String, String> items
            ) {
                this.storageLayerSupplier = storageLayerSupplier;
                this.valueCreator = valueCreator;
                this.items = items;
            }

            @Override
            public void clear(CoroutineScope coroutineScope, Runnable onFinished) throws
                    NotConfiguredException {
                storageLayerSupplier.get().clear(coroutineScope)
                                    .whenCompleteAsync((result, throwable) -> onFinished.run(),
                                                       Utils.getExecutor(coroutineScope)
                                    );
            }

            @Override
            public void populate(CoroutineScope coroutineScope, Runnable onFinished) {
                List<Map.Entry<String, String>> entries = new ArrayList<>(items.entrySet());
                CompletableFuture<Void> completableFuture = storageLayerSupplier.get().add(
                        coroutineScope,
                        valueCreator.apply(entries.get(0).getKey(), entries.get(0).getValue())
                );
                for (int i = 1; i < entries.size(); i++) {
                    Map.Entry<String, String> entry = entries.get(i);
                    completableFuture = completableFuture.thenCompose((Void v) -> storageLayerSupplier.get()
                                                                                                      .add(
                                                                                                              coroutineScope,
                                                                                                              valueCreator.apply(
                                                                                                                      entry.getKey(),
                                                                                                                      entry.getValue()
                                                                                                              )
                                                                                                      ));
                }
                completableFuture.whenCompleteAsync((result, throwable) -> onFinished.run(),
                                                    Utils.getExecutor(coroutineScope)
                );
            }

            @Override
            public void isPopulated(
                    CoroutineScope coroutineScope, Consumer<Boolean> onFinished
            ) throws NotConfiguredException {
                storageLayerSupplier.get().get(
                        coroutineScope,
                        new ArrayList<>(items.entrySet()).get(0).getKey()
                ).whenCompleteAsync(
                        (s, throwable) -> onFinished.accept(s != null),
                        Utils.getExecutor(coroutineScope)
                );
            }

            private static String prefixed(String type, String value) {
                return type + value;
            }
        }
    }
}
