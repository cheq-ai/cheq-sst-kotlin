package ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin

import ai.cheq.sst.android.core.Event
import ai.cheq.sst.android.core.Sst
import ai.cheq.sst.android.core.monitoring.Error
import ai.cheq.sst.android.core.monitoring.EventHandler
import ai.cheq.sst.android.core.monitoring.HttpRequest
import ai.cheq.sst.android.core.monitoring.HttpResponse
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventSendFragment
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.SendWrapper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.function.Consumer


class KotlinBasicSendEventSendFragment : BasicSendEventSendFragment(KotlinSendWrapper()) {
    class KotlinSendWrapper : SendWrapper {
        private val eventTypes: Map<String, Event> = mapOf(
            "Screen view" to Event(
                "screen_view", mapOf("screen_name" to "Home")
            ), "Custom" to Event(
                "custom_example", mapOf(
                    "string" to "foobar",
                    "int" to 123,
                    "float" to 456.789,
                    "boolean" to true,
                    "date" to Date(),
                    "zonedDateTime" to ZonedDateTime.now(),
                    "zonedDateTimeUTC" to ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
                    "localDate" to LocalDate.now(),
                    "localDateTime" to LocalDateTime.now(),
                    "list" to listOf("abc", "123"),
                    "map" to mapOf("a" to "1", "b" to mapOf("c" to "2", "d" to 3)),
                    "customData" to CustomData()
                )
            ), "Invalid data" to Event(
                "invalid_data", mapOf("invalid_data" to UnserializableClass())
            ), "Disable user tracking" to Event(
                "screen_view", mapOf("screen_name" to "Home"), mapOf("ensDisableTracking" to "user")
            )
        )

        override fun startRequestHandler(
            lifecycleOwner: LifecycleOwner, consumer: EventHandler<HttpRequest>
        ) {
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    Sst.onRequest(consumer::handle)
                }
            }
        }

        override fun startResponseHandler(
            lifecycleOwner: LifecycleOwner, consumer: EventHandler<HttpResponse>
        ) {
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    Sst.onResponse(consumer::handle)
                }
            }
        }

        override fun startErrorHandler(
            lifecycleOwner: LifecycleOwner, consumer: EventHandler<Error>
        ) {
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    Sst.onError(consumer::handle)
                }
            }
        }

        override fun getCheqUuid(coroutineScope: CoroutineScope): String? {
            return runBlocking {
                Sst.getCheqUuid()
            }
        }

        override fun clearCheqUuid(coroutineScope: CoroutineScope) {
            coroutineScope.launch {
                Sst.clearCheqUuid()
            }
        }

        override fun getClientName(): String {
            return Sst.config().clientName
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

        override fun getEventTypes(): List<String> {
            return eventTypes.keys.toMutableList()
        }

        override fun trackEvent(
            coroutineScope: CoroutineScope,
            eventType: String,
            additionalParameters: Map<String, String>,
            onSuccess: Consumer<String?>,
            onFinish: Runnable
        ) {
            val eventTemplate = eventTypes[eventType]!!
            coroutineScope.launch {
                try {
                    Sst.trackEvent(
                        Event(
                            eventTemplate.name,
                            eventTemplate.data,
                            eventTemplate.parameters + additionalParameters
                        )
                    )
                    onSuccess.accept(Sst.getCheqUuid())
                } finally {
                    onFinish.run()
                }
            }
        }
    }

    private class UnserializableClass {
        @Suppress("unused")
        val foo: String
            get() {
                error("This class is not serializable")
            }
    }
}