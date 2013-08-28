package net.xaethos.android.halbrowser.fragment;

import android.view.View;

import net.xaethos.android.halbrowser.profile.ResourceConfiguration;
import net.xaethos.android.halparser.HALResource;

public class ResourceBinder extends ElementBinder<HALResource, ResourceConfiguration> {

    public ResourceBinder(HALResource resource, ResourceConfiguration config) {
        super(resource, config);
    }

    @Override
    public void bindView(View view) {
    }
}
