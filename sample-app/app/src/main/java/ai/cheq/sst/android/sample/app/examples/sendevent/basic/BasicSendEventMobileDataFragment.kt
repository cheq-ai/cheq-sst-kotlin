package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.sample.app.examples.enableButton
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
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
    Fragment(R.layout.fragment_basic_send_event_mobile_data) {
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
        wrapper.getMobileData(lifecycleScope) {
            if (it != null) {
                jsonViewer.bindJson(JSONObject(it))
                jsonViewer.expandAll()
            }
            if (mutateButton) {
                refreshButton.enableButton = true
            }
        }
    }
}

//Sst.getMobileData()