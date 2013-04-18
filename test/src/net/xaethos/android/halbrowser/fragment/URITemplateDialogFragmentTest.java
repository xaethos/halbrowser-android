package net.xaethos.android.halbrowser.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.impl.BaseHALLink;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class URITemplateDialogFragmentTest extends ActivityInstrumentationTestCase2<TestActivity>
{

    TestActivity activity;
    Solo solo;

    public URITemplateDialogFragmentTest() {
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
        HALLink link = new BaseHALLink.Builder(null).putAttribute("rel", "search")
                                                    .putAttribute("href", "/pet/{?search}")
                                                    .putAttribute("title", "Search")
                                                    .build();

        URITemplateDialogFragment.forLink(link).show(activity.getSupportFragmentManager(), "uritemplate");

        assertThat(solo.searchText("Search"), is(true));
        solo.enterText(0, "Fido");
        solo.clickOnText("OK");

        assertThat(activity.lastFollowed.first, is(link));
        assertThat(activity.lastFollowed.second, hasEntry("search", (Object) "Fido"));
    }

}
