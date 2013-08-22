package net.xaethos.android.halbrowser.profile;

import android.content.Context;
import android.test.InstrumentationTestCase;

import net.xaethos.android.halbrowser.tests.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class ProfileInflaterTest extends InstrumentationTestCase {

    ProfileInflater inflater;
    Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        inflater = new ProfileInflater();
        context = getInstrumentation().getTargetContext();
    }

    public void testResourceAttributeInflating() {
        ResourceConfiguration config = inflater.inflate(context, R.xml.profile_sample);
        assertThat(config.getLayoutRes(), is(R.layout.sample_resource));
    }

    public void testPropertyAttributeInflating() {
        ResourceConfiguration profile = inflater.inflate(context, R.xml.profile_sample);

        assertThat(profile.hasPropertyConfiguration("blahblah"), is(false));
        assertThat(profile.getPropertyConfiguration("blahblah"), is(nullValue()));

        assertThat(profile.hasPropertyConfiguration("name"), is(true));
        PropertyConfiguration config = profile.getPropertyConfiguration("name");
        assertThat(config, is(not(nullValue())));

        assertThat(config.getLayoutRes(), is(R.layout.sample_property));
        assertThat(config.getContainerId(), is(R.id.properties_container));
        assertThat(config.getLabelId(), is(R.id.property_name));
        assertThat(config.getContentId(), is(R.id.property_value));

        assertThat(profile.getPropertyConfigurations(), contains(config));
    }

}
