package net.xaethos.android.halbrowser.profile;

import java.util.Collection;

public interface ResourceConfiguration {

    public int getLayoutRes();

    boolean hasPropertyConfiguration(String name);
    PropertyConfiguration getPropertyConfiguration(String name);
    Collection<PropertyConfiguration> getPropertyConfigurations();
}
