package ai.cheq.sst.android.sample.app

import ai.cheq.sst.android.core.Config

object Constants {
    private const val CLIENT_NAME = "mobile_demo"
    const val EXAMPLES = "examples"

    fun getDefaultSstConfig() = Config(CLIENT_NAME, debug = true)
}