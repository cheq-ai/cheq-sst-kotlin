package ai.cheq.sst.android.sample.app.examples.sendevent.basic;

import android.content.Context;

import java.util.function.Consumer;

import ai.cheq.sst.android.core.exceptions.DuplicateModelException;
import ai.cheq.sst.android.core.exceptions.InvalidModelException;
import ai.cheq.sst.android.core.exceptions.NotConfiguredException;
import ai.cheq.sst.android.core.models.Model;
import kotlinx.coroutines.CoroutineScope;

public interface ConfigWrapper {
    String getClientName();

    String getDomain();

    String getPublishPath();

    String getNexusHost();

    String getDataLayerName();

    boolean isDebug();

    void clearDataLayer(CoroutineScope coroutineScope, Runnable onFinished) throws
            NotConfiguredException;

    void populateDataLayer(CoroutineScope coroutineScope, Runnable onFinished);

    void isDataLayerPopulated(CoroutineScope coroutineScope, Consumer<Boolean> onFinished) throws
            InterruptedException, NotConfiguredException;

    Model<?> getStaticModel();

    void configure(
            String clientName,
            String domain,
            String publishPath,
            String nexusHost,
            String dataLayerName,
            boolean isDebug,
            Model<?>[] customModels,
            Context context
    ) throws InvalidModelException, DuplicateModelException;
}