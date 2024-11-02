package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.sample.app.examples.TabFragment
import ai.cheq.sst.android.sample.app.examples.enableButton
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.yuyh.jsonviewer.library.JsonRecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import org.json.JSONObject
import kotlin.time.Duration.Companion.seconds

abstract class BasicSendEventMobileDataFragment(private val wrapper: MobileDataWrapper) :
    TabFragment(R.layout.fragment_basic_send_event_mobile_data) {
    private lateinit var refreshButton: Button
    private lateinit var jsonViewer: JsonRecyclerView
    private var refreshJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshButton = view.findViewById(R.id.refresh)
        jsonViewer = view.findViewById(R.id.mobile_data)

        refreshButton.setOnClickListener {
            refresh(true)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
        if (refreshJob == null) {
            val refreshFlow = flow {
                while (true) {
                    delay(1.seconds)
                    refresh()
                    emit(Unit)
                }
            }.cancellable()
            refreshJob = refreshFlow.launchIn(lifecycleScope)
        }
    }

    override fun onPause() {
        super.onPause()
        if (refreshJob != null) {
            refreshJob?.cancel()
            refreshJob = null
        }
    }

    private fun refresh(mutateButton: Boolean = false) {
        if (mutateButton) {
            refreshButton.enableButton = false
        }
        wrapper.getMobileData(lifecycleScope) { mobileData ->
            if (mobileData != null) {
                val sortedMobileData = mobileData.entries.sortedBy { it.key }.associate { it.key to it.value }
                jsonViewer.bindJson(JSONObject(sortedMobileData))
                jsonViewer.expandAll()
            }
            if (mutateButton) {
                refreshButton.enableButton = true
            }
        }
    }
}
