package net.xaethos.android.halbrowser.profile;

import java.util.Collection;

public interface ResourceConfiguration extends ElementConfiguration {

    public String getRel();

    boolean hasPropertyConfiguration(String name);
    PropertyConfiguration getPropertyConfiguration(String name);
    Collection<PropertyConfiguration> getPropertyConfigurations();
    PropertyConfiguration getDefaultPropertyConfiguration();

    LinkConfiguration getLinkConfiguration(String rel, String name);
    LinkConfiguration getDefaultLinkConfiguration();

    ResourceConfiguration getResourceConfiguration(String rel);
    ResourceConfiguration getDefaultResourceConfiguration();

}
