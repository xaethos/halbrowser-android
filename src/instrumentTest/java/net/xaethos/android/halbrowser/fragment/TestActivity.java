package net.xaethos.android.halbrowser.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;

import net.xaethos.android.halparser.HALLink;

import java.util.Map;

public class TestActivity extends FragmentActivity implements OnLinkFollowListener
{
    public static final int FRAGMENT_ID = android.R.id.content;

    Pair<HALLink, Map<String, Object>> lastFollowed;

    public Fragment getFragment() {
        return getSupportFragmentManager().findFragmentById(FRAGMENT_ID);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getFragment() == null) {
            transaction.add(FRAGMENT_ID, fragment);
        } else {
            transaction.replace(FRAGMENT_ID, fragment);
        }
        transaction.commit();
    }

    @Override
    public void onFollowLink(HALLink link, Map<String, Object> map) {
        lastFollowed = new Pair<HALLink, Map<String, Object>>(link, map);
    }

}
