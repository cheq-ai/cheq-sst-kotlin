package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.core.models.Model
import ai.cheq.sst.android.models.advertising.AdvertisingModel
import ai.cheq.sst.android.sample.app.Constants
import ai.cheq.sst.android.sample.app.examples.TabFragment
import ai.cheq.sst.android.sample.app.examples.enableButton
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import java.util.function.Consumer

abstract class BasicSendEventConfigureFragment(private val wrapper: ConfigWrapper) :
    TabFragment(R.layout.fragment_basic_send_event_configure) {
    private lateinit var clientEditText: EditText
    private lateinit var domainEditText: EditText
    private lateinit var publishPathEditText: EditText
    private lateinit var nexusHostEditText: EditText
    private lateinit var dataLayerNameEditText: EditText
    private lateinit var virtualBrowserPageEditText: EditText
    private lateinit var userAgentEditText: EditText
    private lateinit var enableDebugSwitch: SwitchCompat
    private lateinit var includeDefaultModelsSwitch: SwitchCompat
    private lateinit var includeDeviceModelIdSwitch: SwitchCompat
    private lateinit var includeDeviceModelOsSwitch: SwitchCompat
    private lateinit var includeDeviceModelScreenSwitch: SwitchCompat
    private lateinit var includeCustomModelSwitch: SwitchCompat
    private lateinit var includeAdvertisingModelSwitch: SwitchCompat
    private lateinit var dataLayerButtons: StorageButtons
    private lateinit var storageCookiesButtons: StorageButtons
    private lateinit var storageSessionButtons: StorageButtons
    private lateinit var storageLocalButtons: StorageButtons

    private val advertisingModel = AdvertisingModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clientEditText = view.findViewById(R.id.client)
        domainEditText = view.findViewById(R.id.domain)
        publishPathEditText = view.findViewById(R.id.publish_path)
        nexusHostEditText = view.findViewById(R.id.nexus_host)
        dataLayerNameEditText = view.findViewById(R.id.data_layer_name)
        virtualBrowserPageEditText = view.findViewById(R.id.virtual_browser_page)
        userAgentEditText = view.findViewById(R.id.user_agent)
        enableDebugSwitch = view.findViewById(R.id.enable_debug)
        includeDefaultModelsSwitch = view.findViewById(R.id.include_default_models)
        includeDeviceModelIdSwitch = view.findViewById(R.id.include_device_model_id)
        includeDeviceModelOsSwitch = view.findViewById(R.id.include_device_model_os)
        includeDeviceModelScreenSwitch = view.findViewById(R.id.include_device_model_screen)
        includeCustomModelSwitch = view.findViewById(R.id.include_custom_model)
        includeAdvertisingModelSwitch = view.findViewById(R.id.include_advertising_model)
        dataLayerButtons = StorageButtons(
            R.id.populate_data_layer,
            R.id.clear_data_layer,
            { coroutineScope, onFinished -> wrapper.dataLayer().populate(coroutineScope, onFinished) },
            { coroutineScope, onFinished -> wrapper.dataLayer().clear(coroutineScope, onFinished) },
            { coroutineScope, consumer -> wrapper.dataLayer().isPopulated(coroutineScope, consumer) },
            view,
            lifecycleScope
        )
        storageCookiesButtons = StorageButtons(
            R.id.populate_storage_cookies,
            R.id.clear_storage_cookies,
            { coroutineScope, onFinished -> wrapper.cookies().populate(coroutineScope, onFinished) },
            { coroutineScope, onFinished -> wrapper.cookies().clear(coroutineScope, onFinished) },
            { coroutineScope, consumer -> wrapper.cookies().isPopulated(coroutineScope, consumer) },
            view,
            lifecycleScope
        )
        storageSessionButtons = StorageButtons(
            R.id.populate_storage_session,
            R.id.clear_storage_session,
            { coroutineScope, onFinished -> wrapper.sessionStorage().populate(coroutineScope, onFinished) },
            { coroutineScope, onFinished -> wrapper.sessionStorage().clear(coroutineScope, onFinished) },
            { coroutineScope, consumer -> wrapper.sessionStorage().isPopulated(coroutineScope, consumer) },
            view,
            lifecycleScope
        )
        storageLocalButtons = StorageButtons(
            R.id.populate_storage_local,
            R.id.clear_storage_local,
            { coroutineScope, onFinished -> wrapper.localStorage().populate(coroutineScope, onFinished) },
            { coroutineScope, onFinished -> wrapper.localStorage().clear(coroutineScope, onFinished) },
            { coroutineScope, consumer -> wrapper.localStorage().isPopulated(coroutineScope, consumer) },
            view,
            lifecycleScope
        )

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
        virtualBrowserPageEditText.addTextChangedListener(textWatcher)
        userAgentEditText.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        setFields()
        dataLayerButtons.updateButtons(lifecycleScope)
        storageCookiesButtons.updateButtons(lifecycleScope)
        storageSessionButtons.updateButtons(lifecycleScope)
        storageLocalButtons.updateButtons(lifecycleScope)
    }

    override fun onPause() {
        super.onPause()
        configure()
    }

    private fun setFields() {
        clientEditText.setText(wrapper.clientName)
        domainEditText.setText(wrapper.domain)
        publishPathEditText.setText(wrapper.publishPath)
        nexusHostEditText.setText(wrapper.nexusHost)
        dataLayerNameEditText.setText(wrapper.dataLayerName)
        virtualBrowserPageEditText.setText(wrapper.virtualBrowserPage)
        userAgentEditText.setText(wrapper.userAgent)
        enableDebugSwitch.isChecked = wrapper.isDebug
    }

    private fun validate() {
        if (validateClient()) {
            clientEditText.error = null
        } else {
            clientEditText.error = "Client is required"
        }
        if (validateDomain()) {
            domainEditText.error = null
        } else {
            domainEditText.error = "Domain is required"
        }
        if (validatePublishPath()) {
            publishPathEditText.error = null
        } else {
            publishPathEditText.error = "Publish path is required"
        }
        if (validateNexusHost()) {
            nexusHostEditText.error = null
        } else {
            nexusHostEditText.error = "Nexus host is required"
        }
        if (validateDataLayerName()) {
            dataLayerNameEditText.error = null
        } else {
            dataLayerNameEditText.error = "Data layer name is required"
        }
        if (validateVirtualBrowserPage()) {
            virtualBrowserPageEditText.error = null
        } else {
            virtualBrowserPageEditText.error =
                "Virtual browser page must be a valid URL or not specified"
        }
    }

    private fun validateClient() = clientEditText.text.toString().isNotBlank()

    private fun validateDomain() = domainEditText.text.toString().isNotBlank()

    private fun validatePublishPath() = publishPathEditText.text.toString().isNotBlank()

    private fun validateNexusHost() = nexusHostEditText.text.toString().isNotBlank()

    private fun validateDataLayerName() = dataLayerNameEditText.text.toString().isNotBlank()

    private fun validateVirtualBrowserPage() =
        virtualBrowserPageEditText.text.toString().isEmpty() || (URLUtil.isValidUrl(
            virtualBrowserPageEditText.text.toString()
        ) && Patterns.WEB_URL.matcher(virtualBrowserPageEditText.text.toString()).matches())

    private fun configure() {
        var customModels = arrayOf<Model<*>>()
        if (includeCustomModelSwitch.isChecked) {
            customModels += wrapper.staticModel
        }
        if (includeAdvertisingModelSwitch.isChecked) {
            customModels += advertisingModel
        }

        val defaultConfig = Constants.getDefaultSstConfig()
        val client =
            validateClient().let { if (it) clientEditText.text.toString() else defaultConfig.clientName }
        val domain =
            validateDomain().let { if (it) domainEditText.text.toString() else defaultConfig.domain }
        val publishPath =
            validatePublishPath().let { if (it) publishPathEditText.text.toString() else defaultConfig.publishPath }
        val nexusHost =
            validateNexusHost().let { if (it) nexusHostEditText.text.toString() else defaultConfig.nexusHost }
        val dataLayerName =
            validateDataLayerName().let { if (it) dataLayerNameEditText.text.toString() else defaultConfig.dataLayerName }
        val virtualBrowserPage =
            validateVirtualBrowserPage().let { if (it) virtualBrowserPageEditText.text.toString() else defaultConfig.virtualBrowser.page }
        val userAgent = userAgentEditText.text.toString().ifBlank { null }

        wrapper.configure(
            client,
            domain,
            publishPath,
            nexusHost,
            dataLayerName,
            virtualBrowserPage,
            userAgent,
            enableDebugSwitch.isChecked,
            includeDefaultModelsSwitch.isChecked,
            includeDeviceModelIdSwitch.isChecked,
            includeDeviceModelOsSwitch.isChecked,
            includeDeviceModelScreenSwitch.isChecked,
            customModels,
            requireContext()
        )
    }

    class StorageButtons(
        @IdRes private val populateButtonId: Int,
        @IdRes private val clearButtonId: Int,
        private val populateFunc: (CoroutineScope, Runnable) -> Unit,
        private val clearFunc: (CoroutineScope, Runnable) -> Unit,
        private val isPopulatedFunc: (CoroutineScope, Consumer<Boolean>) -> Unit,
        view: View,
        coroutineScope: CoroutineScope
    ) {
        private val populateButton: Button = view.findViewById(populateButtonId)
        private val clearButton: Button = view.findViewById(clearButtonId)

        init {
            populateButton.setOnClickListener {
                populateFunc(coroutineScope) { updateButtons(coroutineScope) }
            }

            clearButton.setOnClickListener {
                clearFunc(coroutineScope) { updateButtons(coroutineScope) }
            }

            updateButtons(coroutineScope)
        }

        fun updateButtons(coroutineScope: CoroutineScope) {
            isPopulatedFunc(coroutineScope) {
                populateButton.enableButton = !it
                clearButton.enableButton = it
            }
        }
    }
}