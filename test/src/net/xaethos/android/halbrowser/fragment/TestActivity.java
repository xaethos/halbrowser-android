package net.xaethos.android.halbrowser.fragment;

import java.util.Map;

import net.xaethos.android.halparser.HALLink;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class TestActivity extends FragmentActivity implements ResourceFragment.OnLinkFollowListener
{
    HALLink lastFollowedLink;

    public void loadFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();

        if (manager.findFragmentById(android.R.id.content) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(android.R.id.content, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onFollowLink(HALLink link, Map<String, Object> map) {
        lastFollowedLink = link;
    }

}
