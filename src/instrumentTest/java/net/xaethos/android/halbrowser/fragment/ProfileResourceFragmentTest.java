package net.xaethos.android.halbrowser.fragment;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
        startActivityWithConfiguration(R.xml.profile_with_property);

        View root = fragment.getView();
        assertThat(root.findViewById(R.id.properties_container), is(instanceOf(LinearLayout.class)));
    }

    public void testPropertyConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_property);

        View root = fragment.getView();
        ViewGroup propertiesContainer = (ViewGroup) root.findViewById(R.id.properties_container);

        assertThat(propertiesContainer.getChildCount(), is(1));
        assertThat(getText(propertiesContainer, R.id.property_name), is("name"));
        assertThat(getText(propertiesContainer, R.id.property_value), is("John"));
    }

    public void testDefaultPropertyConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_default_property);

        View root = fragment.getView();
        ViewGroup propertiesContainer = (ViewGroup) root.findViewById(R.id.properties_container);
        assertThat(propertiesContainer.getChildCount(), is(1));

        assertThat(getText(propertiesContainer, android.R.id.text1), is("33"));
    }

    public void testLinkConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_link);

        View root = fragment.getView();
        ViewGroup container;
        assertThat(getText(root, R.id.link_wiki_main), is("Jon Arbuckle"));

        container = (ViewGroup) root.findViewById(R.id.links_wiki_container);
        assertThat(container.getChildCount(), is(2));
        assertThat(getText(container.getChildAt(0)), is("Garfield"));
        assertThat(getText(container.getChildAt(1)), is("Dr. Liz Wilson"));

        container = (ViewGroup) root.findViewById(R.id.links_container);
        assertThat(container.getChildCount(), is(2));
        assertThat(getText(container.getChildAt(0)), is("self"));
        assertThat(getText(container.getChildAt(1)), is("alternate"));
    }

    public void testEmbeddedConfiguration() throws Exception {
        startActivityWithConfiguration(R.xml.profile_with_embedded);

        ViewGroup container = (ViewGroup) fragment.getView().findViewById(R.id.pets_container);
        assertThat(container.getChildCount(), is(2));

        ViewGroup petLayout = (ViewGroup) container.getChildAt(0);
        assertThat(getText(petLayout, R.id.text_name), is("Odis"));
        assertThat(getText(petLayout, R.id.text_type), is("Dog"));

        petLayout = (ViewGroup) container.getChildAt(1);
        assertThat(getText(petLayout, R.id.text_name), is("Garfield"));
        assertThat(getText(petLayout, R.id.text_type), is("Cat"));
    }

    public void testListAsContainer() throws Exception {
        ListView listView;
        startActivityWithConfiguration(R.xml.profile_with_list);

        listView = (ListView) fragment.getView().findViewById(android.R.id.list);
        assertThat(listView.getAdapter().getCount(), is(9));

        String[] expected = {
                "name", "age", "self", "alternate",
                "Jon Arbuckle", "Garfield", "Dr. Liz Wilson",
                "Odis", "Garfield"
        };

        for (int i=0; i<9; ++i) {
            assertThat(getText(listView.getChildAt(i), android.R.id.text1), is(expected[i]));
        }

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.remove(fragment);
        t.add(TestActivity.FRAGMENT_ID, fragment);
        t.commit();

        listView = (ListView) fragment.getView().findViewById(android.R.id.list);
        assertThat(listView.getAdapter().getCount(), is(9));
    }

    // *** Helpers

    protected String getText(View view) {
        CharSequence text = ((TextView) view).getText();
        return text == null ? null : text.toString();
    }

    protected String getText(View root, int viewId) {
        return getText(root.findViewById(viewId));
    }

    protected Context getTargetContext() {
        return getInstrumentation().getTargetContext();
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
