package ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin

import ai.cheq.sst.android.core.Config
import ai.cheq.sst.android.core.Sst
import ai.cheq.sst.android.core.config.VirtualBrowser
import ai.cheq.sst.android.core.models.DeviceModel
import ai.cheq.sst.android.core.models.Model
import ai.cheq.sst.android.core.models.ModelContext
import ai.cheq.sst.android.core.models.Models
import ai.cheq.sst.android.core.storage.Cookie
import ai.cheq.sst.android.core.storage.Identifiable
import ai.cheq.sst.android.core.storage.Item
import ai.cheq.sst.android.core.storage.StorageCollection
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventConfigureFragment
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.ConfigWrapper
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.ConfigWrapper.Storage
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
            return Sst.config().clientName.ifBlank { "mobile_demo" }
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

        override fun getVirtualBrowserPage(): String? {
            return Sst.config().virtualBrowser.page
        }

        override fun getUserAgent(): String? {
            return Sst.config().virtualBrowser.userAgent
        }

        override fun isDebug(): Boolean {
            return Sst.config().debug
        }

        override fun dataLayer(): Storage {
            return object : Storage {
                override fun clear(coroutineScope: CoroutineScope, onFinished: Runnable) {
                    coroutineScope.launch {
                        try {
                            Sst.dataLayer.clear()
                        } finally {
                            onFinished.run()
                        }
                    }
                }

                override fun populate(coroutineScope: CoroutineScope, onFinished: Runnable) {
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
                                "zonedDateTimeUTC",
                                ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
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

                override fun isPopulated(
                    coroutineScope: CoroutineScope, onFinished: Consumer<Boolean>
                ) {
                    coroutineScope.launch {
                        onFinished.accept(Sst.dataLayer.get<String>("uuid") == uuid)
                    }
                }
            }
        }

        override fun cookies() = StorageImpl(
            "cookie", { Sst.storage.cookies }, ::Cookie, mapOf(
                "MOBILE_DEMO_ENSIGHTEN_PRIVACY_BANNER_VIEWED" to "1",
                "MOBILE_DEMO_ENSIGHTEN_PRIVACY_BANNER_LOADED" to "1",
                "MOBILE_DEMO_ENSIGHTEN_PRIVACY_Analytics" to "0"
            )
        )

        override fun localStorage() = StorageImpl("local", { Sst.storage.local }, ::Item)

        override fun sessionStorage() = StorageImpl("session", { Sst.storage.session }, ::Item)

        override fun getStaticModel(): Model<*> {
            return StaticModel()
        }

        override fun configure(
            clientName: String,
            domain: String,
            publishPath: String,
            nexusHost: String,
            dataLayerName: String,
            virtualBrowserPage: String,
            userAgent: String?,
            isDebug: Boolean,
            includeDefaultModels: Boolean,
            includeDeviceModelId: Boolean,
            includeDeviceModelOs: Boolean,
            includeDeviceModelScreen: Boolean,
            customModels: Array<Model<*>>,
            context: Context
        ) {
            val models = (if (includeDefaultModels) {
                Models.default().also {
                    if (!includeDeviceModelId || !includeDeviceModelOs || !includeDeviceModelScreen) {
                        it.add(
                            configureDeviceModel(
                                includeDeviceModelId, includeDeviceModelOs, includeDeviceModelScreen
                            )
                        )
                    }
                }
            } else {
                Models.required().also {
                    if (includeDeviceModelId || includeDeviceModelOs || includeDeviceModelScreen) {
                        it.add(
                            configureDeviceModel(
                                includeDeviceModelId, includeDeviceModelOs, includeDeviceModelScreen
                            )
                        )
                    }
                }
            }).add(*customModels)
            Sst.configure(
                Config(
                    clientName,
                    domain,
                    publishPath,
                    nexusHost,
                    dataLayerName,
                    VirtualBrowser(virtualBrowserPage, userAgent),
                    models,
                    isDebug
                )
            ) { context }
        }

        private fun configureDeviceModel(
            includeDeviceModelId: Boolean,
            includeDeviceModelOs: Boolean,
            includeDeviceModelScreen: Boolean
        ): DeviceModel {
            if (includeDeviceModelId && includeDeviceModelOs && includeDeviceModelScreen) {
                return DeviceModel.default()
            }
            return DeviceModel.custom().apply {
                if (!includeDeviceModelId) {
                    disableId()
                }
                if (!includeDeviceModelOs) {
                    disableOs()
                }
                if (!includeDeviceModelScreen) {
                    disableScreen()
                }
            }.create()
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

    class StorageImpl<T : Identifiable>(
        private val type: String,
        private val storage: () -> StorageCollection<T>,
        private val itemCreator: (String, String) -> T,
        private val items: Map<String, String> = mapOf(
            prefix(type, "StorageKey1") to prefix(type, "StorageValue1"),
            prefix(type, "StorageKey2") to prefix(type, "StorageValue2")
        )
    ) : Storage {
        override fun clear(coroutineScope: CoroutineScope, onFinished: Runnable) {
            coroutineScope.launch {
                try {
                    storage().clear()
                } finally {
                    onFinished.run()
                }
            }
        }

        override fun populate(coroutineScope: CoroutineScope, onFinished: Runnable) {
            coroutineScope.launch {
                try {
                    items.forEach {
                        storage().add(itemCreator(it.key, it.value))
                    }
                } finally {
                    onFinished.run()
                }
            }
        }

        override fun isPopulated(coroutineScope: CoroutineScope, onFinished: Consumer<Boolean>) {
            coroutineScope.launch {
                onFinished.accept(storage().get(items.entries.first().key) != null)
            }
        }

        companion object {
            private fun prefix(type: String, key: String): String {
                return "${type}${key}"
            }
        }
    }
}