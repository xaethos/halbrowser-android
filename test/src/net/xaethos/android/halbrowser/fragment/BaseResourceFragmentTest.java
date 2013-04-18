package net.xaethos.android.halbrowser.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.impl.BaseHALResource;
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
                                              .build();

        BaseResourceFragment.Builder builder = new BaseResourceFragment.Builder();
        activity.loadFragment(builder.setResource(resource).buildFragment(BaseResourceFragment.class));

        assertThat(solo.searchText("John"), is(true));
        assertThat(solo.searchText("Fido"), is(true));

        solo.clickOnText("Fido");
        assertThat(activity.lastFollowed.first, is(resource.getLink("pet")));
    }
}
