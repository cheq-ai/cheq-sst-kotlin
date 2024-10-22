package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.sample.app.examples.enableButton
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

abstract class BasicSendEventSendFragment(private val wrapper: SendWrapper) :
    Fragment(R.layout.fragment_basic_send_event_send) {
    private lateinit var sendButton: Button
    private lateinit var requestTextView: TextView
    private lateinit var responseTextView: TextView
    private lateinit var outputGeoSwitch: SwitchCompat
    private lateinit var cheqUuidClearButton: MaterialButton
    private lateinit var cheqUuidTextView: TextView
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var mapper: ObjectMapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.mapper =
            ObjectMapper().registerKotlinModule().enable(SerializationFeature.INDENT_OUTPUT)
                .setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })

        sendButton = view.findViewById(R.id.send)
        cheqUuidClearButton = view.findViewById(R.id.cheq_uuid_clear)
        requestTextView = view.findViewById(R.id.request)
        responseTextView = view.findViewById(R.id.response)
        outputGeoSwitch = view.findViewById(R.id.output_geo)

        eventTypeSpinner = view.findViewById(R.id.event_type)
        eventTypeSpinner.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, wrapper.eventTypes
        )

        cheqUuidTextView = view.findViewById(R.id.cheq_uuid)
        cheqUuidTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cheqUuidClearButton.enableButton = cheqUuidTextView.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        wrapper.startRequestHandler(viewLifecycleOwner) {
            appendResult(
                serialize(it), requestTextView
            )
        }

        wrapper.startResponseHandler(viewLifecycleOwner) {
            appendResult(
                serialize(it), responseTextView
            )
        }

        wrapper.startErrorHandler(viewLifecycleOwner) {
            responseTextView.setTextColor(
                MaterialColors.getColor(
                    requireContext(), com.google.android.material.R.attr.colorError, Color.BLACK
                )
            )
            appendResult(serialize(it), responseTextView)
        }

        sendButton.setOnClickListener {
            requestTextView.text = ""
            responseTextView.text = ""
            responseTextView.setTextColor(
                MaterialColors.getColor(
                    requireContext(), com.google.android.material.R.attr.colorOnSurface, Color.BLACK
                )
            )

            sendButton.enableButton = false
            val customParameters = mutableMapOf<String, String>()
            if (outputGeoSwitch.isChecked) {
                customParameters["output"] = "geo"
            }

            wrapper.trackEvent(lifecycleScope,
                eventTypeSpinner.selectedItem.toString(),
                customParameters,
                { cheqUuidTextView.text = it ?: "" },
                { sendButton.enableButton = isValid() })
        }

        cheqUuidClearButton.setOnClickListener {
            wrapper.clearCheqUuid(lifecycleScope)
            cheqUuidTextView.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        sendButton.enableButton = isValid()
        cheqUuidTextView.text = wrapper.getCheqUuid(lifecycleScope) ?: ""
    }

    private fun isValid(): Boolean {
        return wrapper.clientName.isNotBlank() && wrapper.domain.isNotBlank() && wrapper.publishPath.isNotBlank() && wrapper.nexusHost.isNotBlank()
    }

    private fun serialize(data: Any): String {
        return try {
            mapper.writeValueAsString(data)
        } catch (_: Exception) {
            data.toString()
        }
    }

    private fun deserialize(data: String): JsonNode = mapper.readTree(data)

    private fun appendResult(result: String, textView: TextView) {
        if (textView.text.isNotEmpty()) {
            var existingNode = deserialize(textView.text.toString())
            if (!existingNode.isArray) {
                existingNode = mapper.createArrayNode().add(existingNode)
            }
            val arrayNode = existingNode as ArrayNode
            arrayNode.add(deserialize(result))
            textView.text = serialize(arrayNode)
        } else {
            textView.text = result
        }
    }
}