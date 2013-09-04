package net.xaethos.android.halbrowser.fragment;

import android.view.View;
import android.widget.TextView;

import net.xaethos.android.halbrowser.profile.LinkConfiguration;
import net.xaethos.android.halparser.HALLink;

public class LinkBinder extends ElementBinder<HALLink, LinkConfiguration> {

    public LinkBinder(HALLink link, LinkConfiguration config) {
        super(link, config);
    }

    @Override
    public void bindView(View view) {
        TextView label = findTextView(view, config.getLabelId());

        if (label != null) {
            String title = element.getTitle();
            label.setText(title == null ? element.getRel() : title);
        }
    }
}
