package net.xaethos.android.halbrowser.fragment;

import java.util.Map;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALResource;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ResourceFragment
{

    public HALResource getResource();

    public void setResource(HALResource resource);

    public void bindResource(View view, HALResource resource);

    public View getPropertyView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            HALResource resource,
            String name);

    public void bindPropertyView(View propertyView, HALResource resource, String name, Object value);

    public View getLinkView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            HALResource resource,
            HALLink link);

    public void bindLinkView(View propertyView, HALResource resource, HALLink link);

    public View getResourceView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            String rel,
            HALResource resource);

    public void bindResourceView(View propertyView, String rel, HALResource resource);

    // ***** Inner classes

    public interface OnLinkFollowListener
    {
        void onFollowLink(HALLink link, Map<String, Object> map);
    }

}
