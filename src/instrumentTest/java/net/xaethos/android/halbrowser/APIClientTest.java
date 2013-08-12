package net.xaethos.android.halbrowser;

import android.support.v4.content.Loader;

import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.lib.calico.spechelper.net.MockHttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class APIClientTest extends HALBrowserTestCase
{
    private APIClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        client = new APIClient(exampleURI);
    }

    public void testGetBaseURI() {
        assertThat(client.getBaseURI(), is(exampleURI));
    }

    public void testLoadBaseURI() {
        stubRequest(exampleURI, R.raw.example);

        Loader<HALResource> loader = client.getLoader(getContext());

        HALResource resource = getLoaderResultSynchronously(loader);
        assertThat(resource.getLink(Relation.SELF).getHref(), is("https://example.com/api/customer/123456"));
    }

    public void testLoadPath() {
        stubRequest("/foo/bar", R.raw.example);

        Loader<HALResource> loader = client.getLoaderForURI(getContext(), "/foo/bar");

        HALResource resource = getLoaderResultSynchronously(loader);
        assertThat(resource.getLink(Relation.SELF).getHref(), is("https://example.com/api/customer/123456"));
    }

    public void testRequestHeaders() {
        stubRequest(exampleURI, R.raw.example);

        getLoaderResultSynchronously(client.getLoader(getContext()));

        MockHttpURLConnection conn = connectionMocker.getConnection();
        assertThat(conn.getRequestProperty("Accept"), is("application/hal+json, application/json"));
    }

}
