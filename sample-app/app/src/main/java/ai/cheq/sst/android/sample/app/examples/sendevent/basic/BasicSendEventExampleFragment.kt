package ai.cheq.sst.android.sample.app.examples.sendevent.basic

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.sample.app.examples.TabbedFragment

abstract class BasicSendEventExampleFragment(
    configureFragment: BasicSendEventConfigureFragment,
    mobileDataFragment: BasicSendEventMobileDataFragment,
    sendFragment: BasicSendEventSendFragment
) : TabbedFragment(
    R.layout.fragment_basic_send_event_example,
    R.id.pager,
    R.id.tab_layout,
    Tab(R.string.configure) { configureFragment },
    Tab(R.string.mobile_data) { mobileDataFragment },
    Tab(R.string.send_event, primary = true) { sendFragment })