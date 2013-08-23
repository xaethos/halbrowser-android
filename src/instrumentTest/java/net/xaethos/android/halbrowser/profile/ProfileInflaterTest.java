package net.xaethos.android.halbrowser.profile;

import android.content.Context;
import android.test.InstrumentationTestCase;

import net.xaethos.android.halbrowser.tests.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

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
        ResourceConfiguration config = inflater.inflate(context, R.xml.profile_with_property);
        assertThat(config.getLayoutRes(), is(R.layout.resource_with_properties_container));
    }

    public void testPropertyAttributeInflating() {
        ResourceConfiguration profile = inflater.inflate(context, R.xml.profile_with_property);

        assertThat(profile.hasPropertyConfiguration("foo"), is(false));
        assertThat(profile.getPropertyConfiguration("foo"), is(nullValue()));

        assertThat(profile.hasPropertyConfiguration("name"), is(true));
        PropertyConfiguration config = profile.getPropertyConfiguration("name");
        assertThat(config, is(not(nullValue())));

        assertThat(config.getLayoutRes(), is(R.layout.property_with_label));
        assertThat(config.getContainerId(), is(R.id.properties_container));
        assertThat(config.getLabelId(), is(R.id.property_name));
        assertThat(config.getContentId(), is(R.id.property_value));

        assertThat(profile.getPropertyConfigurations(), contains(config));
    }

    public void testDefaultPropertyAttributeInflating() {
        ResourceConfiguration profile = inflater.inflate(context, R.xml.profile_with_default_property);

        assertThat(profile.hasPropertyConfiguration("foo"), is(false));
        assertThat(profile.getPropertyConfiguration("foo"), is(nullValue()));

        assertThat(profile.hasPropertyConfiguration("name"), is(true));
        PropertyConfiguration config = profile.getDefaultPropertyConfiguration();
        assertThat(config, is(not(nullValue())));

        assertThat(config.getLayoutRes(), is(android.R.layout.simple_list_item_1));
        assertThat(config.getContainerId(), is(R.id.properties_container));
        assertThat(config.getLabelId(), is(0));
        assertThat(config.getContentId(), is(android.R.id.text1));

        assertThat(profile.getPropertyConfigurations(), not(hasItem(config)));
    }

    public void testLinkAttributeInflating() {
        ResourceConfiguration profile = inflater.inflate(context, R.xml.profile_with_link);
        LinkConfiguration config;

        config = profile.getDefaultLinkConfiguration();
        assertThat(config.getContainerId(), is(R.id.links_container));

        assertThat(profile.getLinkConfiguration("foo", null), is(nullValue()));
        assertThat(profile.getLinkConfiguration("foo", "bar"), is(nullValue()));

        config = profile.getLinkConfiguration("wiki", "main");
        assertThat(config, is(notNullValue()));
        assertThat(config.getLabelId(), is(R.id.link_wiki_main));

        config = profile.getLinkConfiguration("wiki", null);
        assertThat(config, is(notNullValue()));
        assertThat(config.getLabelId(), is(R.id.link_button));
        assertThat(config.getLayoutRes(), is(R.layout.link_button));
        assertThat(config.getContainerId(), is(R.id.links_wiki_container));

        assertThat(profile.getLinkConfiguration("wiki", "blah"), is(sameInstance(config)));
    }

}
