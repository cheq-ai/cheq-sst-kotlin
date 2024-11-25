package ai.cheq.sst.android.sample.app.examples.sendevent.basic;

import android.content.Context;

import java.util.function.Consumer;

import ai.cheq.sst.android.core.exceptions.ConflictingModelException;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.models.Model;
import kotlinx.coroutines.CoroutineScope;

public interface ConfigWrapper {
    String getClientName();

    String getDomain();

    String getPublishPath();

    String getNexusHost();

    String getDataLayerName();

    String getVirtualBrowserPage();

    String getUserAgent();

    boolean isDebug();

    Storage dataLayer();

    Storage cookies();

    Storage localStorage();

    Storage sessionStorage();

    Model<?> getStaticModel();

    void configure(
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
    ) throws ConflictingModelException;

    interface Storage {
        void clear(CoroutineScope coroutineScope, Runnable onFinished) throws
                NotConfiguredException;

        void populate(CoroutineScope coroutineScope, Runnable onFinished) throws
                NotConfiguredException;

        void isPopulated(CoroutineScope coroutineScope, Consumer<Boolean> onFinished) throws
                InterruptedException, NotConfiguredException;
    }
}