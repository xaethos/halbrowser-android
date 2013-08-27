package net.xaethos.android.halbrowser.fragment;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.xaethos.android.halbrowser.profile.ProfileInflater;
import net.xaethos.android.halbrowser.profile.ResourceConfiguration;
import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.serializers.HALJsonSerializer;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class ProfileResourceFragmentTest extends ActivityInstrumentationTestCase2<TestActivity> {
    ProfileResourceFragment fragment;
    ResourceConfiguration config;
    HALResource resource;

    public ProfileResourceFragmentTest() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        resource = newResource(R.raw.owner);
        fragment = new ProfileResourceFragment();
        fragment.setResource(resource);
    }

    public void testConfigurationAccessors() throws Exception {
        config = inflateConfiguration(R.xml.profile_with_property);
        fragment.setConfiguration(config);
        assertThat(fragment.getConfiguration(), is(sameInstance(config)));
        fragment.setConfiguration(null);
        assertThat(fragment.getConfiguration(), is(nullValue()));
    }

    public void testOnCreateViewLoadConfigurationView() throws Exception {
        loadConfiguration(R.xml.profile_with_property);
        View root = fragment.onCreateView(getLayoutInflater(), null, null);
        assertThat(root.findViewById(R.id.properties_container), is(instanceOf(LinearLayout.class)));
    }

    public void testPropertyConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_property);

        View root = fragment.getView();
        ViewGroup propertiesContainer = (ViewGroup) root.findViewById(R.id.properties_container);
        assertThat(propertiesContainer.getChildCount(), is(1));

        TextView tv = (TextView) propertiesContainer.findViewById(R.id.property_name);
        assertThat(tv.getText().toString(), is("name"));

        tv = (TextView) propertiesContainer.findViewById(R.id.property_value);
        assertThat(tv.getText().toString(), is("John"));
    }

    public void testDefaultPropertyConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_default_property);

        View root = fragment.getView();
        ViewGroup propertiesContainer = (ViewGroup) root.findViewById(R.id.properties_container);
        assertThat(propertiesContainer.getChildCount(), is(1));

        TextView tv = (TextView) propertiesContainer.findViewById(android.R.id.text1);
        assertThat(tv.getText().toString(), is("33"));
    }

    public void testLinkConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_link);

        View root = fragment.getView();
        ViewGroup container;
        Button button = (Button) root.findViewById(R.id.link_wiki_main);
        assertThat(button.getText().toString(), is("Jon Arbuckle"));

        container = (ViewGroup) root.findViewById(R.id.links_wiki_container);
        assertThat(container.getChildCount(), is(2));
        button = (Button) container.getChildAt(0);
        assertThat(button.getText().toString(), is("Garfield"));
        button = (Button) container.getChildAt(1);
        assertThat(button.getText().toString(), is("Dr. Liz Wilson"));

        container = (ViewGroup) root.findViewById(R.id.links_container);
        assertThat(container.getChildCount(), is(2));
        button = (Button) container.getChildAt(0);
        assertThat(button.getText().toString(), is("self"));
        button = (Button) container.getChildAt(1);
        assertThat(button.getText().toString(), is("alternate"));
    }

    public void testEmbeddedConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_embedded);

        ViewGroup container = (ViewGroup) fragment.getView().findViewById(R.id.pets_container);
        assertThat(container.getChildCount(), is(2));

        ViewGroup petLayout = (ViewGroup) container.getChildAt(0);
        TextView tv;
        tv = (TextView) petLayout.findViewById(R.id.text_name);
        assertThat(tv.getText().toString(), is("Odis"));
        tv = (TextView) petLayout.findViewById(R.id.text_type);
        assertThat(tv.getText().toString(), is("Dog"));

        petLayout = (ViewGroup) container.getChildAt(1);
        tv = (TextView) petLayout.findViewById(R.id.text_name);
        assertThat(tv.getText().toString(), is("Garfield"));
        tv = (TextView) petLayout.findViewById(R.id.text_type);
        assertThat(tv.getText().toString(), is("Cat"));
    }

    // *** Helpers

    protected Context getTargetContext() {
        return getInstrumentation().getTargetContext();
    }

    protected LayoutInflater getLayoutInflater() {
        return (LayoutInflater) getTargetContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected Reader newReader(int resId) {
        return new InputStreamReader(getInstrumentation().getTargetContext().getResources().openRawResource(resId));
    }

    protected HALResource newResource(int resId) throws Exception {
        return new HALJsonSerializer().parse(newReader(resId));
    }

    private ResourceConfiguration inflateConfiguration(int resId) {
        return new ProfileInflater().inflate(getTargetContext(), resId);
    }

    private void loadConfiguration(int resId) {
        fragment.setConfiguration(inflateConfiguration(resId));
    }

    private void startActivityWithConfiguration(int configRes) {
        loadConfiguration(configRes);
        getActivity().loadFragment(fragment);
        getInstrumentation().waitForIdleSync();
    }

}
