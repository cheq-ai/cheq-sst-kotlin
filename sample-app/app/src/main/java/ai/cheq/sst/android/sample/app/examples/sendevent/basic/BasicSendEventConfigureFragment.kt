package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.core.models.Model
import ai.cheq.sst.android.models.advertising.AdvertisingModel
import ai.cheq.sst.android.sample.app.Constants
import ai.cheq.sst.android.sample.app.examples.enableButton
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

abstract class BasicSendEventConfigureFragment(private val wrapper: ConfigWrapper) :
    Fragment(R.layout.fragment_basic_send_event_configure) {
    private lateinit var clientEditText: EditText
    private lateinit var domainEditText: EditText
    private lateinit var publishPathEditText: EditText
    private lateinit var nexusHostEditText: EditText
    private lateinit var dataLayerNameEditText: EditText
    private lateinit var enableDebugSwitch: SwitchCompat
    private lateinit var includeCustomModelSwitch: SwitchCompat
    private lateinit var includeAdvertisingModelSwitch: SwitchCompat
    private lateinit var populateDataLayerButton: Button
    private lateinit var clearDataLayerButton: Button

    private val advertisingModel = AdvertisingModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clientEditText = view.findViewById(R.id.client)
        domainEditText = view.findViewById(R.id.domain)
        publishPathEditText = view.findViewById(R.id.publish_path)
        nexusHostEditText = view.findViewById(R.id.nexus_host)
        dataLayerNameEditText = view.findViewById(R.id.data_layer_name)
        enableDebugSwitch = view.findViewById(R.id.enable_debug)
        includeCustomModelSwitch = view.findViewById(R.id.include_custom_model)
        includeAdvertisingModelSwitch = view.findViewById(R.id.include_advertising_model)
        populateDataLayerButton = view.findViewById(R.id.populate_data_layer)
        clearDataLayerButton = view.findViewById(R.id.clear_data_layer)

        setFields()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validate()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        clientEditText.addTextChangedListener(textWatcher)
        domainEditText.addTextChangedListener(textWatcher)
        publishPathEditText.addTextChangedListener(textWatcher)
        nexusHostEditText.addTextChangedListener(textWatcher)
        dataLayerNameEditText.addTextChangedListener(textWatcher)

        clearDataLayerButton.setOnClickListener {
            wrapper.clearDataLayer(lifecycleScope) { updateDataLayerButtons() }
        }

        populateDataLayerButton.setOnClickListener {
            wrapper.populateDataLayer(lifecycleScope) { updateDataLayerButtons() }
        }

        updateDataLayerButtons()
    }

    override fun onResume() {
        super.onResume()
        setFields()
        updateDataLayerButtons()
    }

    override fun onPause() {
        super.onPause()
        configure()
    }

    private fun updateDataLayerButtons() {
        wrapper.isDataLayerPopulated(lifecycleScope) {
            populateDataLayerButton.enableButton = !it
            clearDataLayerButton.enableButton = it
        }
    }

    private fun setFields() {
        clientEditText.setText(wrapper.clientName)
        domainEditText.setText(wrapper.domain)
        publishPathEditText.setText(wrapper.publishPath)
        nexusHostEditText.setText(wrapper.nexusHost)
        dataLayerNameEditText.setText(wrapper.dataLayerName)
        enableDebugSwitch.isChecked = wrapper.isDebug
    }

    private fun validate() {
        if (clientEditText.text.toString().isBlank()) {
            clientEditText.error = "Client is required"
        } else {
            clientEditText.error = null
        }
        if (domainEditText.text.toString().isBlank()) {
            domainEditText.error = "Domain is required"
        } else {
            domainEditText.error = null
        }
        if (publishPathEditText.text.toString().isBlank()) {
            publishPathEditText.error = "Publish path is required"
        } else {
            publishPathEditText.error = null
        }
        if (nexusHostEditText.text.toString().isBlank()) {
            nexusHostEditText.error = "Nexus host is required"
        } else {
            nexusHostEditText.error = null
        }
        if (dataLayerNameEditText.text.toString().isBlank()) {
            dataLayerNameEditText.error = "Data layer name is required"
        } else {
            dataLayerNameEditText.error = null
        }
    }

    private fun configure() {
        var customModels = arrayOf<Model<*>>()
        if (includeCustomModelSwitch.isChecked) {
            customModels += wrapper.staticModel
        }
        if (includeAdvertisingModelSwitch.isChecked) {
            customModels += advertisingModel
        }

        val defaultConfig = Constants.getDefaultSstConfig()
        wrapper.configure(clientEditText.text.toString().ifBlank {
            clientEditText.setText(defaultConfig.clientName)
            defaultConfig.clientName
        }, domainEditText.text.toString().ifBlank {
            domainEditText.setText(defaultConfig.domain)
            defaultConfig.domain
        }, publishPathEditText.text.toString().ifBlank {
            publishPathEditText.setText(defaultConfig.publishPath)
            defaultConfig.publishPath
        }, nexusHostEditText.text.toString().ifBlank {
            nexusHostEditText.setText(defaultConfig.nexusHost)
            defaultConfig.nexusHost
        }, dataLayerNameEditText.text.toString().ifBlank {
            dataLayerNameEditText.setText(defaultConfig.dataLayerName)
            defaultConfig.dataLayerName
        }, enableDebugSwitch.isChecked, customModels, requireContext()
        )
    }
}