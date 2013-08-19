package net.xaethos.android.halbrowser.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

public abstract class BaseResourceFragment extends Fragment
{
    // ***** Constants

    // *** Argument/State keys
    protected static final String ARG_RESOURCE = "xae:resource";
//    protected static final String ARG_FRAGMENT_LAYOUT = "fragment_layout";
//    protected static final String ARG_PROPERTY_LAYOUT = "property_layout";
//    protected static final String ARG_LINK_LAYOUT = "link_layout";
//    protected static final String ARG_PROPERTY_LAYOUT_MAP = "property_layout_map";
//    protected static final String ARG_LINK_LAYOUT_MAP = "link_layout_map";
//    protected static final String ARG_REL_SHOW_MAP = "rel_show_map";

    // ***** Instance fields

    private HALResource mResource;
    protected OnLinkFollowListener mLinkListener;

    // ***** Instance methods

    public HALResource getResource() {
        return mResource;
    }

    public void setResource(HALResource resource) {
        mResource = resource;
        if (resource != null) bindResource(resource);
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
        HALResource resource = getResource();
        if (resource != null) bindResource(resource);
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

    protected void bindResource(HALResource resource) {
        View view = getView();
        if (view == null) return;

        for (HALProperty property : resource.getProperties()) {
            onHandleProperty(resource, property);
        }

    }

    protected abstract boolean onHandleProperty(HALResource resource, HALProperty property);

    // *** View.OnClickListener implementation

//    @Override
//    public void onClick(View v) {
//        if (mLinkListener != null) {
//            HALLink link = (HALLink) v.getTag(R.id.tag_link);
//            if (link != null) mLinkListener.onFollowLink(link, null);
//        }
//    }

    // *** AdapterView.OnItemClickListener

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View itemView, int pos, long id) {
//        if (mLinkListener == null) return;
//
//        Object obj = getListAdapter().getItem(pos);
//        if (obj == null) return;
//
//        if (obj instanceof HALLink) {
//            mLinkListener.onFollowLink((HALLink) obj, null);
//        }
//        else if (obj instanceof HALResource) {
//            mLinkListener.onFollowLink(((HALResource) obj).getLink(SELF), null);
//        }
//    }

    // *** Helpers

//    protected String propertyTag(String name) {
//        return "property:" + name;
//    }
//
//    protected String linkTag(String name) {
//        return "rel:" + name;
//    }

    // ***** Inner classes

//    public static class Builder
//    {
//        private static final String[] BASIC_RELS = { SELF, CURIE, PROFILE };
//
//        private final Bundle mArgs;
//
//        public Builder() {
//            Bundle args = new Bundle();
//            Bundle subArgs;
//            // Set defaults...
//            args.putInt(ARG_FRAGMENT_LAYOUT, R.layout.resource_fragment);
//            args.putInt(ARG_PROPERTY_LAYOUT, R.layout.property_item);
//            args.putBundle(ARG_PROPERTY_LAYOUT_MAP, new Bundle());
//            args.putInt(ARG_LINK_LAYOUT, R.layout.link_item);
//            args.putBundle(ARG_LINK_LAYOUT_MAP, new Bundle());
//
//            subArgs = new Bundle();
//            for (String rel : BASIC_RELS) {
//                subArgs.putBoolean(rel, false);
//            }
//            args.putBundle(ARG_REL_SHOW_MAP, subArgs);
//
//            mArgs = args;
//        }
//
//        public Builder setResource(HALResource resource) {
//            mArgs.putParcelable(ARG_RESOURCE, resource);
//            return this;
//        }
//
//        public Builder setFragmentView(int resId) {
//            mArgs.putInt(ARG_FRAGMENT_LAYOUT, resId);
//            return this;
//        }
//
//        public Builder setDefaultPropertyView(int resId) {
//            mArgs.putInt(ARG_PROPERTY_LAYOUT, resId);
//            return this;
//        }
//
//        public Builder setPropertyView(String name, int resId) {
//            mArgs.getBundle(ARG_PROPERTY_LAYOUT_MAP).putInt(name, resId);
//            return this;
//        }
//
//        public Builder setDefaultLinkView(int resId) {
//            mArgs.putInt(ARG_LINK_LAYOUT, resId);
//            return this;
//        }
//
//        public Builder setLinkView(String rel, int resId) {
//            mArgs.getBundle(ARG_LINK_LAYOUT_MAP).putInt(rel, resId);
//            return this;
//        }
//
//        public Builder showBasicRels(boolean show) {
//            Bundle showMap = mArgs.getBundle(ARG_REL_SHOW_MAP);
//            for (String rel : BASIC_RELS) {
//                showMap.putBoolean(rel, show);
//            }
//            return this;
//        }
//
//        public Builder showRel(String rel, boolean show) {
//            mArgs.getBundle(ARG_REL_SHOW_MAP).putBoolean(rel, show);
//            return this;
//        }
//
//        public <T extends BaseResourceFragment> T buildFragment(Class<T> klass) {
//            T fragment;
//            try {
//                fragment = klass.newInstance();
//            }
//            catch (java.lang.InstantiationException e) {
//                throw new IllegalArgumentException(klass.getName() + " must implement the zero-argument constructor.");
//            }
//            catch (IllegalAccessException e) {
//                throw new IllegalArgumentException(klass.getName() + " must have a public zero-argument constructor.");
//            }
//
//            fragment.setArguments(mArgs);
//            return fragment;
//        }
//
//        public Bundle buildArguments() {
//            return new Bundle(mArgs);
//        }
//    }

}
