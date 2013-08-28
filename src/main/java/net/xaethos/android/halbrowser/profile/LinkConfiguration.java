package net.xaethos.android.halbrowser.profile;

import net.xaethos.android.halparser.HALLink;

public interface LinkConfiguration extends ElementConfiguration<HALLink> {

    public String getRel();
    public String getName();

    public int getLabelId();

}
