package net.xaethos.android.halbrowser.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

public abstract class BaseResourceFragment extends Fragment
{
    // ***** Constants

    // *** Argument/State keys
    protected static final String ARG_RESOURCE = "xae:resource";

    // ***** Instance fields

    private HALResource mResource;
    protected OnLinkFollowListener mLinkListener;

    // ***** Instance methods

    public HALResource getResource() {
        return mResource;
    }

    public void setResource(HALResource resource) {
        mResource = resource;
        if (resource != null) bindResource(getView(), resource);
    }

    // *** Fragment lifecycle

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnLinkFollowListener) {
            mLinkListener = (OnLinkFollowListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARG_RESOURCE)) {
                setResource(savedInstanceState.<HALResource>getParcelable(ARG_RESOURCE));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bindResource(view, getResource());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResource != null) outState.putParcelable(ARG_RESOURCE, mResource);
    }

    // *** Resource management

    protected void bindResource(View root, HALResource resource) {
        if (root == null || resource == null) return;

        for (HALProperty property : resource.getProperties()) {
            onBindProperty(resource, property);
        }

        for (String rel : resource.getLinkRels()) {
            for (HALLink link : resource.getLinks(rel)) {
                onBindLink(resource, link);
            }
        }

        for (String rel : resource.getResourceRels()) {
            for (HALResource embedded : resource.getResources(rel)) {
                onBindEmbedded(resource, embedded, rel);
            }
        }
    }

    protected abstract boolean onBindProperty(HALResource resource, HALProperty property);
    protected abstract boolean onBindLink(HALResource resource, HALLink link);
    protected abstract boolean onBindEmbedded(HALResource resource, HALResource embedded, String rel);

}
