package net.xaethos.android.halbrowser.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ElementConfiguration<E> {

    public int getLayoutRes();
    public int getContainerId();

    public ViewGroup findContainerView(View root);
    public View inflateLayout(LayoutInflater inflater, ViewGroup parent);

    public void bindView(View view, E element);

}
