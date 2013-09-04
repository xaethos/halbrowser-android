package net.xaethos.android.halbrowser.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xaethos.android.halbrowser.profile.ElementConfiguration;

public abstract class ElementBinder<E, C extends ElementConfiguration> {
    public final E element;
    public final C config;

    public ElementBinder(E element, C config) {
        if (element == null) throw new IllegalArgumentException("element must not be null");
        if (config == null) throw new IllegalArgumentException("configuration must not be null");
        this.element = element;
        this.config = config;
    }

    public ViewGroup findContainerView(View root) {
        int containerId = config.getContainerId();
        if (containerId == 0) return null;

        View container = root.findViewById(containerId);
        if (container instanceof ViewGroup) return (ViewGroup) container;

        throw new IllegalArgumentException("Container must be a ViewGroup");
    }

    public View inflateLayout(LayoutInflater inflater, ViewGroup parent) {
        int layoutRes = config.getLayoutRes();
        if (layoutRes == 0) return null;

        View layout = inflater.inflate(layoutRes, parent, false);
        if (layout == null) throw new RuntimeException("Couldn't inflate layout");

        return layout;
    }

    public abstract void bindView(View view);

    protected TextView findTextView(View root, int viewId) {
        if (root == null || viewId == 0) return null;
        View view = root.findViewById(viewId);
        if (view != null && view instanceof TextView) return (TextView) view;
        return null;
    }

}
