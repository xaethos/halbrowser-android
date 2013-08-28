package net.xaethos.android.halbrowser.profile;

import net.xaethos.android.halparser.HALProperty;

public interface PropertyConfiguration extends ElementConfiguration<HALProperty> {

    public String getName();

    public int getLabelId();
    public int getContentId();

}
