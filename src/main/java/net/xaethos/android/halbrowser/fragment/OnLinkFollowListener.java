package net.xaethos.android.halbrowser.fragment;

import net.xaethos.android.halparser.HALLink;

import java.util.Map;

public interface OnLinkFollowListener
{
    void onFollowLink(HALLink link, Map<String, Object> map);
}
