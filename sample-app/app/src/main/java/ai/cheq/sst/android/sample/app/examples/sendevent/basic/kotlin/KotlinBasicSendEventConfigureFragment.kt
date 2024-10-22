package ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin

import ai.cheq.sst.android.core.Config
import ai.cheq.sst.android.core.Sst
import ai.cheq.sst.android.core.models.Model
import ai.cheq.sst.android.core.models.ModelContext
import ai.cheq.sst.android.core.models.Models
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventConfigureFragment
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.ConfigWrapper
import android.content.Context
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.function.Consumer

class KotlinBasicSendEventConfigureFragment :
    BasicSendEventConfigureFragment(KotlinConfigWrapper()) {
    class KotlinConfigWrapper : ConfigWrapper {
        private val uuid = "9c0877d9-253d-4a01-9a0c-dcac62a10dc1"

        override fun getClientName(): String {
            return Sst.config().clientName.ifBlank { "di_demo" }
        }

        override fun getDomain(): String {
            return Sst.config().domain
        }

        override fun getPublishPath(): String {
            return Sst.config().publishPath
        }

        override fun getNexusHost(): String {
            return Sst.config().nexusHost
        }

        override fun getDataLayerName(): String {
            return Sst.config().dataLayerName
        }

        override fun isDebug(): Boolean {
            return Sst.config().debug
        }

        override fun clearDataLayer(coroutineScope: CoroutineScope, onFinished: Runnable) {
            coroutineScope.launch {
                try {
                    Sst.dataLayer.clear()
                } finally {
                    onFinished.run()
                }
            }
        }

        override fun populateDataLayer(coroutineScope: CoroutineScope, onFinished: Runnable) {
            coroutineScope.launch {
                try {
                    Sst.dataLayer.add("uuid", uuid)
                    Sst.dataLayer.add("string", "baz")
                    Sst.dataLayer.add("int", 456)
                    Sst.dataLayer.add("float", 789.012)
                    Sst.dataLayer.add("boolean", true)
                    Sst.dataLayer.add("date", Date())
                    Sst.dataLayer.add("zonedDateTime", ZonedDateTime.now())
                    Sst.dataLayer.add(
                        "zonedDateTimeUTC", ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
                    )
                    Sst.dataLayer.add("localDate", LocalDate.now())
                    Sst.dataLayer.add("localDateTime", LocalDateTime.now())
                    Sst.dataLayer.add("list", listOf("def", "987"))
                    Sst.dataLayer.add(
                        "map", mapOf("z" to "9", "y" to mapOf("x" to "8", "w" to 7))
                    )
                    Sst.dataLayer.add("customData", CustomData())
                } finally {
                    onFinished.run()
                }
            }
        }

        override fun isDataLayerPopulated(
            coroutineScope: CoroutineScope, onFinished: Consumer<Boolean>
        ) {
            coroutineScope.launch {
                onFinished.accept(Sst.dataLayer.get<String>("uuid") == uuid)
            }
        }

        override fun getStaticModel(): Model<*> {
            return StaticModel()
        }

        override fun configure(
            clientName: String,
            domain: String,
            publishPath: String,
            nexusHost: String,
            dataLayerName: String,
            isDebug: Boolean,
            customModels: Array<Model<*>>,
            context: Context
        ) {
            val models = Models(*customModels)
            Sst.configure(
                Config(
                    clientName, domain, publishPath, nexusHost, dataLayerName, isDebug, models
                )
            ) { context }
        }
    }

    class StaticModel : Model<StaticModel.Data>(Data::class, "custom_static_model") {
        override suspend fun get(modelContext: ModelContext): Data {
            return Data()
        }

        class Data : Model.Data() {
            @JsonProperty
            @Suppress("unused")
            val foo: String = "bar"
        }
    }
}