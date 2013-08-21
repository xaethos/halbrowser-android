package net.xaethos.android.halbrowser.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    protected boolean onBindProperty(HALResource resource, HALProperty property) {
        return false;
    }

    @Override
    protected boolean onBindLink(HALResource resource, HALLink link) {
        return false;
    }

    @Override
    protected boolean onBindEmbedded(HALResource resource, HALResource embedded, String rel) {
        return false;
    }

}
