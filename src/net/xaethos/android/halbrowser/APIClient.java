package net.xaethos.android.halbrowser;

import java.net.URI;

import net.xaethos.android.halbrowser.loader.HALResourceLoader;
import net.xaethos.android.halparser.HALResource;
import android.content.Context;
import android.content.Loader;

public class APIClient
{
    private final URI mBaseURI;
    private URI mEntryURI;

    private APIClient(URI baseURI) {
        mBaseURI = baseURI;
        mEntryURI = baseURI;
    }

    public URI getBaseURI() {
        return mBaseURI;
    }

    public URI getEntryURI() {
        return mEntryURI;
    }

    public Loader<HALResource> getLoaderForURI(Context context, String path) {
        return getLoaderForURI(context, URI.create(path));
    }

    public Loader<HALResource> getLoaderForURI(Context context, URI path) {
        return new HALResourceLoader(context, mBaseURI.resolve(path));
    }

    public Loader<HALResource> getLoader(Context context) {
        return getLoaderForURI(context, mEntryURI);
    }

    // ***** Inner classes

    public static class Builder
    {
        private final APIClient mClient;

        public Builder(String baseURI) {
            this(URI.create(baseURI));
        }

        public Builder(URI baseURI) {
            mClient = new APIClient(baseURI);
        }

        public Builder setEntryPath(String entryPath) {
            return setEntryPath(URI.create(entryPath));
        }

        public Builder setEntryPath(URI entryPath) {
            mClient.mEntryURI = mClient.mBaseURI.resolve(entryPath);
            return this;
        }

        public APIClient build() {
            return mClient;
        }
    }
}
