package ai.cheq.sst.android.sample.app.examples.sendevent.basic.java;

import ai.cheq.sst.android.app.R;
import ai.cheq.sst.android.sample.app.examples.ExampleDetails;
import ai.cheq.sst.android.sample.app.examples.Titled;
import ai.cheq.sst.android.sample.app.examples.sendevent.basic.BasicSendEventExampleFragment;

public class JavaBasicSendEventExampleFragment extends BasicSendEventExampleFragment {
    public JavaBasicSendEventExampleFragment() {
        super(new JavaBasicSendEventConfigureFragment(),
              new JavaBasicSendEventMobileDataFragment(),
              new JavaBasicSendEventSendFragment()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() instanceof Titled) {
            Titled titled = (Titled) this.getActivity();
            titled.setTitle(getExampleDetails().getNameResource());
        }
    }

    public static ExampleDetails getExampleDetails() {
        return new ExampleDetails(
                R.string.java_basic_send_event,
                JavaBasicSendEventExampleFragment.class,
                null
        );
    }
}
