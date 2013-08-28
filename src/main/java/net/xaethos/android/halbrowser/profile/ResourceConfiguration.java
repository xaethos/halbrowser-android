package net.xaethos.android.halbrowser.profile;

import net.xaethos.android.halparser.HALResource;

public interface ResourceConfiguration extends ElementConfiguration<HALResource> {

    public String getRel();

    PropertyConfiguration getPropertyConfiguration(String name);
    PropertyConfiguration getDefaultPropertyConfiguration();

    LinkConfiguration getLinkConfiguration(String rel, String name);
    LinkConfiguration getDefaultLinkConfiguration();

    ResourceConfiguration getResourceConfiguration(String rel);
    ResourceConfiguration getDefaultResourceConfiguration();

}
