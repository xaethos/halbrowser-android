package net.xaethos.android.halbrowser.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import net.xaethos.android.halparser.HALJsonParser;
import net.xaethos.android.halparser.HALResource;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class HALResourceLoader extends AsyncTaskLoader<HALResource>
{
    private static final String TAG = "HALResourceLoader";

    private URI mURI;

    public HALResourceLoader(Context context, URI uri) {
        super(context);
        mURI = uri;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public HALResource loadInBackground() {
        try {
            HALResource resource;

            HttpURLConnection conn = (HttpURLConnection) mURI.toURL().openConnection();
            conn.setRequestProperty("Accept", "application/hal+json, application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                Log.e(TAG, "GET " + mURI + " failed with: " + responseCode + " " + conn.getResponseMessage());
                return null;
            }

            HALJsonParser parser = new HALJsonParser(mURI);
            InputStream in = conn.getInputStream();

            try {
                resource = parser.parse(new InputStreamReader(in));
            }
            finally {
                in.close();
            }

            return resource;
        }
        catch (IOException e) {
            Log.e(TAG, "Error fetching from " + mURI.toString(), e);
            return null;
        }
    }

}
