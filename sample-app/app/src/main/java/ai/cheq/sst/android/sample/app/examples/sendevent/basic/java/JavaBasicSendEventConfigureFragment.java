package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ai.cheq.sst.android.core.Config;
import ai.cheq.sst.android.core.Sst;
import ai.cheq.sst.android.core.config.VirtualBrowser;
import ai.cheq.sst.android.core.exceptions.ConflictingModelException;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.models.DeviceModel;
import ai.cheq.sst.android.core.models.Model;
import ai.cheq.sst.android.core.models.ModelContext;
import ai.cheq.sst.android.core.models.Models;
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
                return "di_demo";
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
        public void clearDataLayer(CoroutineScope coroutineScope, Runnable onFinished) throws
                NotConfiguredException {
            Sst.getDataLayer().clear(coroutineScope)
               .whenCompleteAsync((result, throwable) -> onFinished.run(),
                                  Utils.getExecutor(coroutineScope)
               );
        }

        @Override
        public void populateDataLayer(CoroutineScope coroutineScope, Runnable onFinished) {
            Sst.getDataLayer().add("uuid", uuid, coroutineScope)
               .thenCompose((Void v) -> Sst.getDataLayer().add("string", "baz", coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("int", 456, coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("float", 789.012, coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("boolean", true, coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("date", new Date(), coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("zonedDateTime",
                                                               ZonedDateTime.now(),
                                                               coroutineScope
               )).thenCompose((Void v) -> Sst.getDataLayer().add("zonedDateTimeUTC",
                                                                 ZonedDateTime.now()
                                                                              .withZoneSameInstant(
                                                                                      ZoneOffset.UTC),
                                                                 coroutineScope
               )).thenCompose((Void v) -> Sst.getDataLayer()
                                             .add("localDate", LocalDate.now(), coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("localDateTime",
                                                               LocalDateTime.now(),
                                                               coroutineScope
               )).thenCompose((Void v) -> Sst.getDataLayer()
                                             .add("list", List.of("def", "987"), coroutineScope))
               .thenCompose((Void v) -> Sst.getDataLayer().add("map",
                                                               Map.of("z",
                                                                      "9",
                                                                      "y",
                                                                      Map.of("x", "8", "w", 7)
                                                               ),
                                                               coroutineScope
               )).thenCompose((Void v) -> Sst.getDataLayer()
                                             .add("customData", new CustomData(), coroutineScope))
               .whenCompleteAsync((result, throwable) -> onFinished.run(),
                                  Utils.getExecutor(coroutineScope)
               );
        }

        @Override
        public void isDataLayerPopulated(
                CoroutineScope coroutineScope, Consumer<Boolean> onFinished
        ) throws NotConfiguredException {
            Sst.getDataLayer().get(coroutineScope, "uuid", String.class)
               .whenCompleteAsync((s, throwable) -> onFinished.accept(uuid.equals(s)),
                                  Utils.getExecutor(coroutineScope)
               );
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
                    models = models.add(configureDeviceModel(includeDeviceModelId, includeDeviceModelOs, includeDeviceModelScreen));
                }
            } else {
                models = Models.requiredModels();
                if (includeDeviceModelId || includeDeviceModelOs || includeDeviceModelScreen) {
                    models = models.add(configureDeviceModel(includeDeviceModelId, includeDeviceModelOs, includeDeviceModelScreen));
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
                boolean includeDeviceModelScreen) {
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
    }
}
