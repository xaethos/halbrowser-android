package net.xaethos.android.halbrowser.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;

import net.xaethos.android.halparser.HALLink;

import java.util.Map;

public class TestActivity extends FragmentActivity implements OnLinkFollowListener
{
    Pair<HALLink, Map<String, Object>> lastFollowed;

    public Fragment getFragment() {
        return getSupportFragmentManager().findFragmentById(android.R.id.content);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getFragment() == null) {
            transaction.add(android.R.id.content, fragment);
        } else {
            transaction.replace(android.R.id.content, fragment);
        }
        transaction.commit();
    }

    @Override
    public void onFollowLink(HALLink link, Map<String, Object> map) {
        lastFollowed = new Pair<HALLink, Map<String, Object>>(link, map);
    }

}
