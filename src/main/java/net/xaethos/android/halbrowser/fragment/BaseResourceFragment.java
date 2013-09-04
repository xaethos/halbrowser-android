package net.xaethos.android.halbrowser.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.xaethos.android.halparser.HALResource;

public class BaseResourceFragment extends Fragment {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResource != null) outState.putParcelable(ARG_RESOURCE, mResource);
    }

}
