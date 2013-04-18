package net.xaethos.android.halbrowser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.net.URI;

import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.lib.calico.spechelper.net.MockHttpURLConnection;
import android.content.Loader;

public class APIClientTest extends HALBrowserTestCase
{
    public void testBuildClient() {
        APIClient client = new APIClient.Builder(exampleURI).setEntryPath(URI.create("/dashboard")).build();
        assertThat(client.getBaseURI(), is(exampleURI));
        assertThat(client.getEntryURI(), is(exampleURI.resolve("/dashboard")));
    }

    public void testLoad() {
        requestHandler = connectionMocker.addRequest(exampleURI.resolve("/dashboard"));
        requestHandler.setResponseStream(getContext().getResources(), R.raw.example);

        APIClient client = new APIClient.Builder(exampleURI).build();
        Loader<HALResource> loader = client.getLoaderForURI(getContext(), "/dashboard");

        HALResource resource = getLoaderResultSynchronously(loader);
        assertThat(resource.getLink(Relation.SELF).getHref(), is("https://example.com/api/customer/123456"));

        MockHttpURLConnection conn = connectionMocker.getConnection();
        assertThat(conn.getRequestProperty("Accept"), is("application/hal+json, application/json"));
    }

    public void testLoadEntryPath() {
        requestHandler = connectionMocker.addRequest(exampleURI.resolve("/dashboard"));
        requestHandler.setResponseStream(getContext().getResources(), R.raw.example);

        APIClient client = new APIClient.Builder(exampleURI).setEntryPath("/dashboard").build();
        Loader<HALResource> loader = client.getLoader(getContext());

        HALResource resource = getLoaderResultSynchronously(loader);
        assertThat(resource.getLink(Relation.SELF).getHref(), is("https://example.com/api/customer/123456"));
    }

}
