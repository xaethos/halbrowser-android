package net.xaethos.android.halbrowser.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.xaethos.android.halbrowser.profile.LinkConfiguration;
import net.xaethos.android.halbrowser.profile.PropertyConfiguration;
import net.xaethos.android.halbrowser.profile.ResourceConfiguration;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

public class ResourceAdapterBinder extends ResourceBinder {

    public ResourceAdapterBinder(HALResource resource, ResourceConfiguration config) {
        super(resource, config);
    }

    @Override
    public void bindView(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bindResource(view, inflater, element, config);
    }

    protected void bindResource(View root, LayoutInflater inflater, HALResource resource, ResourceConfiguration config) {
        View elementView;
        ElementBinder binder;
        ResourceBinder embeddedBinder;

        for (HALProperty property : resource.getProperties()) {
            binder = getPropertyBinder(config, property);
            if (binder == null) continue;

            elementView = getElementView(root, inflater, binder);
            binder.bindView(elementView);
        }

        for (String rel : resource.getLinkRels()) {
            for (HALLink link : resource.getLinks(rel)) {
                binder = getLinkBinder(config, link);
                if (binder == null) continue;

                elementView = getElementView(root, inflater, binder);
                binder.bindView(elementView);
            }
        }

        for (String rel : resource.getResourceRels()) {
            for (HALResource embedded : resource.getResources(rel)) {
                embeddedBinder = getResourceBinder(config, resource, rel);
                if (embeddedBinder == null) continue;

                elementView = getElementView(root, inflater, embeddedBinder);
                embeddedBinder.bindView(elementView);
                bindResource(elementView, inflater, embedded, embeddedBinder.config);
            }
        }
    }

    private PropertyBinder getPropertyBinder(ResourceConfiguration profile, HALProperty property) {
        if (profile == null) return null;

        PropertyConfiguration config = profile.getPropertyConfiguration(property.getName());
        if (config == null) config = profile.getDefaultPropertyConfiguration();
        if (config == null) return null;
        return new PropertyBinder(property, config);
    }

    private LinkBinder getLinkBinder(ResourceConfiguration profile, HALLink link) {
        if (profile == null) return null;

        LinkConfiguration config = profile.getLinkConfiguration(link.getRel(), link.getName());
        if (config == null) config = profile.getDefaultLinkConfiguration();
        if (config == null) return null;
        return new LinkBinder(link, config);
    }

    private ResourceBinder getResourceBinder(ResourceConfiguration profile, HALResource resource, String rel) {
        if (profile == null) return null;

        ResourceConfiguration config = profile.getResourceConfiguration(rel);
        if (config == null) config = profile.getDefaultResourceConfiguration();
        if (config == null) return null;
        return new ResourceBinder(resource, config);
    }

    private View getElementView(View root, LayoutInflater inflater, ElementBinder binder) {
        ViewGroup container = binder.findContainerView(root);
        if (container == null) return root;
        if (container instanceof AdapterView) return container;

        View layout = binder.inflateLayout(inflater, container);
        if (layout == null) return container;

        container.addView(layout);
        return layout;
    }
}
