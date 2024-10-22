package ai.cheq.sst.android.sample.app

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.core.Sst
import ai.cheq.sst.android.sample.app.Constants.EXAMPLES
import ai.cheq.sst.android.sample.app.Constants.getDefaultSstConfig
import ai.cheq.sst.android.sample.app.examples.ExampleActivity
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.java.JavaBasicSendEventExampleFragment
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.kotlin.KotlinBasicSendEventExampleFragment
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Sst.configure(getDefaultSstConfig()) { this }
        lifecycleScope.launch {
            Sst.dataLayer.get<Int>(getString(R.string.launch_count))?.let {
                Sst.dataLayer.add(getString(R.string.launch_count), it + 1)
            } ?: Sst.dataLayer.add(getString(R.string.launch_count), 1)
        }

        val intent = Intent(this, ExampleActivity::class.java)
        val examples = arrayListOf(
            KotlinBasicSendEventExampleFragment.getExampleDetails(),
            JavaBasicSendEventExampleFragment.getExampleDetails()
        )
        intent.putParcelableArrayListExtra(EXAMPLES, examples)

        startActivity(intent)
        finish()
    }
}