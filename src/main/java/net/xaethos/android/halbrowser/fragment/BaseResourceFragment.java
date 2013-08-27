package net.xaethos.android.halbrowser.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.android.halparser.HALResource;

public abstract class BaseResourceFragment extends Fragment {
    // ***** Constants

    // *** Argument/State keys
    protected static final String ARG_RESOURCE = "xae:resource";

    // ***** Instance fields
    protected OnLinkFollowListener mLinkListener;
    private HALResource mResource;

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

    protected abstract void bindResource(View root, HALResource resource);

}
