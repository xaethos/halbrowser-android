package net.xaethos.android.halbrowser.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.InputStreamReader;
import java.io.Reader;

import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALJsonParser;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.impl.BaseHALResource;
import android.support.v4.app.ListFragment;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

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
        BaseHALResource.Builder resourceBuilder = new BaseHALResource.Builder(null);
        HALResource resource = resourceBuilder.putProperty("name", "John")
                                              .putLink(resourceBuilder.buildLink()
                                                                      .putAttribute("rel", "pet")
                                                                      .putAttribute("href", "/pet/13")
                                                                      .putAttribute("title", "Fido")
                                                                      .build())
                                              .putLink(resourceBuilder.buildLink()
                                                                      .putAttribute("rel", "alternate")
                                                                      .putAttribute("href", "/owner/42")
                                                                      .build())
                                              .build();

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
        HALResource resource = new HALJsonParser("http://example.com").parse(newReader(R.raw.owner));

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
