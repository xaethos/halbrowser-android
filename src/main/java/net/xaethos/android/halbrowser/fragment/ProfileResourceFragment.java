package net.xaethos.android.halbrowser.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
        if (config == null) return root;
        return getElementView(root, new ResourceBinder(resource, config));
    }

    protected View getPropertyView(View root, HALProperty property, PropertyConfiguration config) {
        if (config == null) return root;
        return getElementView(root, new PropertyBinder(property, config));
    }

    protected View getLinkView(View root, HALLink link, LinkConfiguration config) {
        if (config == null) return root;
        return getElementView(root, new LinkBinder(link, config));
    }

    protected void bindResourceView(View root, HALResource resource, ResourceConfiguration config) {
        if (config != null && root instanceof AdapterView) {
            ElementAdapter adapter = adapterFor((AdapterView) root);
            adapter.add(new ResourceAdapterBinder(resource, config));
            return;
        }

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
                if (embeddedConfig != null)
                    new ResourceBinder(embedded, embeddedConfig).bindView(elementView);
                bindResourceView(elementView, embedded, embeddedConfig);
            }
        }
    }

    protected void bindPropertyView(View view, HALProperty property, PropertyConfiguration config) {
        if (config != null) bindElementView(view, new PropertyBinder(property, config));
    }

    protected void bindLinkView(View view, HALLink link, LinkConfiguration config) {
        if (config != null) bindElementView(view, new LinkBinder(link, config));
    }

    // *** Helpers

    private ElementAdapter adapterFor(AdapterView view) {
        ElementAdapter adapter = (ElementAdapter) view.getAdapter();
        if (adapter == null) {
            adapter = new ElementAdapter();
            view.setAdapter(adapter);
        }
        return adapter;
    }

    private View getElementView(View root, ElementBinder binder) {
        ViewGroup container = binder.findContainerView(root);
        if (container == null) return root;
        if (container instanceof AdapterView) return container;

        View layout = binder.inflateLayout(getActivity().getLayoutInflater(), container);
        if (layout == null) return container;

        container.addView(layout);
        return layout;
    }

    private void bindElementView(View view, ElementBinder binder) {
        if (view instanceof AdapterView) {
            ElementAdapter adapter = adapterFor((AdapterView) view);
            adapter.add(binder);
        } else {
            binder.bindView(view);
        }
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

    // ***** Inner Classes

    private class ElementAdapter extends ArrayAdapter<ElementBinder> {
        public ElementAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ElementBinder binder = getItem(position);
            View view = binder.inflateLayout(getActivity().getLayoutInflater(), parent);
            binder.bindView(view);
            return view;
        }
    }

}
