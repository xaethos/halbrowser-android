package net.xaethos.android.halbrowser.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class ProfileInflater {
    private static final String TAG_PROPERTY = "property";
    private static final String TAG_DEFAULT_PROPERTY = "defaultProperty";
    private static final String TAG_LINK = "link";
    private static final String TAG_DEFAULT_LINK = "defaultLink";
    private static final String TAG_RESOURCE = "resource";
    private static final String TAG_DEFAULT_RESOURCE = "defaultResource";
    private static final String ATTR_LAYOUT = "layout";
    private static final String ATTR_REL = "rel";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_LABEL_VIEW = "labelView";
    private static final String ATTR_CONTENT_VIEW = "contentView";

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
        return inflateResource(parser);
    }

    private ResourceConfiguration inflateResource(final XmlPullParser parser) throws IOException, XmlPullParserException {
        ResourceConfigurationImpl config = new ResourceConfigurationImpl(Xml.asAttributeSet(parser));

        while (nextTagEvent(parser) == START_TAG) {
            String name = parser.getName();
            if (TAG_DEFAULT_PROPERTY.equals(name)) {
                config.setDefaultPropertyConfiguration(inflateProperty(parser));
            } else if (TAG_PROPERTY.equals(name)) {
                config.addPropertyConfiguration(inflateProperty(parser));
            } else if (TAG_DEFAULT_LINK.equals(name)) {
                config.setDefaultLinkConfiguration(inflateLink(parser));
            } else if (TAG_LINK.equals(name)) {
                config.addLinkConfiguration(inflateLink(parser));
            } else if (TAG_RESOURCE.equals(name)) {
                config.addResourceConfiguration(inflateResource(parser));
            } else if (TAG_DEFAULT_RESOURCE.equals(name)) {
                config.setDefaultResourceConfiguration(inflateResource(parser));
            } else {
                throw new XmlPullParserException("Unexpected element: " + name); // TODO: XML schema
            }
        }

        return config;
    }

    private PropertyConfiguration inflateProperty(XmlPullParser parser) throws IOException, XmlPullParserException {
        PropertyConfiguration config = new PropertyConfigurationImpl(Xml.asAttributeSet(parser));
        if (nextTagEvent(parser) != END_TAG) {
            throw new XmlPullParserException("Tag nested in property"); // TODO: XML schema
        }
        return config;
    }

    private LinkConfiguration inflateLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        LinkConfiguration config = new LinkConfigurationImpl(Xml.asAttributeSet(parser));
        if (nextTagEvent(parser) != END_TAG) {
            throw new XmlPullParserException("Tag nested in property"); // TODO: XML schema
        }
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

    private abstract class ElementConfigurationImpl implements ElementConfiguration {

        protected int mLayoutRes;
        protected int mContainerId;

        @Override
        public int getLayoutRes() {
            return mLayoutRes;
        }

        @Override
        public int getContainerId() {
            return mContainerId;
        }

    }

    private class ResourceConfigurationImpl extends ElementConfigurationImpl implements ResourceConfiguration {

        private String mRel;
        private PropertyConfiguration mDefaultPropertyConfig;
        private HashMap<String, PropertyConfiguration> mPropertyConfigMap;
        private LinkConfiguration mDefaultLinkConfig;
        private HashMap<String, HashMap<String, LinkConfiguration>> mLinksConfigMap;
        private ResourceConfiguration mDefaultResourceConfig;
        private HashMap<String, ResourceConfiguration> mResourceConfigMap;

        public ResourceConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if (ATTR_LAYOUT.equals(name)) mLayoutRes = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_REL.equals(name)) mRel = attrs.getAttributeValue(i);
                else if (ATTR_CONTAINER.equals(name))
                    mContainerId = attrs.getAttributeResourceValue(i, 0);
            }
            mPropertyConfigMap = new HashMap<String, PropertyConfiguration>();
            mLinksConfigMap = new HashMap<String, HashMap<String, LinkConfiguration>>();
            mResourceConfigMap = new HashMap<String, ResourceConfiguration>();
        }

        @Override
        public String getRel() {
            return mRel;
        }

        public void addPropertyConfiguration(PropertyConfiguration config) {
            mPropertyConfigMap.put(config.getName(), config);
        }

        @Override
        public PropertyConfiguration getPropertyConfiguration(String name) {
            return mPropertyConfigMap.get(name);
        }

        @Override
        public PropertyConfiguration getDefaultPropertyConfiguration() {
            return mDefaultPropertyConfig;
        }

        public void setDefaultPropertyConfiguration(PropertyConfiguration config) {
            mDefaultPropertyConfig = config;
        }

        public void addLinkConfiguration(LinkConfiguration config) {
            String rel = config.getRel();

            HashMap<String, LinkConfiguration> nameMap = mLinksConfigMap.get(rel);
            if (nameMap == null) {
                nameMap = new HashMap<String, LinkConfiguration>();
                mLinksConfigMap.put(rel, nameMap);
            }
            nameMap.put(config.getName(), config);
        }

        @Override
        public LinkConfiguration getLinkConfiguration(String rel, String name) {
            HashMap<String, LinkConfiguration> nameMap = mLinksConfigMap.get(rel);
            if (nameMap == null) return null;
            LinkConfiguration config = nameMap.get(name);
            if (config != null) return config;
            return nameMap.get(null);
        }

        @Override
        public LinkConfiguration getDefaultLinkConfiguration() {
            return mDefaultLinkConfig;
        }

        public void setDefaultLinkConfiguration(LinkConfiguration config) {
            mDefaultLinkConfig = config;
        }

        public void addResourceConfiguration(ResourceConfiguration config) {
            mResourceConfigMap.put(config.getRel(), config);
        }

        @Override
        public ResourceConfiguration getResourceConfiguration(String rel) {
            return mResourceConfigMap.get(rel);
        }

        @Override
        public ResourceConfiguration getDefaultResourceConfiguration() {
            return mDefaultResourceConfig;
        }

        private void setDefaultResourceConfiguration(ResourceConfiguration config) {
            mDefaultResourceConfig = config;
        }

    }

    private class PropertyConfigurationImpl extends ElementConfigurationImpl implements PropertyConfiguration {

        private String mName;
        private int mLabelId;
        private int mContentId;

        public PropertyConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if (ATTR_NAME.equals(name)) mName = attrs.getAttributeValue(i);
                else if (ATTR_LAYOUT.equals(name))
                    mLayoutRes = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_CONTAINER.equals(name))
                    mContainerId = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_LABEL_VIEW.equals(name))
                    mLabelId = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_CONTENT_VIEW.equals(name))
                    mContentId = attrs.getAttributeResourceValue(i, 0);
            }
        }

        @Override
        public String getName() {
            return mName;
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

    private class LinkConfigurationImpl extends ElementConfigurationImpl implements LinkConfiguration {

        private String mRel;
        private String mName;
        private int mLabelId;

        public LinkConfigurationImpl(AttributeSet attrs) {
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; ++i) {
                String name = attrs.getAttributeName(i);
                if (ATTR_REL.equals(name)) mRel = attrs.getAttributeValue(i);
                else if (ATTR_NAME.equals(name)) mName = attrs.getAttributeValue(i);
                else if (ATTR_LAYOUT.equals(name))
                    mLayoutRes = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_CONTAINER.equals(name))
                    mContainerId = attrs.getAttributeResourceValue(i, 0);
                else if (ATTR_LABEL_VIEW.equals(name))
                    mLabelId = attrs.getAttributeResourceValue(i, 0);
            }
        }

        @Override
        public String getRel() {
            return mRel;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public int getLabelId() {
            return mLabelId;
        }

    }

}
