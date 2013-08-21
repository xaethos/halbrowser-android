package net.xaethos.android.halbrowser.profile;

import android.test.InstrumentationTestCase;

import net.xaethos.android.halbrowser.tests.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProfileInflaterTest extends InstrumentationTestCase {

    public void testResourceAttributeInflating() {
        ProfileInflater inflater = new ProfileInflater(getInstrumentation().getTargetContext());
        ResourceConfiguration config = inflater.inflate(R.xml.profile_sample);
        assertThat(config.getLayoutRes(), is(R.layout.sample_resource));
    }

}
