package net.xaethos.android.halbrowser;

import net.xaethos.android.test.SupportLoaderTestCase;
import net.xaethos.lib.calico.spechelper.net.URLConnectionMocker;

import java.net.URI;

public class HALBrowserTestCase extends SupportLoaderTestCase
{

    protected static final URI exampleURI = URI.create("http://example.com/");

    protected URLConnectionMocker connectionMocker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connectionMocker = URLConnectionMocker.startMocking();
    }

    @Override
    protected void tearDown() throws Exception {
        connectionMocker.finishMocking();
        super.tearDown();
    }

    protected void stubRequest(String path, int resId) {
        stubRequest(exampleURI.resolve(path), resId);
    }

    protected void stubRequest(URI uri, int resId) {
        connectionMocker.addRequest(uri).setResponseStream(getContext().getResources(), resId);
    }

}
