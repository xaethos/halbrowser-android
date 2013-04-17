package net.xaethos.android.halbrowser.loader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.net.URI;

import net.xaethos.android.halbrowser.HALBrowserTestCase;
import net.xaethos.android.halbrowser.tests.R;
import net.xaethos.android.halparser.HALResource;

public class HALResourceLoaderTest extends HALBrowserTestCase
{

    public void testSmoke() throws Exception {
        URI uri = URI.create("http://example.com");
        requestHandler = connectionMocker.addRequest(uri.toURL());
        requestHandler.setResponseStream(getContext().getResources(), R.raw.example);

        HALResourceLoader loader = new HALResourceLoader(getContext(), uri);
        HALResource resource = getLoaderResultSynchronously(loader);

        assertThat(resource, is(notNullValue()));
        assertThat(resource.getLink("self").getHref(), is("https://example.com/api/customer/123456"));
    }

}
