package net.xaethos.android.halbrowser.fragment;

import java.util.Map;

import static net.xaethos.android.halbrowser.Relation.*;

import net.xaethos.android.halbrowser.R;

import net.xaethos.android.halbrowser.adapter.SimpleRepresentationAdapter;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALResource;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class BaseResourceFragment extends ListFragment
        implements
        ResourceFragment,
        View.OnClickListener,
        AdapterView.OnItemClickListener
{
    // ***** Constants

    // *** Argument/State keys
    protected static final String ARG_RESOURCE = "resource";
    protected static final String ARG_FRAGMENT_LAYOUT = "fragment_layout";
    protected static final String ARG_PROPERTY_LAYOUT = "property_layout";
    protected static final String ARG_LINK_LAYOUT = "link_layout";
    protected static final String ARG_PROPERTY_LAYOUT_MAP = "property_layout_map";
    protected static final String ARG_LINK_LAYOUT_MAP = "link_layout_map";
    protected static final String ARG_REL_SHOW_MAP = "rel_show_map";

    // ***** Instance fields

    private HALResource mResource;
    protected OnLinkFollowListener mLinkListener;

    // ***** Instance methods

    @Override
    public HALResource getResource() {
        if (mResource == null) {
            Bundle args = getArguments();
            if (args != null) {
                mResource = args.getParcelable(ARG_RESOURCE);
            }
        }
        return mResource;
    }

    @Override
    public void setResource(HALResource resource) {
        mResource = resource;
        if (resource != null) {
            setListAdapter(new SimpleRepresentationAdapter(getActivity(), resource, getArguments()));
        }
    }

    protected int getFragmentLayoutRes() {
        return getArguments().getInt(ARG_FRAGMENT_LAYOUT);
    }

    protected int getPropertyItemRes(String name) {
        Bundle args = getArguments();
        Bundle map = args.getBundle(ARG_PROPERTY_LAYOUT_MAP);

        if (map.containsKey(name)) return map.getInt(name);
        return args.getInt(ARG_PROPERTY_LAYOUT);
    }

    protected int getLinkItemRes(String rel) {
        Bundle args = getArguments();
        Bundle map = args.getBundle(ARG_LINK_LAYOUT_MAP);

        if (map.containsKey(rel)) return map.getInt(rel);

        return args.getInt(ARG_LINK_LAYOUT);
    }

    protected boolean isViewableRel(String rel) {
        return getArguments().getBundle(ARG_REL_SHOW_MAP).getBoolean(rel, true);
    }

    // *** Fragment lifecycle

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnLinkFollowListener) mLinkListener = (OnLinkFollowListener) activity;

        setResource((HALResource) getArguments().get(ARG_RESOURCE));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getListView().setOnItemClickListener(this);
    }

    // *** View binding

    @Override
    public void bindResource(View view, HALResource resource) {
        bindProperties(view, resource);
        bindLinks(view, resource);
    }

    private void bindProperties(View view, HALResource resource) {
        Map<String, Object> properties = resource.getProperties();

        ViewGroup propertiesLayout = (ViewGroup) view.findViewById(R.id.properties_layout);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (String name : properties.keySet()) {
            View propertyView = getPropertyView(inflater, view, propertiesLayout, resource, name);
            if (propertyView != null) {
                bindPropertyView(propertyView, resource, name, properties.get(name));
                if (propertyView.getParent() == null && propertiesLayout != null) {
                    propertiesLayout.addView(propertyView);
                }
            }
        }

    }

    @Override
    public View getPropertyView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            HALResource resource,
            String name)
    {
        View view = rootView.findViewWithTag(propertyTag(name));
        if (view == null && container != null) {
            view = inflater.inflate(getPropertyItemRes(name), container, false);
        }
        return view;
    }

    @Override
    public void bindPropertyView(View propertyView, HALResource resource, String name, Object value) {
        View childView;

        childView = propertyView.findViewById(R.id.property_name);
        if (childView instanceof TextView) {
            ((TextView) childView).setText(name);
        }

        childView = propertyView.findViewById(R.id.property_value);
        if (childView instanceof TextView) {
            ((TextView) childView).setText(value == null ? "" : value.toString());
        }
    }

    private void bindLinks(View view, HALResource resource) {
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.links_layout);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Links
        for (String rel : resource.getLinkRels()) {
            if (!isViewableRel(rel)) continue;
            for (HALLink link : resource.getLinks(rel)) {
                View linkView = getLinkView(inflater, view, layout, resource, link);
                if (linkView != null) {
                    bindLinkView(linkView, resource, link);
                    if (linkView.getParent() == null && layout != null) {
                        layout.addView(linkView);
                    }
                }
            }
        }

        // Resources
        for (String rel : resource.getResourceRels()) {
            if (!isViewableRel(rel)) continue;
            for (HALResource embeddedResource : resource.getResources(rel)) {
                View resourceView = getResourceView(inflater, view, layout, rel, embeddedResource);
                if (resourceView != null) {
                    bindResourceView(resourceView, rel, embeddedResource);
                    if (resourceView.getParent() == null && layout != null) {
                        layout.addView(resourceView);
                    }
                }
            }
        }
    }

    @Override
    public View getLinkView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            HALResource resource,
            HALLink link)
    {
        String rel = link.getRel();
        View view = rootView.findViewWithTag(linkTag(rel));
        if (view == null && container != null) {
            view = inflater.inflate(getLinkItemRes(rel), container, false);
        }
        return view;
    }

    @Override
    public void bindLinkView(View linkView, HALResource resource, HALLink link) {
        View childView;

        childView = linkView.findViewById(R.id.link_title);
        if (childView instanceof TextView) {
            String title = (String) link.getAttribute("title");
            if (TextUtils.isEmpty(title)) title = link.getRel();
            ((TextView) childView).setText(title);
        }

        linkView.setOnClickListener(this);
        linkView.setTag(R.id.tag_link, link);
    }

    @Override
    public View getResourceView(LayoutInflater inflater,
            View rootView,
            ViewGroup container,
            String rel,
            HALResource embeddedResource)
    {
        View view = getView().findViewWithTag(linkTag(rel));
        if (view == null && container != null) {
            view = inflater.inflate(getLinkItemRes(rel), container, false);
        }
        return view;
    }

    @Override
    public void bindResourceView(View resourceView, String rel, HALResource embeddedResource) {
        View childView;
        HALLink link = embeddedResource.getLink(SELF);

        childView = resourceView.findViewById(R.id.link_title);
        if (childView instanceof TextView) {
            String title = (String) link.getAttribute("title");
            if (TextUtils.isEmpty(title)) title = rel;
            ((TextView) childView).setText(title);
        }

        if (link != null) {
            resourceView.setOnClickListener(this);
            resourceView.setTag(R.id.tag_link, link);
        }

        bindProperties(resourceView, embeddedResource);
        bindLinks(resourceView, embeddedResource);
    }

    // *** View.OnClickListener implementation

    @Override
    public void onClick(View v) {
        if (mLinkListener != null) {
            HALLink link = (HALLink) v.getTag(R.id.tag_link);
            if (link != null) mLinkListener.onFollowLink(link, null);
        }
    }

    // *** AdapterView.OnItemClickListener

    @Override
    public void onItemClick(AdapterView<?> adapterView, View itemView, int pos, long id) {
        if (mLinkListener == null) return;

        Object obj = getListAdapter().getItem(pos);
        if (obj == null) return;

        if (obj instanceof HALLink) {
            mLinkListener.onFollowLink((HALLink) obj, null);
        }
        else if (obj instanceof HALResource) {
            mLinkListener.onFollowLink(((HALResource) obj).getLink(SELF), null);
        }
    }

    // *** Helpers

    protected String propertyTag(String name) {
        return "property:" + name;
    }

    protected String linkTag(String name) {
        return "rel:" + name;
    }

    // ***** Inner classes

    public static class Builder
    {
        private static final String[] BASIC_RELS = { SELF, CURIE, PROFILE };

        private final Bundle mArgs;

        public Builder() {
            Bundle args = new Bundle();
            Bundle subArgs;
            // Set defaults...
            args.putInt(ARG_FRAGMENT_LAYOUT, R.layout.resource_fragment);
            args.putInt(ARG_PROPERTY_LAYOUT, R.layout.property_item);
            args.putBundle(ARG_PROPERTY_LAYOUT_MAP, new Bundle());
            args.putInt(ARG_LINK_LAYOUT, R.layout.link_item);
            args.putBundle(ARG_LINK_LAYOUT_MAP, new Bundle());

            subArgs = new Bundle();
            for (String rel : BASIC_RELS) {
                subArgs.putBoolean(rel, false);
            }
            args.putBundle(ARG_REL_SHOW_MAP, subArgs);

            mArgs = args;
        }

        public Builder setResource(HALResource resource) {
            mArgs.putParcelable(ARG_RESOURCE, resource);
            return this;
        }

        public Builder setFragmentView(int resId) {
            mArgs.putInt(ARG_FRAGMENT_LAYOUT, resId);
            return this;
        }

        public Builder setDefaultPropertyView(int resId) {
            mArgs.putInt(ARG_PROPERTY_LAYOUT, resId);
            return this;
        }

        public Builder setPropertyView(String name, int resId) {
            mArgs.getBundle(ARG_PROPERTY_LAYOUT_MAP).putInt(name, resId);
            return this;
        }

        public Builder setDefaultLinkView(int resId) {
            mArgs.putInt(ARG_LINK_LAYOUT, resId);
            return this;
        }

        public Builder setLinkView(String rel, int resId) {
            mArgs.getBundle(ARG_LINK_LAYOUT_MAP).putInt(rel, resId);
            return this;
        }

        public Builder showBasicRels(boolean show) {
            Bundle showMap = mArgs.getBundle(ARG_REL_SHOW_MAP);
            for (String rel : BASIC_RELS) {
                showMap.putBoolean(rel, show);
            }
            return this;
        }

        public Builder showRel(String rel, boolean show) {
            mArgs.getBundle(ARG_REL_SHOW_MAP).putBoolean(rel, show);
            return this;
        }

        public <T extends BaseResourceFragment> T buildFragment(Class<T> klass) {
            T fragment;
            try {
                fragment = klass.newInstance();
            }
            catch (java.lang.InstantiationException e) {
                throw new IllegalArgumentException(klass.getName() + " must implement the zero-argument contructor.");
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException(klass.getName() + " must have a public zero-argument contructor.");
            }

            fragment.setArguments(mArgs);
            return fragment;
        }

        public Bundle buildArguments() {
            return new Bundle(mArgs);
        }
    }

}
