package net.xaethos.android.halbrowser.fragment;

import android.view.View;
import android.widget.TextView;

import net.xaethos.android.halbrowser.profile.PropertyConfiguration;
import net.xaethos.android.halparser.HALProperty;

public class PropertyBinder extends ElementBinder<HALProperty, PropertyConfiguration> {

    public PropertyBinder(HALProperty property, PropertyConfiguration config) {
        super(property, config);
    }

    @Override
    public void bindView(View view) {
        TextView tv;

        if ((tv = findTextView(view, config.getLabelId())) != null) {
            tv.setText(element.getName());
        }

        if ((tv = findTextView(view, config.getContentId())) != null) {
            tv.setText(element.getValueString());
        }
    }
}
