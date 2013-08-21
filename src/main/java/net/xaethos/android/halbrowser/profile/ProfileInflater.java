package net.xaethos.android.halbrowser.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ProfileInflater {

    private final Context mContext;

    public ProfileInflater(Context context) {
        this.mContext = context;
    }

    public ResourceConfiguration inflate(int resId) {
        XmlPullParser parser = mContext.getResources().getXml(resId);
        try {
            return parseRoot(parser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private ResourceConfiguration parseRoot(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        parser.nextTag();
        return new ResourceConfigurationImpl(Xml.asAttributeSet(parser));
    }

    // ***** Inner classes

    private class ResourceConfigurationImpl implements ResourceConfiguration {

        private int mLayoutRes;

        public ResourceConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if ("layout".equals(name)) mLayoutRes = attrs.getAttributeResourceValue(i, 0);
            }
        }

        @Override
        public int getLayoutRes() {
            return mLayoutRes;
        }

    }

}
