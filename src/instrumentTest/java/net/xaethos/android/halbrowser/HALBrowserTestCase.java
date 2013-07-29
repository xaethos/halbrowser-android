package net.xaethos.android.halbrowser;

import java.net.URI;

import net.xaethos.android.test.SupportLoaderTestCase;
import net.xaethos.lib.calico.spechelper.net.URLConnectionMocker;

public class HALBrowserTestCase extends SupportLoaderTestCase
{

    protected static final URI exampleURI = URI.create("http://example.com/");

    protected URLConnectionMocker connectionMocker;
    protected URLConnectionMocker.RequestHandler requestHandler;

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

}
