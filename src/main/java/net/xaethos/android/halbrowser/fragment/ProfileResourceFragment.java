package net.xaethos.android.halbrowser.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.android.halbrowser.profile.ElementConfiguration;
import net.xaethos.android.halbrowser.profile.LinkConfiguration;
import net.xaethos.android.halbrowser.profile.PropertyConfiguration;
import net.xaethos.android.halbrowser.profile.ResourceConfiguration;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

public class ProfileResourceFragment extends BaseResourceFragment {

    // ***** Instance fields

    private ResourceConfiguration mConfiguration;

    // ***** Instance methods

    // *** Accessors

    public ResourceConfiguration getConfiguration() {
        return mConfiguration;
    }

    public void setConfiguration(ResourceConfiguration mConfiguration) {
        this.mConfiguration = mConfiguration;
    }

    // *** Fragment lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getConfiguration().getLayoutRes();
        if (layoutRes > 0) return inflater.inflate(layoutRes, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    // *** Resource binding

    @Override
    protected final void bindResource(View root, HALResource resource) {
        if (root == null || resource == null) return;

        ResourceConfiguration config = getConfiguration();
        root = getResourceView(root, resource, config);
        bindResourceView(root, resource, config);
    }

    protected View getResourceView(View root, HALResource resource, ResourceConfiguration config) {
        return getElementView(root, config);
    }

    protected View getPropertyView(View root, HALProperty property, PropertyConfiguration config) {
        return getElementView(root, config);
    }

    protected View getLinkView(View root, HALLink link, LinkConfiguration config) {
        return getElementView(root, config);
    }

    protected void bindResourceView(View root, HALResource resource, ResourceConfiguration config) {
        View elementView;
        PropertyConfiguration propertyConfig;
        LinkConfiguration linkConfig;
        ResourceConfiguration embeddedConfig;

        for (HALProperty property : resource.getProperties()) {
            propertyConfig = getPropertyConfiguration(config, property);
            elementView = getPropertyView(root, property, propertyConfig);
            bindPropertyView(elementView, property, propertyConfig);
        }

        for (String rel : resource.getLinkRels()) {
            for (HALLink link : resource.getLinks(rel)) {
                linkConfig = getLinkConfiguration(config, link);
                elementView = getLinkView(root, link, linkConfig);
                bindLinkView(elementView, link, linkConfig);
            }
        }

        for (String rel : resource.getResourceRels()) {
            for (HALResource embedded : resource.getResources(rel)) {
                embeddedConfig = getResourceConfiguration(config, rel);
                elementView = getResourceView(root, embedded, embeddedConfig);
                if (embeddedConfig != null) embeddedConfig.bindView(elementView, embedded);
                bindResourceView(elementView, embedded, embeddedConfig);
            }
        }
    }

    protected void bindPropertyView(View view, HALProperty property, PropertyConfiguration config) {
        if (config != null) config.bindView(view, property);
    }

    protected void bindLinkView(View view, HALLink link, LinkConfiguration config) {
        if (config != null) config.bindView(view, link);
    }

    // *** Helpers

    private View getElementView(View root, ElementConfiguration config) {
        if (config == null) return root;

        ViewGroup container = config.findContainerView(root);
        if (container == null) return root;

        View layout = config.inflateLayout(getActivity().getLayoutInflater(), container);
        if (layout == null) return container;

        container.addView(layout);
        return layout;
    }

    private PropertyConfiguration getPropertyConfiguration(ResourceConfiguration profile, HALProperty property) {
        if (profile == null) return null;

        PropertyConfiguration config = profile.getPropertyConfiguration(property.getName());
        if (config == null) config = profile.getDefaultPropertyConfiguration();
        return config;
    }

    private LinkConfiguration getLinkConfiguration(ResourceConfiguration profile, HALLink link) {
        if (profile == null) return null;

        LinkConfiguration config = profile.getLinkConfiguration(link.getRel(), link.getName());
        if (config == null) config = profile.getDefaultLinkConfiguration();
        return config;
    }

    private ResourceConfiguration getResourceConfiguration(ResourceConfiguration profile, String rel) {
        if (profile == null) return null;

        ResourceConfiguration config = profile.getResourceConfiguration(rel);
        if (config == null) config = profile.getDefaultResourceConfiguration();
        return config;
    }

}
