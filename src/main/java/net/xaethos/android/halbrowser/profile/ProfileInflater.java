package net.xaethos.android.halbrowser.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class ProfileInflater {

    public ResourceConfiguration inflate(Context context, int resId) {
        XmlPullParser parser = context.getResources().getXml(resId);
        try {
            return parseRoot(parser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private ResourceConfiguration parseRoot(final XmlPullParser parser) throws IOException, XmlPullParserException {
        nextTagEvent(parser);
        ResourceConfigurationImpl config = new ResourceConfigurationImpl(Xml.asAttributeSet(parser));

        while (nextTagEvent(parser) == START_TAG) {
            if ("defaultProperty".equals(parser.getName())) {
                config.setDefaultPropertyConfiguration(inflateProperty(parser));
            } else {
                config.addPropertyConfiguration(inflateProperty(parser));
            }
        }

        return config;
    }

    private PropertyConfiguration inflateProperty(XmlPullParser parser) throws IOException, XmlPullParserException {
        PropertyConfiguration config = new PropertyConfigurationImpl(Xml.asAttributeSet(parser));
        if (nextTagEvent(parser) != END_TAG)
            throw new XmlPullParserException("Tag nested in property"); // TODO: XML schema
        return config;
    }

    private int nextTagEvent(XmlPullParser parser) throws IOException, XmlPullParserException {
        int event = -1;
        while (event != START_TAG && event != END_TAG) {
            event = parser.next();
            if (event == END_DOCUMENT)
                throw new XmlPullParserException("Premature end of document");
        }
        return event;
    }

    // ***** Inner classes

    private class ResourceConfigurationImpl implements ResourceConfiguration {

        private int mLayoutRes;
        private PropertyConfiguration mDefaultPropertyConfig;
        private HashMap<String, PropertyConfiguration> mPropertyConfigMap;

        public ResourceConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if ("layout".equals(name)) mLayoutRes = attrs.getAttributeResourceValue(i, 0);
            }
            mPropertyConfigMap = new HashMap<String, PropertyConfiguration>();
        }

        @Override
        public int getLayoutRes() {
            return mLayoutRes;
        }

        public void addPropertyConfiguration(PropertyConfiguration config) {
            mPropertyConfigMap.put(config.getName(), config);
        }

        @Override
        public boolean hasPropertyConfiguration(String name) {
            return mPropertyConfigMap.containsKey(name);
        }

        @Override
        public PropertyConfiguration getPropertyConfiguration(String name) {
            return mPropertyConfigMap.get(name);
        }

        @Override
        public Collection<PropertyConfiguration> getPropertyConfigurations() {
            return Collections.unmodifiableCollection(mPropertyConfigMap.values());
        }

        @Override
        public PropertyConfiguration getDefaultPropertyConfiguration() {
            return mDefaultPropertyConfig;
        }

        public void setDefaultPropertyConfiguration(PropertyConfiguration config) {
            mDefaultPropertyConfig = config;
        }
    }

    private class PropertyConfigurationImpl implements PropertyConfiguration {

        private String mName;
        private int mLayoutRes;
        private int mContainerId;
        private int mLabelId;
        private int mContentId;

        public PropertyConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if ("name".equals(name)) mName = attrs.getAttributeValue(i);
                else if ("layout".equals(name)) mLayoutRes = attrs.getAttributeResourceValue(i, 0);
                else if ("container".equals(name))
                    mContainerId = attrs.getAttributeResourceValue(i, 0);
                else if ("labelView".equals(name)) mLabelId = attrs.getAttributeResourceValue(i, 0);
                else if ("contentView".equals(name))
                    mContentId = attrs.getAttributeResourceValue(i, 0);
            }
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public int getLayoutRes() {
            return mLayoutRes;
        }

        @Override
        public int getContainerId() {
            return mContainerId;
        }

        @Override
        public int getLabelId() {
            return mLabelId;
        }

        @Override
        public int getContentId() {
            return mContentId;
        }
    }

}
