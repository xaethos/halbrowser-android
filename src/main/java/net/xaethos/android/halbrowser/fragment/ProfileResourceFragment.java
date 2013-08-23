package net.xaethos.android.halbrowser.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    protected boolean onBindProperty(View root, HALResource resource, HALProperty property) {
        String propertyName = property.getName();
        ResourceConfiguration profile = getConfiguration();
        PropertyConfiguration config;

        if (profile.hasPropertyConfiguration(propertyName)) {
            config = profile.getPropertyConfiguration(propertyName);
        } else {
            config = profile.getDefaultPropertyConfiguration();
        }

        if (config == null) return false;

        ViewGroup container = (ViewGroup) root.findViewById(config.getContainerId());
        if (container != null) {
            root = container;
            if (config.getLayoutRes() > 0) {
                View layout = getActivity().getLayoutInflater().inflate(config.getLayoutRes(), container, false);
                container.addView(layout);
                root = layout;
            }
        }

        View view;
        view = root.findViewById(config.getLabelId());
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(propertyName);
        }
        view = root.findViewById(config.getContentId());
        if (view != null && view instanceof TextView) {
            Object value = property.getValue();
            if (value != null) {
                ((TextView) view).setText(value.toString());
            }
        }
        return true;
    }

    @Override
    protected boolean onBindLink(View root, HALResource resource, HALLink link) {
        return false;
    }

    @Override
    protected boolean onBindEmbedded(View root, HALResource resource, HALResource embedded, String rel) {
        return false;
    }

}
