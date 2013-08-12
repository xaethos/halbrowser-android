package net.xaethos.android.halbrowser;

import android.content.Context;
import android.support.v4.content.Loader;

import net.xaethos.android.halbrowser.loader.HALResourceLoader;
import net.xaethos.android.halparser.HALResource;

import java.net.URI;

public class APIClient
{
    private final URI mBaseURI;

    public APIClient(URI baseURI) {
        mBaseURI = baseURI;
    }

    public URI getBaseURI() {
        return mBaseURI;
    }

    public Loader<HALResource> getLoaderForURI(Context context, String path) {
        return getLoaderForURI(context, URI.create(path));
    }

    public Loader<HALResource> getLoaderForURI(Context context, URI path) {
        return new HALResourceLoader(context, mBaseURI.resolve(path));
    }

    public Loader<HALResource> getLoader(Context context) {
        return getLoaderForURI(context, mBaseURI);
    }

}
