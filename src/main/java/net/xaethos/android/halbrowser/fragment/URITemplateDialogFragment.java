package net.xaethos.android.halbrowser.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.xaethos.android.halparser.HALLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class URITemplateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{

    private static final String ARG_LINK = "link";

    private LinearLayout mLayout;
    private ResourceFragment.OnLinkFollowListener mListener;

    public static URITemplateDialogFragment forLink(HALLink link) {
        Bundle args = new Bundle(1);
        args.putParcelable(ARG_LINK, link);
        URITemplateDialogFragment fragment = new URITemplateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ResourceFragment.OnLinkFollowListener)) {
            throw new IllegalArgumentException("Attaching activity must implement ResourceFragment.OnLinkFollowListener");
        }
        mListener = (ResourceFragment.OnLinkFollowListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        HALLink link = getArguments().getParcelable(ARG_LINK);

        String title = link.getTitle();
        if (TextUtils.isEmpty(title)) title = link.getRel();

        return new AlertDialog.Builder(getActivity()).setTitle(title)
                                                     .setView(getContentView(link.getVariables()))
                                                     .setPositiveButton(android.R.string.ok, this)
                                                     .setCancelable(true)
                                                     .create();
    }

    private View getContentView(Set<String> variables) {
        if (mLayout == null) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            for (String variable : variables) {
                EditText et = new EditText(getActivity());
                et.setHint(variable);
                et.setTag(variable);
                layout.addView(et);
            }
            mLayout = layout;
        }
        return mLayout;
    }

    // *** DialogInterface.OnClickListener

    @Override
    public void onClick(DialogInterface dialog, int which) {
        LinearLayout layout = mLayout;
        HALLink link = getArguments().getParcelable(ARG_LINK);
        Map<String, Object> map = new HashMap<String, Object>();

        for (String variable : link.getVariables()) {
            EditText et = (EditText) layout.findViewWithTag(variable);
            map.put(variable, et.getText().toString());
        }

        mListener.onFollowLink(link, map);
    }

}
