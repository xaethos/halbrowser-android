package net.xaethos.android.halbrowser.profile;

public interface ResourceConfiguration extends ElementConfiguration {

    public String getRel();

    PropertyConfiguration getPropertyConfiguration(String name);
    PropertyConfiguration getDefaultPropertyConfiguration();

    LinkConfiguration getLinkConfiguration(String rel, String name);
    LinkConfiguration getDefaultLinkConfiguration();

    ResourceConfiguration getResourceConfiguration(String rel);
    ResourceConfiguration getDefaultResourceConfiguration();

}
