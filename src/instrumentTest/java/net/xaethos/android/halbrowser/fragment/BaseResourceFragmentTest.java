package net.xaethos.android.halbrowser.fragment;

import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.view.View;

import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.impl.BaseHALResource;
import net.xaethos.android.halparser.serializers.HALJsonSerializer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class BaseResourceFragmentTest extends InstrumentationTestCase
{
    TestResourceFragment fragment;
    HALResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fragment = new TestResourceFragment();
    }

    public void testResourceAccessors() throws Exception {
        resource = new BaseHALResource();

        fragment.setResource(resource);
        assertThat(fragment.getResource(), is(sameInstance(resource)));
    }

    public void testFragmentInstanceState() throws Exception {
        resource = newResource(R.raw.owner);

        fragment.setResource(resource);

        Bundle icicle = new Bundle();
        fragment.onSaveInstanceState(icicle);

        TestResourceFragment restoredFragment = new TestResourceFragment();
        restoredFragment.onCreate(icicle);

        assertThat(restoredFragment.getResource(), is(not(nullValue())));
        assertThat(restoredFragment.getResource().getValueString("age"), is("33"));
    }

    public void testOnViewCreatedBindsResource() throws Exception {
        resource = newResource(R.raw.owner);
        fragment.setResource(resource);

        fragment.view = basicView();
        fragment.onViewCreated(fragment.view, null);
        assertTrue(fragment.boundProperties.containsAll(resource.getProperties()));

        for (String rel : resource.getLinkRels()) {
            assertTrue(fragment.boundLink.containsAll(resource.getLinks(rel)));
        }

        for (String rel : resource.getResourceRels()) {
            assertTrue(fragment.boundEmbedded.containsAll(resource.getResources(rel)));
        }
    }

    public void testWhenViewExistsSetResourceBindsResource() throws Exception {
        resource = newResource(R.raw.owner);

        fragment.view = basicView();

        assertThat(fragment.boundProperties, is(empty()));
        fragment.setResource(resource);
        assertTrue(fragment.boundProperties.containsAll(resource.getProperties()));
    }

    // *** Helpers

    protected View basicView() {
        return new View(getInstrumentation().getTargetContext());
    }

    protected Reader newReader(int resId) {
        return new InputStreamReader(getInstrumentation().getTargetContext().getResources().openRawResource(resId));
    }

    protected HALResource newResource(int resId) throws Exception {
        return new HALJsonSerializer().parse(newReader(resId));
    }

    class TestResourceFragment extends BaseResourceFragment {

        public View view;
        public Collection<HALProperty> boundProperties = new ArrayList<HALProperty>();
        public Collection<HALLink> boundLink = new ArrayList<HALLink>();
        public Collection<HALResource> boundEmbedded = new ArrayList<HALResource>();

        @Override
        protected boolean onBindProperty(View root, HALResource resource, HALProperty property) {
            boundProperties.add(property);
            return false;
        }

        @Override
        protected boolean onBindLink(View root, HALResource resource, HALLink link) {
            boundLink.add(link);
            return false;
        }

        @Override
        protected boolean onBindEmbedded(View root, HALResource resource, HALResource embedded, String rel) {
            boundEmbedded.add(embedded);
            return false;
        }

        @Override
        public View getView() {
            return view;
        }

    }

}
