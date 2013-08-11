package net.xaethos.android.halbrowser.fragment;

import android.support.v4.app.ListFragment;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.impl.BaseHALLink;
import net.xaethos.android.halparser.impl.BaseHALResource;
import net.xaethos.android.halparser.serializers.HALJsonSerializer;

import java.io.InputStreamReader;
import java.io.Reader;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BaseResourceFragmentTest extends ActivityInstrumentationTestCase2<TestActivity>
{

    TestActivity activity;
    Solo solo;

    public BaseResourceFragmentTest() {
        super(TestActivity.class);
    }

    public void setUp() throws Exception {
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testPropertiesAndLinks() throws Exception {
        BaseHALResource resource = new BaseHALResource();
        resource.setValue("name", "John");
        resource.addLink(new BaseHALLink("pet", "/pet/13", singletonMap("title", "Fido")));
        resource.addLink(new BaseHALLink("alternate", "/owner/42"));

        BaseResourceFragment.Builder builder = new BaseResourceFragment.Builder();
        ListFragment fragment = builder.setResource(resource).buildFragment(BaseResourceFragment.class);
        activity.loadFragment(fragment);

        assertThat(solo.searchText("John"), is(true));
        assertThat(solo.searchText("Fido"), is(true));

        assertThat(fragment.getListAdapter().getCount(), is(2));

        solo.clickOnText("Fido");
        assertThat(activity.lastFollowed.first, is(resource.getLink("pet")));
    }

    public void testEmbeddedResources() throws Exception {
        HALResource resource = new HALJsonSerializer().parse(newReader(R.raw.owner));

        BaseResourceFragment.Builder builder = new BaseResourceFragment.Builder();
        ListFragment fragment = builder.setResource(resource).buildFragment(BaseResourceFragment.class);
        activity.loadFragment(fragment);

        assertThat(solo.searchText("John in Title"), is(false));
        assertThat(solo.searchText("John"), is(true));
        assertThat(solo.searchText("33"), is(true));

        assertThat(solo.searchText("Odis"), is(true));
        assertThat(solo.searchText("Dog"), is(true));
        assertThat(solo.searchText("Toys"), is(false));

        assertThat(fragment.getListAdapter().getCount(), is(4));

        solo.clickOnText("Odis");
        assertThat(activity.lastFollowed.first, is(resource.getResource("pet").getLink("self")));

        assertThat(solo.searchText("Garfield"), is(true));
        assertThat(solo.searchText("Cat"), is(true));

        solo.clickOnText("Garfield");
        assertThat(activity.lastFollowed.first, is(resource.getResources("pet").get(1).getLink("self")));
    }

    // *** Helpers

    protected Reader newReader(int resId) {
        return new InputStreamReader(activity.getResources().openRawResource(resId));
    }

}
